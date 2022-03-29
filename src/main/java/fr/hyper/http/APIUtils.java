package fr.hyper.http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class APIUtils {
	public static final boolean contains(JSONArray arr, CharSequence str) {
		for(Object obj : arr) {
			if(obj instanceof String)
				if (((String)obj).contentEquals(str))
					return true;
		}
		return false;
	}

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.contains("&") ? query.split("&") : new String[] {query};
		Map<String, String> map = new HashMap<String, String>();

		for (String param : params) {
			String[] p = param.split("="); 
			map.put(p[0], p[1]);  
		}
		return map;  
	}

	public static final JSONObject decodeRequest(InputStream requestBody) {
		JSONObject request = null;
		InputStreamReader reader = new InputStreamReader(requestBody);
		try (StringWriter str = new StringWriter()) {
			reader.transferTo(str);
			System.out.println("Reading : " + str.toString());

			if(!str.getBuffer().isEmpty())
				request = new JSONObject(str.toString());
		} catch (Exception e) {
			return null;
		}
		System.out.println("request = " + request);
		return request;
	}
}
