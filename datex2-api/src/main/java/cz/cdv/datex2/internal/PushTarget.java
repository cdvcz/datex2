package cz.cdv.datex2.internal;

import java.io.Serializable;
import java.net.URL;

@SuppressWarnings("serial")
public class PushTarget implements Serializable {

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

	@Override
	public String toString() {
		if (username == null && password == null)
			return url.toString();

		return (username != null ? username : "")
				+ (password != null ? ":" + password : "") + " @ "
				+ url.toString();
	}

}