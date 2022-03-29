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
	
	private boolean askBoat(Battleship[] fleet, int i, Scanner sc) {
		System.out.println("Will your " + fleet[i].getName() + " be vertical ? (y/n)");
		String line = sc.nextLine();
		boolean v = line.contentEquals("y");
		System.out.println("Enter position for your " + fleet[i].getName() + " (the most upper left tile written with a character followed by a number) : ");
		int[] pos = readPos(sc);
		if (pos == null) {
			return false;
		}
		try {fleet[i] = fleet[i].setPosition(pos[0], pos[1], v);} catch(IllegalArgumentException e) {
			System.out.println("Illegal position !");
			return false;
		}
		return true;
	}

	@Override
	public Collection<Battleship> provide() {
		Battleship[] fleet = RandomGame.defaultFleet();
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < fleet.length; i++) {
			while(!askBoat(fleet, i, sc));
		}
		return (Collection<Battleship>) Arrays.asList(fleet);
	}

}
