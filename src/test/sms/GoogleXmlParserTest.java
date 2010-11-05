package sms;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.*;

import core.Query;

public class GoogleXmlParserTest {
	
	private String number1 = "1-503-777-7775";
	private String number2 = "1-503-555-5555";
	private String numberP = "1-503-893-4287";

	@Test
	public void testMessageParsing() {
		File f = new File("resources/g_dump.xml");
		
		
	}
	
	private boolean queriesAreEqual(Query q1, Query q2) {
		if (q1.getPhoneNumber().equals(q2.getPhoneNumber())) {
			return false;
		}
		if (!q1.getBody().equals(q2.getBody())) {
			return false;
		}
		if (!q1.getKeyword().equals(q2.getKeyword())) {
			return false;
		}
		if (!q1.getTimeReceived().equals(q2.getTimeReceived())) {
			return false;
		}
		return true;
	}
	
}
