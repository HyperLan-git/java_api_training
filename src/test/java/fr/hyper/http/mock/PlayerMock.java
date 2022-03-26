package fr.hyper.http.mock;

import java.awt.Point;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.Player;

public class PlayerMock implements Player {
	private Point toPlay = null;

	public void playNext(Point p) {
		toPlay = p;
	}

	@Override
	public Point attack(BattleshipGame game) {
		return toPlay;
	}
}
