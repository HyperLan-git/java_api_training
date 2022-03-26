package fr.hyper.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.ComputerPlayer;
import fr.hyper.battleship.RandomGame;

public class NavyServer implements Runnable {

	private final HttpServer server;

	public NavyServer(int port) {
		InetSocketAddress address = new InetSocketAddress(port);
		HttpServer s = null;
		try {
			s = HttpServer.create(address, 0);
		} catch (IOException e) {
			System.err.println("Could not create server ! Cause : " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		server = s;
	}

	@Override
	public void run() {
		ExecutorService service = Executors.newFixedThreadPool(1);
		GameHandler handler = new GameHandler(new BattleshipGame(new RandomGame()), new ComputerPlayer());
		server.setExecutor(service);
		server.createContext("/ping", new PingHandler());
		server.createContext("/api/game/", handler);
		server.start();
	}

}
