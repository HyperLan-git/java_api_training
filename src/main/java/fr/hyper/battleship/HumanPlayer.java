package fr.hyper.battleship;

import java.awt.Point;
import java.util.Map.Entry;
import java.util.Scanner;

public class HumanPlayer implements Player {

	@Override
	public Point attack(BattleshipGame game) {
		System.out.println("Radar:");
		for (int i = 0; i < BattleshipGame.getDefaultSize(); i++) {
			System.out.println("---------------------");
			for (int j = 0; j < BattleshipGame.getDefaultSize(); j++) {
				boolean attacked = false;
				boolean hit = false;
				for(Entry<Point, Boolean> entry : game.getRadar().entrySet()) {
					Point p = entry.getKey();
					if(p.y == i && p.x == j) {
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

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		Point toAttack = null;
		do {
			try {
				System.out.println("Choississez une case à attaquer :");
				String tile = sc.nextLine();
				toAttack = new Point(tile.charAt(0) - 'A' + 1, Integer.valueOf(tile.substring(1)));
			} catch (NumberFormatException e) {
				toAttack = null;
			}
		} while(toAttack == null);
		return toAttack;
	}
}
