package sms;

import core.Query;

public interface SmsConnection {
	
	public Query[] getMessages();
	
	public void sendSms(String returnNumber, String message);


}
