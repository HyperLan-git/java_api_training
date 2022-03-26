package fr.hyper.battleship;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class UserGame implements BattleshipProvider {

	private int[] readPos(Scanner sc) {
		String str = sc.nextLine();
		if (str.length() == 0)
			return null;
		int x = str.charAt(0) - 'A';
		x++;
		int y = -1;
		try {
			y = Integer.valueOf(str.substring(1));
		} catch (NumberFormatException e) {
			return null;
		}
		return new int[] {x, y};
	}

	@Override
	public Collection<Battleship> provide() {
		Battleship[] fleet = RandomGame.defaultFleet();
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < fleet.length; i++) {
			Battleship b = fleet[i];
			boolean p;
			do {
				System.out.println("Will your " + b.getName() + " be vertical ? (y/n)");
				boolean v = sc.nextLine().contentEquals("y");
				System.out.println("Enter position for your " + b.getName() + " (the most upper left tile written with a character followed by a number) : ");

				p = false;
				int[] pos = readPos(sc);
				if (pos == null) {
					p = true;
					continue;
				}
				try {
					b.setPosition(pos[0], pos[1], v);
				} catch(IllegalArgumentException e) {
					p = true;
					System.out.println("Illegal position !");
				}
			} while (p);
		}
		return (Collection<Battleship>) Arrays.asList(fleet);
	}

}
