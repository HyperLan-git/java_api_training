package fr.hyper.battleship;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class BattleshipGame {

	public static final int SIZE = 10;

	private final Map<Point, Boolean> radar = new HashMap<>();

	private Battleship[] fleet = null;

	private BattleshipProvider provider = new RandomGame();

	public boolean myTurn = false;

	public BattleshipGame() {}

	public BattleshipGame(BattleshipProvider provider) {
		this.provider = provider;
	}

	public BattleshipGame(Battleship[] fleet) {
		this.fleet = fleet;
	}

	public void attacking(int x, int y, boolean hit) {
		myTurn = false;
		radar.put(new Point(x, y), hit);
	}

	public Battleship attacked(int x, int y) {
		for(Battleship ship : fleet) {
			if(ship.alive() && ship.attack(x, y))
				return ship;
		}
		return null;
	}

	public AttackResult getAttacked(int x, int y) {
		Battleship attacked = attacked(x, y);
		myTurn = true;
		if(attacked == null)
			return AttackResult.MISS;
		return attacked.alive() ? AttackResult.HIT : AttackResult.SINK;
	}

	public Map<Point, Boolean> getRadar() {
		return radar;
	}

	public int getShipsAlive() {
		int i = 0;
		for(Battleship b : fleet)
			if(b.alive())
				i++;
		return i;
	}
	
	public Battleship[] getFleet() {
		return this.fleet;
	}

	public boolean hasLost() {
		for(Battleship b : fleet)
			if(b.alive())
				return false;
		return true;
	}

	public void init() {
		if(fleet == null)
			fleet = provider.provide().toArray(new Battleship[0]);
	}
}
