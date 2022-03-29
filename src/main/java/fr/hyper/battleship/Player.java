package fr.hyper.battleship;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;

public interface Player {

	public static char getChar(Map<Point, Boolean> radar, int x, int y) {
		boolean attacked = false;
		boolean hit = false;
		for(Entry<Point, Boolean> entry : radar.entrySet()) {
			Point p = entry.getKey();
			if(p.y == y+1 && p.x == x+1) {
				attacked = true;
				hit = entry.getValue();
			}
		}
		char c = attacked ? '.' : ' ';
		if(hit) c = '*';
		return c;
	}

	public static void drawRadar(Map<Point, Boolean> radar) {
		for (int i = 0; i < BattleshipGame.getDefaultSize(); i++) {
			System.out.println("---------------------");
			for (int j = 0; j < BattleshipGame.getDefaultSize(); j++) {
				System.out.print("|" + getChar(radar, j, i));
			}
			System.out.println("|");
		}
		System.out.println("---------------------");
	}
	public Point attack(BattleshipGame game);
}
