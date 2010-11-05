package sms;

import java.io.IOException;

/** For when those pesky sms just won't come in. */
@SuppressWarnings("serial")
public class SmsRecieveException extends IOException {
	
	public SmsRecieveException(Exception cause) {
		super(cause);
	}

}
