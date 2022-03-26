package fr.hyper.http.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpContextMock extends HttpContext {

	@Override
	public HttpHandler getHandler() {
		return null;
	}

	@Override
	public void setHandler(HttpHandler handler) {
	}

	@Override
	public String getPath() {
		return "";
	}

	@Override
	public HttpServer getServer() {
		return new HttpServerMock();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return new HashMap<>();
	}

	@Override
	public List<Filter> getFilters() {
		return new ArrayList<>();
	}

	@Override
	public Authenticator setAuthenticator(Authenticator auth) {
		return auth;
	}

	@Override
	public Authenticator getAuthenticator() {
		return null;
	}
}
