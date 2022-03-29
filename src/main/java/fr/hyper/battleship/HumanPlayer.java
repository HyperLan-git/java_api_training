package fr.hyper.battleship;

import java.awt.Point;
import java.util.Scanner;

public class HumanPlayer implements Player {

	@Override
	public Point attack(BattleshipGame game) {
		System.out.println("Radar:");
		Player.drawRadar(game.getRadar());

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
