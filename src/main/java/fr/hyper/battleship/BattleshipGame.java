package fr.hyper.battleship;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleshipGame {

	public static final int SIZE = 10;

	private final Map<Point, Boolean> radar = new HashMap<>();

	private final List<Battleship> fleet = new ArrayList<>();

	private BattleshipProvider provider = new RandomGame();

	public boolean myTurn = false;

	public BattleshipGame() {}

	public BattleshipGame(BattleshipProvider provider) {
		this.provider = provider;
	}

	public BattleshipGame(Battleship[] fleet) {
		for(Battleship ship : fleet)
			this.fleet.add(ship);
	}

	public void attacking(Point p, boolean hit) {
		myTurn = false;
		radar.put(p, hit);
	}

	public Battleship attacked(Point p) {
		for(Battleship ship : fleet) {
			if(ship.alive() && ship.attack(p.x, p.y))
				return ship;
		}
		return null;
	}

	public AttackResult getAttacked(Point p) {
		Battleship attacked = attacked(p);
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
		return (Battleship[]) fleet.toArray(new Battleship[fleet.size()]);
	}

	public boolean hasLost() {
		for(Battleship b : fleet)
			if(b.alive())
				return false;
		return true;
	}

	public void init() {
		if(fleet.size() == 0)
			fleet.addAll(provider.provide());
	}
}
