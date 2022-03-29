package fr.hyper.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.hyper.battleship.Battleship;
import fr.hyper.battleship.BattleshipGame;
import fr.hyper.http.mock.HttpExchangeMock;
import fr.hyper.http.mock.MockUtils;
import fr.hyper.http.mock.PlayerMock;

public class GameHandlerTest {
	private GameHandler handler;
	private PlayerMock player;

	@Test
	@BeforeEach
	public void constructor() {
		Battleship[] fleet = new Battleship[] {
				new Battleship("lonely", 3, 5, 7, true)
		};
		BattleshipGame game = new BattleshipGame(fleet);
		player = new PlayerMock();
		player.playNext(new Point(1, 1));
		handler = new GameHandler(game, player);
		assertEquals(handler.game, game);
		assertFalse(handler.done.get());
	}

	@Test
	public void test_json_handling() {
		JSONArray array = new JSONArray("[\"test\", 1, 50, {\"lul\":-42}]");
		assertTrue(APIUtils.contains(array, "test"));
		assertFalse(APIUtils.contains(array, "yuiop"));

		JSONObject obj = APIUtils.decodeRequest(MockUtils.inputStreamOf("{\"test\":\"lol\", \"aze\":1}"));
		assertEquals(obj.optString("test"), "lol");
		assertEquals(obj.optString("aze"), "1");
		assertNull(APIUtils.decodeRequest(MockUtils.inputStreamOf("\"{}{}didi\"eeeeeeeeeeeee")));
	}
	
	@Test
	public void test_url_decoding() {
		Map<String, String> pairs = APIUtils.getQueryMap("a=aBc123&watch=dQw4w9WgXcQ");
		assertTrue(pairs.containsKey("a") && pairs.containsKey("watch"));
		assertEquals(pairs.get("a"), "aBc123");
		assertEquals(pairs.get("watch"), "dQw4w9WgXcQ");
	}

	@Test
	public void test_start_request() {
		try {
			HttpExchangeMock mock = new HttpExchangeMock(
					MockUtils.inputStreamOf("{\"id\":\"testid\",\"message\":\"hello\",\"url\":\"http://testurl\"}"),
					"GET", "api/game/start");
			handler.sendStartRequest("http://testurl", "http://test");
			handler.handle(mock);
			mock = new HttpExchangeMock(
					MockUtils.inputStreamOf("{\"id\":\"testid\",\"message\":\"hello\",\"url\":\"http://testurl\"}"),
					"POST", "api/game/start");
			handler.handle(mock);
			JSONObject obj = new JSONObject(mock.getResponse());
			assertTrue(obj.optString("id") != null && obj.optString("message") != null && obj.optString("url") != null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Exception !", e);
		}
	}
}
