package fr.hyper.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.RandomGame;

public class NavyServer implements Runnable {

	private int port;

	public NavyServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			InetSocketAddress address = new InetSocketAddress(port);
			ExecutorService service = Executors.newFixedThreadPool(1);
			HttpServer server = HttpServer.create(address, 0);
			GameHandler handler = new GameHandler(new BattleshipGame(new RandomGame()));
			server.setExecutor(service);
			server.createContext("/ping", new PingHandler());
			server.createContext("/api/game/", handler);
			server.start();
		} catch (IOException e) {
			System.err.println("Could not create server ! Cause : " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

}
