package cz.cdv.datex2.internal;

import java.net.URL;

public class PushTarget {

	private final URL url;
	private final String username;
	private final String password;

	public PushTarget(URL url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public URL getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}