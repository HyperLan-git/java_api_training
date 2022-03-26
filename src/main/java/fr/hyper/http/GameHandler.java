package fr.hyper.http;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.hyper.battleship.AttackResult;
import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.Player;

public class GameHandler implements HttpHandler {
	/**
	 * These messages are maximum cringe
	 */
	public static final String[] START_MESSAGE = {"Get owned n00b", "Your fleet will exit this earth's atomosphere",
			"ff?", "Why sink your boats when you've already hit rock bottom?", "ESM pow !"};
	private final UUID id = UUID.randomUUID();

	public final BattleshipGame game;

	public final Player player;

	private HttpClient client = HttpClient.newHttpClient();
	private final AtomicReference<String> url = new AtomicReference<>();

	public GameHandler(BattleshipGame game, Player p) {
		this.game = game;
		this.player = p;
	}

	public static final boolean contains(JSONArray arr, CharSequence str) {
		for(Object obj : arr) {
			if(obj instanceof String)
				if (((String)obj).contentEquals(str))
					return true;
		}
		return false;
	}

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.contains("&") ? query.split("&") : new String[] {query};
		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {
			String[] p = param.split("="); 
			map.put(p[0], p[1]);  
		}
		return map;  
	}

	public static final JSONObject decodeRequest(HttpExchange exchange) {
		JSONObject request = null;
		InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
		try (StringWriter str = new StringWriter()) {
			reader.transferTo(str);
			reader.close();
			System.out.println("Reading : " + str.toString());

			if(!str.getBuffer().isEmpty())
				request = new JSONObject(str.toString());
		} catch (Exception e) {
			return null;
		}
		return request;
	}

	private JSONObject shootRequest(HttpExchange exchange, JSONObject request) {
		if (!exchange.getRequestMethod().contentEquals("GET") ||
				exchange.getRequestURI().toString().lastIndexOf('?') == -1)
			return null;
		Map<String, String> queryMap = getQueryMap(exchange.getRequestURI().toString().split("[?]")[1]);
		if(!queryMap.containsKey("cell"))
			return null;
		String cell = queryMap.get("cell");
		System.out.println("Cell = " + cell);
		AttackResult result = game.getAttacked(new Point(cell.toUpperCase().charAt(0) - 'A' + 1, Integer.valueOf(cell.substring(1))));
		System.out.println("result = " + result);
		JSONObject answer = new JSONObject();
		answer.put("consequence", result.toString());
		answer.put("shipLeft", !game.hasLost());
		System.out.println("shoot consequence = " + answer);
		return answer;
	}

	private JSONObject startRequest(HttpExchange exchange) {
		System.out.println(exchange.getRequestMethod());
		if (!exchange.getRequestMethod().contentEquals("POST"))
			return null;
		this.game.init();
		JSONObject answer = new JSONObject();
		InetSocketAddress addr = exchange.getHttpContext()
				.getServer().getAddress();
		answer.put("id", id.toString());
		int rand = (int) (Math.random() * START_MESSAGE.length);
		answer.put("message", START_MESSAGE[rand]);
		answer.put("url", "[" + addr.getHostString() + "]:" + addr.getPort());
		return answer;
	}

	public void sendStartRequest(String adversaryURL, String myURL) {
		this.url.set(adversaryURL);
		this.game.init();
		JSONObject answer = new JSONObject();
		answer.put("id", id.toString());
		int rand = (int) (Math.random() * START_MESSAGE.length);
		answer.put("message", START_MESSAGE[rand]);
		answer.put("url", myURL);
		System.out.println("Sending start : " + answer.toString());
		System.out.println("url = " + myURL);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + adversaryURL + "/api/game/start"))
				.setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(answer.toString()))
				.build();
		try {
			client.send(request, BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			JSONObject request = null;
			if(exchange.getRequestBody().available() > 0)
				request = decodeRequest(exchange);
			String uri = exchange.getRequestURI().getPath().replace("api/game/", "");
			if(uri.contains("?"))
				uri = uri.split("?")[0];
			JSONObject answer = answer(exchange, request, uri);
			System.out.println("Answering with : " + answer);
			if(answer != null) {
				if(exchange.getResponseCode() == -1)
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
							answer.toString().length());
				exchange.getResponseBody().write(answer.toString().getBytes());
				exchange.close();
			} else {
				exchange.sendResponseHeaders(exchange.getResponseCode() == -1 ? HttpURLConnection.HTTP_OK : exchange.getResponseCode(),
						0);
				exchange.close();
			}
			System.out.println("before");
			if(!game.hasLost()) {
				Point p = this.player.attack(game);
				System.out.println("p = " + p);
				System.out.println("url = " + url.get());
				if(url.get() == null) {
					url.set(request.getString("url"));
				}
				sendShootRequest(url.get(), p.x, p.y);
			} else {
				System.out.println("Lost :/");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject answer(HttpExchange exchange, JSONObject request, String uri) throws IOException {
		JSONObject response = null;
		switch(uri) {
			case "start":
			case "/start":
				System.out.println("Recieved a start request");
				System.out.println(request);
				response = startRequest(exchange);
				if(request.optString("url") == null || response == null) {
					System.out.println("But was invalid");
					OutputStreamWriter writer = new OutputStreamWriter(exchange.getResponseBody());
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 17);
					writer.append("Invalid request !");
					writer.close();
					exchange.close();
					return null;
				}
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_ACCEPTED, response.toString().length());
				return response;
			case "fire":
			case "/fire":
				System.out.println("Recieved fire request");
				response = shootRequest(exchange, request);
				System.out.println("Response : " + response);
				if(this.game.hasLost() || response == null) {
					return response;
				}
				return response;
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
		char c = 'A' - 1;
		c += x;
		System.out.println("Shooting");
		HttpRequest request = HttpRequest.newBuilder()
				.setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json")
				.uri(URI.create("http://" + adversaryURL + "/api/game/fire?cell=" +
						c + y))
				.version(Version.HTTP_1_1)
				.GET()
				.build();
		System.out.println("shoot ready");
		try {
			System.out.println("now waiting for answer");
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
			System.out.println("Shoot response : " + response.body());
			JSONObject obj = new JSONObject(response.body());
			this.game.attacking(new Point(x, y), !"MISS".equals(obj.getString("consequence")));
			Thread.sleep(1);
			if(!obj.getBoolean("shipLeft")) {
				System.out.println("You won !!!");
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Game end myself (no dont do that)");
		}
	}

}
