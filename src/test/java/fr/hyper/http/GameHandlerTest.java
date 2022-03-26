package fr.hyper.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.BattleshipGameTest;
import fr.hyper.http.mock.HttpExchangeMock;
import fr.hyper.http.mock.MockUtils;
import fr.hyper.http.mock.PlayerMock;

public class GameHandlerTest {
	private GameHandler handler;
	private PlayerMock player;

	@Test
	@BeforeEach
	@Order(1)
	public void constructor() {
		BattleshipGame game = BattleshipGameTest.testGame;
		player = new PlayerMock();
		handler = new GameHandler(game, player);
		assertEquals(handler.game, game);
		assertFalse(handler.done());
	}

	@Test
	public void test_json_handling() {
		JSONArray array = new JSONArray("[\"test\", 1, 50, {\"lul\":-42}]");
		assertTrue(GameHandler.contains(array, "test"));
		assertFalse(GameHandler.contains(array, "yuiop"));

		JSONObject obj = GameHandler.decodeRequest(new HttpExchangeMock(MockUtils.inputStreamOf("{\"test\":\"lol\", \"aze\":1}"), "GET"));
		assertEquals(obj.optString("test"), "lol");
		assertEquals(obj.optString("aze"), "1");
		assertNull(GameHandler.decodeRequest(new HttpExchangeMock(MockUtils.inputStreamOf("\"{}{}didi\"eeeeeeeeeeeee"), "GET")));
	}
	
	@Test
	public void test_url_decoding() {
		Map<String, String> pairs = GameHandler.getQueryMap("a=aBc123&watch=dQw4w9WgXcQ");
		assertTrue(pairs.containsKey("a") && pairs.containsKey("watch"));
		assertEquals(pairs.get("a"), "aBc123");
		assertEquals(pairs.get("watch"), "dQw4w9WgXcQ");
	}
	
	@Test
	@Order(3)
	public void test_start_request() {
		try {
			HttpExchangeMock mock = new HttpExchangeMock(
					MockUtils.inputStreamOf("{\"id\":\"testid\",\"message\":\"hello\",\"url\":\"testurl\"}"),
					"GET", "api/game/start");
			handler.handle(mock);
			mock = new HttpExchangeMock(
					MockUtils.inputStreamOf("{\"id\":\"testid\",\"message\":\"hello\",\"url\":\"testurl\"}"),
					"POST", "api/game/start");
			handler.handle(mock);
			JSONObject obj = new JSONObject(mock.getResponse());
			assertTrue(obj.optString("id") != null && obj.optString("message") != null && obj.optString("url") != null);
		} catch (Exception e) {
			throw new IllegalStateException("Exception !", e);
		}
	}
}
