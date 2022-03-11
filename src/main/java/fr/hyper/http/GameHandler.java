package fr.hyper.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import fr.hyper.battleship.AttackResult;
import fr.hyper.battleship.BattleshipGame;

public class GameHandler implements HttpHandler {
	/**
	 * These messages are maximum cringe
	 */
	public static final String[] START_MESSAGE = {"Get owned n00b", "Your fleet will exit this earth's atomosphere",
			"ff?", "Why sink your boats when you've already hit rock bottom?", "ESM pow !"};
	private final UUID id = UUID.randomUUID();

	public final BattleshipGame game;

	public GameHandler(BattleshipGame game) {
		this.game = game;
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
		AttackResult result = game.getAttacked(cell.toUpperCase().charAt(0) - 'A', Integer.valueOf(cell.substring(1)) - 1);
		JSONObject answer = new JSONObject();
		answer.put("consequence", result.toString());
		answer.put("shipLeft", !game.hasLost());
		return answer;
	}

	private JSONObject startRequest(HttpExchange exchange) {
		if (!exchange.getRequestMethod().contentEquals("POST"))
			return null;
		JSONObject answer = new JSONObject();
		InetSocketAddress addr = exchange.getHttpContext()
				.getServer().getAddress();
		answer.put("id", id.toString());
		int rand = (int) (Math.random() * START_MESSAGE.length);
		answer.put("message", START_MESSAGE[rand]);
		answer.put("url", "[" + addr.getHostString() + "]:" + addr.getPort());
		return answer;
	}

	public void sendStartRequest(HttpClient client, String adversaryURL, String myURL) {
		JSONObject answer = new JSONObject();
		answer.put("id", id.toString());
		int rand = (int) (Math.random() * START_MESSAGE.length);
		answer.put("message", START_MESSAGE[rand]);
		answer.put("url", myURL);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(adversaryURL + "/api/game/start"))
				.setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(answer.toString()))
				.build();
		client.sendAsync(request, responseInfo -> {return null;});
	}

	public void sendShootRequest(HttpClient client, String adversaryURL, int x, int y) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(adversaryURL + "/api/game/fire?cell=" +
						(x + "A") + (y + 1)))
				.setHeader("Accept", "application/json")
				.setHeader("Content-Type", "application/json")
				.GET()
				.build();
		client.sendAsync(request, responseInfo -> {return null;});
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject request = null;
		if(exchange.getRequestBody().available() > 0)
			decodeRequest(exchange);
		String uri = exchange.getRequestURI().getPath().replace("api/game/", "");
		if(uri.contains("?"))
			uri = uri.split("?")[0];
		JSONObject answer = answer(exchange, request, uri);
		if(answer != null) {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, answer.toString().length());
			exchange.getResponseBody().write(answer.toString().getBytes());
		} else {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 13);
			exchange.getResponseBody().write("Bad request !".getBytes());
		}
		exchange.close();
	}

	private JSONObject answer(HttpExchange exchange, JSONObject request, String uri) throws IOException {
		switch(uri) {
			case "/start":
				return startRequest(exchange);
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

}
