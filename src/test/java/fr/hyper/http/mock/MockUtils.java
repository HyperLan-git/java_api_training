package fr.hyper.http.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public abstract class MockUtils {
	public static final InputStream inputStreamOf(final String input) {
		return new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8")));
	}
}
