package fr.lernejo.navy_battle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import fr.hyper.http.GameHandler;
import fr.hyper.http.NavyClient;
import fr.hyper.http.NavyServer;
import fr.hyper.http.PingHandler;

public class Launcher {
	public static HttpServer createServer(InetSocketAddress addr, GameHandler handler, int backlog) {
		HttpServer s = null;
		try {
			s = HttpServer.create(addr, backlog);
		} catch (IOException e) {
			System.err.println("Could not create server ! Cause : " + e.getLocalizedMessage());
			e.printStackTrace();
			return null;
		}
		ExecutorService service = Executors.newFixedThreadPool(1);
		s.setExecutor(service);
		s.createContext("/ping", new PingHandler());
		s.createContext("/api/game/", handler);
		s.start();
		return s;
	}
	
	private static void logToFile(String file) {
		File f = new File(file);
		try {
			f.createNewFile();
			System.setOut(new PrintStream(f));
			System.setErr(System.out);
			Thread.sleep(50);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		logToFile("log" + args.length + "_" + System.currentTimeMillis() + ".txt");
		if(args.length < 1) {
			System.out.println("Port needed !");
			return;
		}
		Thread t = null;
		if (args.length > 1) 
			t = new Thread(new NavyClient(Integer.valueOf(args[0]), args[1]));
		else
			t = new Thread(new NavyServer(Integer.valueOf(args[0])));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.print(e);
		}
	}
}
