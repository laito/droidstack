package org.droidstack.stackapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A wrapper around the StackExchange family of websites API
 * @author Felix Oghină <felix.oghina@gmail.com>
 */
public class StackAPI {
	
	/**
	 * Contains the supported version of the API. This is included in API calls
	 */
	public final static String API_VERSION = "0.8";
	
	private final String mHost;
	private final String mKey;
	
	
	/**
	 * Create a StackAPI object without an API key.
	 * @param host The hostname to which API calls are made (e.g. "api.stackoverflow.com")
	 */
	public StackAPI(String host) {
		mHost = host;
		mKey = null;
	}
	/**
	 * Create a StackAPI object.
	 * @param host The hostname to which API calls are made (e.g. "api.stackoverflow.com")
	 * @param key The API key to use in these requests.
	 */
	public StackAPI(String host, String key) {
		mHost = host;
		mKey = key;
	}
	
	private String fetchUrlContents(URL url) throws IOException {
		final URLConnection conn = url.openConnection();
		conn.connect();
		final InputStream in;
		if (conn.getContentEncoding().equals("gzip")) {
			in = new GZIPInputStream(conn.getInputStream());
		}
		else {
			in = conn.getInputStream();
		}
		String charset = conn.getContentType();
		final BufferedReader reader;
		if (charset.indexOf("charset=") != -1) {
			charset = charset.substring(charset.indexOf("charset=") + 8);
			reader = new BufferedReader(new InputStreamReader(in, charset));
		}
		else {
			charset = null;
			reader = new BufferedReader(new InputStreamReader(in));
		}
		final StringBuilder builder = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			builder.append(line + '\n');
			line = reader.readLine();
		}
		return builder.toString();
	}
	
	private URL buildUrlFromPath(String path) throws MalformedURLException {
		String url = "http://" + mHost + "/" + String.valueOf(API_VERSION) + path;
		if (mKey != null) {
			if (url.indexOf('?') == -1) url += "?key=" + mKey;
			else url += "&key=" + mKey;
		}
		return new URL(url);
	}
	
	/**
	 * Perform a question API query
	 * @param query Describes what query to perform
	 * @return A list of questions retrieved
	 * @throws IOException If there's a problem communicating with the server (this includes invalid API key)
	 * @throws MalformedURLException Should never occur, as we are building the URL ourselves. It will occur if you supply a stupid value for <code>host</code> to {@link #StackAPI(String)} or {@link #StackAPI(String, String)}
	 * @throws JSONException If the site you're querying is fucked up and the json returned is invalid (or it's not json)
	 */
	public Set<Question> getQuestions(QuestionsQuery query) throws IOException, MalformedURLException, JSONException {
		int i;
		final Set<Question> questions = new HashSet<Question>();
		final URL url = buildUrlFromPath(query.buildQueryPath());
		final JSONObject json = (JSONObject) new JSONTokener(fetchUrlContents(url)).nextValue();
		final JSONArray jquestions = json.getJSONArray("questions");
		for (i=0; i < jquestions.length(); i++) {
			questions.add(new Question(jquestions.getJSONObject(i)));
		}
		return questions;
	}
	
}
