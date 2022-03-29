package fr.hyper.battleship;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class RandomGame implements BattleshipProvider {
	public static final Battleship[] defaultFleet() {
		Battleship[] fleet = new Battleship[5];
		fleet[0] = new Battleship("Carrier", 5);
		fleet[1] = new Battleship("Battleship", 4);
		fleet[2] = new Battleship("Cruiser", 3);
		fleet[3] = new Battleship("Submarine", 3);
		fleet[4] = new Battleship("Destroyer", 2);
		return fleet;
	}

	protected final boolean collidesWithPrevious(Battleship b, Battleship[] fleet, int i) {
		for(int j = 0; j < i; j++)
			for(int[] pos : fleet[j].getPositions())
				for (int[] pos2 : fleet[i].getPositions())
					if (pos[0] == pos2[0] && pos[1] == pos2[1])
						return true;
		return false;
	}

	private Battleship[] randomFleet() {
		Random r = new Random();
		Battleship[] fleet = defaultFleet();
		boolean collides;
		for(int i = 0; i < 5; i++) {
			do {
				collides = false;
				int x = r.nextInt(BattleshipGame.getDefaultSize()) + 1,
						y = r.nextInt(BattleshipGame.getDefaultSize()) + 1;
				try {
					fleet[i] = fleet[i].setPosition(x, y, r.nextBoolean());
				} catch (IllegalArgumentException e) {
					collides = true;
					continue;
				}
				if(collidesWithPrevious(fleet[i], fleet, i))
					collides = true;
			} while(collides);
		}
		return fleet;
	}

	@Override
	public Collection<Battleship> provide() {
		return Arrays.asList(randomFleet());
	}

}
