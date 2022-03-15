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

public class GameHandlerTest {
	private GameHandler handler;

	@Test
	@BeforeEach
	@Order(2)
	public void constructor() {
		BattleshipGame game = BattleshipGameTest.testGame;
		handler = new GameHandler(game);
		assertEquals(handler.game, game);
	}

	@Test
	public void test_json_handling() {
		JSONArray array = new JSONArray("[\"test\", 1, 50, {\"lul\":-42}]");
		assertTrue(GameHandler.contains(array, "test"));
		assertFalse(GameHandler.contains(array, "yuiop"));

		JSONObject obj = GameHandler.decodeRequest(new HttpExchangeMock(MockUtils.inputStreamOf("{\"test\":\"lol\", \"aze\":1}")));
		assertEquals(obj.optString("test"), "lol");
		assertEquals(obj.optString("aze"), "1");
		assertNull(GameHandler.decodeRequest(new HttpExchangeMock(MockUtils.inputStreamOf("\"{}{}didi\"eeeeeeeeeeeee"))));
	}
	
	@Test
	public void test_url_decoding() {
		Map<String, String> pairs = GameHandler.getQueryMap("a=aBc123&watch=rickroll");
		assertTrue(pairs.containsKey("a") && pairs.containsKey("watch"));
		assertEquals(pairs.get("a"), "aBc123");
		assertEquals(pairs.get("watch"), "rickroll");
	}
}
