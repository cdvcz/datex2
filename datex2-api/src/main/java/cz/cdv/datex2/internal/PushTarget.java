package cz.cdv.datex2.internal;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.datex2.schema._2._2_0.Target;

@SuppressWarnings("serial")
public class PushTarget implements Serializable {

	private static Logger log = Logger.getLogger(PushTarget.class
			.getSimpleName());

	private final URL url;
	private final String username;
	private final String password;

	public static PushTarget create(Target target) {
		try {
			// FIXME: obtain username, password
			return new PushTarget(new URL(target.getAddress()), null, null);
		} catch (MalformedURLException e) {
			log.log(Level.SEVERE, "Error parsing target", e);
			return null;
		}
	}

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

	public Target toTarget() {
		Target t = new Target();
		t.setAddress(url.toString());
		t.setProtocol(url.getProtocol());
		// FIXME: username and password?
		return t;
	}

}
