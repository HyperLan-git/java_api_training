package fr.hyper.battleship;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;

public class ComputerPlayer implements Player {
	@Override
	public Point attack(BattleshipGame game) {
		Map<Point, Boolean> radar = game.getRadar();
		Point chosen = null;
		System.out.println("Radar:");
		for (int i = 0; i < BattleshipGame.SIZE; i++) {
			System.out.println("---------------------");
			for (int j = 0; j < BattleshipGame.SIZE; j++) {
				boolean attacked = false;
				boolean hit = false;
				for(Entry<Point, Boolean> entry : game.getRadar().entrySet()) {
					Point p = entry.getKey();
					if(p.y == i+1 && p.x == j+1) {
						attacked = true;
						hit = entry.getValue();
					}
				}
				char c = attacked ? '.' : ' ';
				if(hit) c = '*';
				System.out.print("|" + c);
			}
			System.out.println("|");
		}
		System.out.println("---------------------");
		do {
			chosen = new Point((int)(Math.random() * BattleshipGame.SIZE + 1),
					(int)(Math.random() * BattleshipGame.SIZE + 1));
			for(Point p : radar.keySet()) if (p.x == chosen.x && p.y == chosen.y) {
				chosen = null;
				break;
			}
		} while(chosen == null);
		System.out.println("chosen");
		return chosen;
	}
}
