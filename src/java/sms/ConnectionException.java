package sms;

import java.io.IOException;

/** Very bad. Hopefully these won't happen often. */
@SuppressWarnings("serial")
public class ConnectionException extends IOException {

	public ConnectionException(String expl, Exception cause) {
		super(expl, cause);
	}

}
