package fr.hyper.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class NavyServer implements Runnable {
	public static final String OK = "OK";

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
			server.setExecutor(service);
			server.createContext("/ping", new PingHandler());
			server.start();
			while(true) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
