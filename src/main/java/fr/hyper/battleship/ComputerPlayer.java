package fr.hyper.battleship;

import java.awt.Point;
import java.util.Map;

public class ComputerPlayer implements Player {

	@Override
	public Point attack(BattleshipGame game) {
		Map<Point, Boolean> radar = game.getRadar();
		Point chosen = null;
		System.out.println("Radar:");
		Player.drawRadar(radar);
		do {
			chosen = new Point((int)(Math.random() * BattleshipGame.getDefaultSize() + 1),
					(int)(Math.random() * BattleshipGame.getDefaultSize() + 1));
			for(Point p : radar.keySet()) if (p.x == chosen.x && p.y == chosen.y) {
				chosen = null;
				break;
			}
		} while(chosen == null);
		System.out.println("chosen");
		return chosen;
	}
}
