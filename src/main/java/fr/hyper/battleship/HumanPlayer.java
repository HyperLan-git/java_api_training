package fr.hyper.battleship;

import java.awt.Point;
import java.util.Scanner;

public class HumanPlayer implements Player {
	private Point getCell(Scanner sc) {
		try {
			System.out.println("Choississez une case à attaquer :");
			String tile = sc.nextLine();
			return new Point(tile.charAt(0) - 'A' + 1, Integer.valueOf(tile.substring(1)));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Point attack(BattleshipGame game) {
		System.out.println("Radar:");
		Player.drawRadar(game.getRadar());

		Scanner sc = new Scanner(System.in);
		Point toAttack = null;
		while(toAttack == null) toAttack = getCell(sc);
		return toAttack;
	}
}
