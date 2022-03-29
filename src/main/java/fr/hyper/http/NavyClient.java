package fr.hyper.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.ComputerPlayer;
import fr.hyper.battleship.RandomGame;

public class NavyClient implements Runnable {
	private final String address;
	private final int port;

	public NavyClient(Integer port, String address) {
		this.port = port;
		this.address = address;
	}

	@Override
	public void run() {
		HttpServer s = null;
		GameHandler handler = new GameHandler(new BattleshipGame(new RandomGame()), new ComputerPlayer());
		try {
			s = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			System.err.println("Could not create server ! Cause : " + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		ExecutorService service = Executors.newFixedThreadPool(1);
		s.setExecutor(service);
		s.createContext("/ping", new PingHandler());
		s.createContext("/api/game/", handler);
		s.start();
		System.out.println("before start request");
		System.out.println("threads = " + Thread.activeCount());
		handler.sendStartRequest(address, "http://[" + s.getAddress().getHostName() + "]:" + s.getAddress().getPort());
		System.out.println("after start request");
		System.out.println("threads = " + Thread.activeCount());
		while(!handler.done());
		s.stop(1);
		System.out.println("done");
		//Why do I need to do this...
		System.exit(0);
		Thread.currentThread().interrupt();
	}

}
