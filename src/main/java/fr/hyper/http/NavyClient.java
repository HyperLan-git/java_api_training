package fr.hyper.http;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.battleship.BattleshipGame;
import fr.hyper.battleship.ComputerPlayer;
import fr.hyper.battleship.RandomGame;
import fr.lernejo.navy_battle.Launcher;

public class NavyClient implements Runnable {
	private final String address;
	private final int port;

	public NavyClient(Integer port, String address) {
		this.port = port;
		this.address = address;
	}

	@Override
	public void run() {
		GameHandler handler = new GameHandler(new BattleshipGame(new RandomGame()), new ComputerPlayer());
		HttpServer server = Launcher.createServer(new InetSocketAddress(port), handler, 0);
		handler.sendStartRequest(address, "http://[" + server.getAddress().getHostName() + "]:" + server.getAddress().getPort());
		while(!handler.done());
		server.stop(0);
		Thread.currentThread().interrupt();
	}

}
