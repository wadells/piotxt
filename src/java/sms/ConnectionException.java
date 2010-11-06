package sms;

import java.io.IOException;

/** For when those pesky sms just won't go. */
@SuppressWarnings("serial")
public class ConnectionException extends IOException {
	
	public ConnectionException(String expl, Exception cause) {
		super(expl, cause);
	}

}
