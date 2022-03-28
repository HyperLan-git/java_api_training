package fr.hyper.battleship;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BattleshipTest {
	@Test
	public void whenOOB_thenThrow() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> {
					new Battleship("test", 50, 0, 0, false);
				});
		assertTrue(e.getMessage()
				.contentEquals("Battleship out of bounds !"));
		e = assertThrows(
				IllegalArgumentException.class,
				() -> {
					new Battleship("test", 1, 0, 0, false);
				});
		assertTrue(e.getMessage()
				.contentEquals("Battleship out of bounds !"));
		e = assertThrows(
				IllegalArgumentException.class,
				() -> {
					new Battleship("test", 2, 1, BattleshipGame.getDefaultSize(), true);
				});
		assertTrue(e.getMessage()
				.contentEquals("Battleship out of bounds !"));
		Battleship ship = new Battleship("test", 5, 1, 1, false);
		e = assertThrows(IllegalArgumentException.class,
				() -> {
					ship.setPosition(-1, 1, true);
				});
		assertTrue(e.getMessage()
				.contentEquals("Battleship out of bounds !"));
	}

	@Test
	public void underAttack() {
		Battleship ship = new Battleship("test", 5, 1, 1, false);
		assertEquals(ship.getName(), "test");
		assertArrayEquals(ship.getPositions(), new int[][] {
			{1, 1},
			{2, 1},
			{3, 1},
			{4, 1},
			{5, 1}
		});

		assertFalse(ship.attack(2, 2));
		assertTrue(ship.attack(2, 1));
		// attack the same tile why
		assertFalse(ship.attack(2, 1));
		assertTrue(ship.alive());

		assertFalse(ship.isAlive(2, 1));
		assertArrayEquals(ship.getAlivePositions(), new int[][] {
			{1, 1},
			{-1, -1},
			{3, 1},
			{4, 1},
			{5, 1}
		});

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, 
				() -> {
					ship.isAlive(0, 0);
				});
		assertTrue(e.getMessage()
				.contentEquals("Not a tile occupied by this boat !"));
		Battleship ship2 = ship.setPosition(1, 2, true);
		assertArrayEquals(ship2.getPositions(), new int[][] {
			{1, 2},
			{1, 3},
			{1, 4},
			{1, 5},
			{1, 6}
		});
		assertTrue(ship2.attack(1, 2));
		assertTrue(ship2.attack(1, 3));
		assertTrue(ship2.attack(1, 4));
		assertTrue(ship2.attack(1, 5));
		assertTrue(ship2.attack(1, 6));
		assertFalse(ship2.alive());
	}

	@Test
	public void equality() {
		Battleship b1 = new Battleship("test", 5, 3, 2, false),
				b2 = new Battleship("test", 5, 3, 2, true);
		assertNotEquals(b1, "test");
		assertNotEquals(b1, b2);
		b2 = b2.setPosition(3, 2, false);
		assertEquals(b1, b2);
	}
}
