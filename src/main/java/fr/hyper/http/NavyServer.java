package fr.hyper.http;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.ComputerPlayer;
import fr.hyper.battleship.RandomGame;
import fr.lernejo.navy_battle.Launcher;

public class NavyServer implements Runnable {

	private final int port;

	public NavyServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		GameHandler handler = new GameHandler(new BattleshipGame(new RandomGame()), new ComputerPlayer());
		HttpServer server = Launcher.createServer(new InetSocketAddress(port), handler, 0);
		while(!handler.done());
		server.stop(0);
		Thread.currentThread().interrupt();
	}

}
