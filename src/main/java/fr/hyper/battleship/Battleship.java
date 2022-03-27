package fr.hyper.battleship;

public class Battleship {
	private final String name;
	private final int size;
	private final boolean[] sunk;

	private final int x, y;
	private final boolean vertical;

	private boolean checkOOB() {
		return (vertical && y + size > BattleshipGame.SIZE + 1) ||
				(!vertical && x + size > BattleshipGame.SIZE + 1) ||
				x <= 0 || y <= 0 || x > BattleshipGame.SIZE ||
				y > BattleshipGame.SIZE;
	}

	public Battleship(String name, int size) {
		this.name = name;
		this.size = size;
		this.sunk = new boolean[size];
		this.x = this.y = 1;
		vertical = false;
	}

	public Battleship(String name, int size, int x, int y, boolean vertical) {
		this.name = name;
		this.size = size;
		this.sunk = new boolean[size];
		this.x = x;
		this.y = y;
		this.vertical = vertical;
		if(checkOOB())
			throw new IllegalArgumentException("Battleship out of bounds !");
	}

	public Battleship setPosition(int x, int y, boolean vertical) {
		Battleship battleship = new Battleship(name, size, x, y, vertical);
		if(battleship.checkOOB()) {
			throw new IllegalArgumentException("Battleship out of bounds !");
		}
		return battleship;
	}

	public int[][] getPositions() {
		int[][] result = new int[size][2];
		for(int i = 0; i < size; i++) {
			result[i][0] = x + ((vertical) ? 0 : i);
			result[i][1] = y + ((vertical) ? i : 0);
		}
		return result;
	}

	public int[][] getAlivePositions() {
		int[][] result = new int[size][2];
		for(int i = 0; i < size; i++) {
			if (sunk[i]) {
				result[i][0] = -1;
				result[i][1] = -1;
				continue;
			}
			result[i][0] = x + ((vertical) ? 0 : i);
			result[i][1] = y + ((vertical) ? i : 0);
		}
		return result;
	}

	public boolean alive() {
		for(boolean b : sunk)
			if(!b)
				return true;
		return false;
	}

	private int getTile(int x, int y) {
		int dx = x - this.x, dy = y - this.y;
		if((vertical && (dx != 0 || dy < 0 || dy >= size)) ||
				(!vertical && (dy != 0 || dy < 0 || dy >= size)))
			return -1;
		if(!vertical)
			return x - this.x;
		return y - this.y;
	}

	public boolean isAlive(int x, int y) {
		int i = getTile(x, y);
		if (i == -1)
			throw new IllegalArgumentException("Not a tile occupied by this boat !");
		return !sunk[i];
	}

	public boolean attack(int x, int y) {
		int i = getTile(x, y);
		if (i < 0 || i >= this.size)
			return false;
		boolean ret = !sunk[i];
		sunk[i] = true;
		return ret;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Battleship)) return false;
		Battleship other = (Battleship) obj;
		return other.x == this.x && other.y == this.y &&
				other.vertical == this.vertical &&
				other.name == this.name;
	}
}
