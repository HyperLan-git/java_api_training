package fr.hyper.battleship;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.hyper.http.mock.MockUtils;

public class BattleshipGameTest {

	public static BattleshipGame testGame;

	@Test
	@BeforeEach
	public void constructors() {
		System.setIn(MockUtils.inputStreamOf("y\nA12\ny\n\ny\nAA\ny\nA1\ny\nB1\nn\nG5\ny\nA7\nn\nI10\n"));
		BattleshipGame game = new BattleshipGame(new UserGame());
		Battleship[] expected = new Battleship[5];
		expected[0] = new Battleship("Carrier", 5, 1, 1, true);
		expected[1] = new Battleship("Battleship", 4, 2, 1, true);
		expected[2] = new Battleship("Cruiser", 3, 7, 5, false);
		expected[3] = new Battleship("Submarine", 3, 1, 7, true);
		expected[4] = new Battleship("Destroyer", 2, 9, 10, false);
		game.init();
		Battleship[] fleet = game.getFleet();
		assertArrayEquals(fleet, expected);

		fleet = new Battleship[] {
				new Battleship("lonely", 3, 5, 7, true)
		};
		testGame = new BattleshipGame(fleet);
		testGame.init(); // Does nothing
		assertArrayEquals(testGame.getFleet(), fleet);
		game = new BattleshipGame();
		game.init();
	}
	
	@Test
	public void getAttackedAndLose() { // Me sad when lose :(
		assertEquals(testGame.getShipsAlive(), 1);
		assertFalse(testGame.myTurn.get());
		assertEquals(testGame.getAttacked(new Point(1, 1)), AttackResult.MISS);
		assertTrue(testGame.myTurn.get());
		assertEquals(testGame.getAttacked(new Point(5, 7)), AttackResult.HIT);
		assertEquals(testGame.getAttacked(new Point(5, 8)), AttackResult.HIT);
		assertFalse(testGame.hasLost()); // happy (hardcore)
		assertEquals(testGame.getAttacked(new Point(5, 9)), AttackResult.SINK);
		assertTrue(testGame.hasLost()); // sadge
		assertEquals(testGame.getShipsAlive(), 0);
	}

	@Test
	public void testRadar() {
		assertEquals(testGame.getRadar().size(), 0);
		testGame.attacking(new Point(1, 1), true);
		assertEquals(testGame.getRadar().size(), 1);
		assertEquals(testGame.getRadar().get(new Point(1, 1)), true);
	}
}
