package fr.hyper.http.mock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

public class HttpExchangeMock extends HttpExchange {
	private final InputStream requestBody;
	private final String requestMethod;
	private final URI uri;
	private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private final Headers response = new Headers();

	public HttpExchangeMock(InputStream requestBody, String requestMethod) {
		this.requestBody = requestBody;
		this.requestMethod = requestMethod;
		try {
			this.uri = new URI("");
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Error when testing !");
		}
	}

	public HttpExchangeMock(InputStream requestBody, String requestMethod, String uri) {
		this.requestBody = requestBody;
		this.requestMethod = requestMethod;
		try {
			this.uri = new URI(uri);
		} catch(URISyntaxException e) {
			throw new IllegalStateException("Error when testing !");
		}
	}

	public String getResponse() {
		System.out.println("response = " + (stream == null));
		return stream.toString();
	}

	@Override
	public Headers getResponseHeaders() { return response; }

	@Override
	public URI getRequestURI() { return uri; }

	@Override
	public String getRequestMethod() { return requestMethod; }

	@Override
	public HttpContext getHttpContext() { return new HttpContextMock(); }

	@Override
	public void close() {}

	@Override
	public InputStream getRequestBody() { return requestBody; }

	@Override
	public OutputStream getResponseBody() { return stream; }

	@Override
	public void sendResponseHeaders(int rCode, long responseLength) throws IOException {}

	@Override
	public InetSocketAddress getRemoteAddress() { return null; }

	@Override
	public int getResponseCode() { return 0; }

	@Override
	public InetSocketAddress getLocalAddress() { return null; }

	@Override
	public String getProtocol() { return null; }

	@Override
	public Object getAttribute(String name) { return null; }

	@Override
	public void setAttribute(String name, Object value) {}

	@Override
	public void setStreams(InputStream i, OutputStream o) {}

	@Override
	public HttpPrincipal getPrincipal() { return null; }

	@Override
	public Headers getRequestHeaders() { return null; }

}
