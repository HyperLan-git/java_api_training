package fr.hyper.http.mock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerMock extends HttpServer {
	@Override
	public void bind(InetSocketAddress addr, int backlog) throws IOException {}

	@Override
	public void start() {}

	@Override
	public void setExecutor(Executor executor) {}

	@Override
	public Executor getExecutor() {return null;}

	@Override
	public void stop(int delay) {}

	@Override
	public HttpContext createContext(String path, HttpHandler handler) {
		return null;
	}

	@Override
	public HttpContext createContext(String path) {
		return null;
	}

	@Override
	public void removeContext(String path) throws IllegalArgumentException {}

	@Override
	public void removeContext(HttpContext context) {}

	@Override
	public InetSocketAddress getAddress() {
		return new InetSocketAddress(0);
	}

}
