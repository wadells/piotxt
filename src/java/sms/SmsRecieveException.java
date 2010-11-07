package sms;

import java.io.IOException;

/** For when those pesky sms just won't come in. */
@SuppressWarnings("serial")
public class SmsRecieveException extends IOException {

	public SmsRecieveException(String expl, Exception cause) {
		super(expl, cause);
	}

}
