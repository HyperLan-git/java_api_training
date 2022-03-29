package fr.hyper.http;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.hyper.battleship.AttackResult;
import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.Player;

public class GameHandler implements HttpHandler {
	public final String[] START_MESSAGE = {"Get owned n00b", "Your fleet will exit this earth's atomosphere", "ff?", "Why sink your boats when you've already hit rock bottom?", "ESM pow !"};
	private final UUID id = UUID.randomUUID();
	public final BattleshipGame game;
	public final Player player;
	public final AtomicBoolean done = new AtomicBoolean();
	private final HttpClient client = HttpClient.newHttpClient();
	private final AtomicReference<String> url = new AtomicReference<>();

	public GameHandler(BattleshipGame game, Player p) {
		this.game = game;
		this.player = p;
	}

	private JSONObject shootRequest(HttpExchange exchange, JSONObject request) {
		if (!exchange.getRequestMethod().contentEquals("GET") ||
				exchange.getRequestURI().toString().lastIndexOf('?') == -1) return null;
		String cell = APIUtils.getQueryMap(exchange.getRequestURI().toString().split("[?]")[1]).get("cell");
		if (cell == null) return null;
		exchange.getResponseHeaders().add("Content-type", "application/json");
		AttackResult result = game.getAttacked(new Point(cell.toUpperCase().charAt(0) - 'A' + 1, Integer.valueOf(cell.substring(1))));
		return new JSONObject().put("consequence", result.toString()).put("shipLeft", !game.hasLost());
	}

	private JSONObject startRequest(HttpExchange exchange, JSONObject request) {
		if (!exchange.getRequestMethod().contentEquals("POST")) return null;
		exchange.getResponseHeaders().add("Content-type", "application/json");
		if (request == null) this.url.set("http://" + exchange.getRemoteAddress().toString().split("/")[1]);
		this.done.set(false);
		this.game.init();
		InetSocketAddress addr = exchange.getHttpContext().getServer().getAddress();
		JSONObject answer = new JSONObject().put("id", id.toString()).put("message", START_MESSAGE[(int) (Math.random() * START_MESSAGE.length)])
				.put("url", "http://[" + addr.getHostString() + "]:" + addr.getPort());
		return answer;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject request = null;
		if(exchange.getRequestBody().available() > 0) request = APIUtils.decodeRequest(exchange.getRequestBody());
		String uri = exchange.getRequestURI().getPath().replace("api/game/", "");
		if(uri.contains("?")) uri = uri.split("?")[0];
		JSONObject answer = answer(exchange, request, uri);
		if(answer != null) {
			if(exchange.getResponseCode() == -1) exchange.sendResponseHeaders(HttpURLConnection.HTTP_ACCEPTED, answer.toString().length());
			exchange.getResponseBody().write(answer.toString().getBytes());
		} else exchange.sendResponseHeaders(exchange.getResponseCode() == -1 ? HttpURLConnection.HTTP_OK : exchange.getResponseCode(), 0);
		exchange.close();
		answer(request);
	}
	
	private void answer(JSONObject request) {
		if(!game.hasLost()) {
			Point p = this.player.attack(game);
			while (url.get() == null) url.set(request.getString("url"));
			sendShootRequest(url.get(), p.x, p.y);
		} else {
			System.out.println("Lost :/");
			while(!done.get()) done.set(true);
		}
	}

	private JSONObject answer(HttpExchange exchange, JSONObject request, String uri) throws IOException {
		switch(uri) {
			case "start":
			case "/start":
				return startRequest(exchange, request);
			case "fire":
			case "/fire":
				return shootRequest(exchange, request);
			default:
				OutputStreamWriter writer = new OutputStreamWriter(exchange.getResponseBody());
				writer.append("Resource not found :(");
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 21);
				writer.close();
				exchange.close();
				return null;
		}
	}

	public void sendShootRequest(String adversaryURL, int x, int y) {
		char c = (char) (('A' - 1) + x);
		System.out.println("uri = " + URI.create(adversaryURL + "/api/game/fire?cell=" + c + "" + y));
		HttpRequest request = HttpRequest.newBuilder().setHeader("Accept", "application/json").setHeader("Content-Type", "application/json")
				.uri(URI.create(adversaryURL + "/api/game/fire?cell=" + c + "" + y)).version(Version.HTTP_1_1).GET().build();
		client.sendAsync(request, BodyHandlers.ofString()).thenAccept((response) -> {
			JSONObject obj = new JSONObject(response.body());
			this.game.attacking(new Point(x, y), !"MISS".equals(obj.getString("consequence")));
			if(!obj.getBoolean("shipLeft")) {
				System.out.println("You won !!!");
				while(!done.get()) done.set(true);
			}
		});
	}

	public void sendStartRequest(String adversaryURL, String myURL) {
		this.done.set(false);
		this.game.init();
		JSONObject answer = new JSONObject().put("id", id.toString()).put("message", START_MESSAGE[(int) (Math.random() * START_MESSAGE.length)])
				.put("url", myURL);
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(adversaryURL + "/api/game/start")).setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json").POST(BodyPublishers.ofString(answer.toString())).build();
		client.sendAsync(request, BodyHandlers.ofString(Charset.forName("UTF-8"))).thenAccept(
				(response) -> {while (this.url.get() == null) this.url.set(new JSONObject(response.body()).getString("url"));}
				);
	}
}
