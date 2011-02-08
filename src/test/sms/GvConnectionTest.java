package sms;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.*;

import com.techventus.server.voice.datatypes.Contact;
import com.techventus.server.voice.datatypes.records.SMS;
import com.techventus.server.voice.datatypes.records.SMSThread;

public class GvConnectionTest {

	/** Mon Nov 15 09:05:00 PST 2010 */
	private static final Date monday = new Date(1289840700887l);

	@Test
	public void testMostRecentSms() {
		Contact jinro = new Contact("LiquidJinro", "testcontact",
				"+1234567890", "http://teamliquid.net");
		SMSThread conv = new SMSThread("test", "this is a test thread", monday,
				jinro, false, false);
		SMS mostRecent = new SMS(jinro, "recent message", new Date(monday
				.getTime() + 3600000));
		conv.addSMS(mostRecent);
		for (int i = 0; i < 10; i++) {
			long time = monday.getTime() + (i + 1) * 60000;
			SMS sms = new SMS(jinro, "message #" + i, new Date(time));
			conv.addSMS(sms);
		}
		SMS found = GvConnection.getMostRecentSms(conv);
		assertSmsEquals(mostRecent, found);
	}

	/**
	 * A helper method for determing whether two sms are the same. Not
	 * particularly strict at the moment.
	 * 
	 * @param expected
	 *            the expected sms
	 * @param found
	 *            the sms to be checked
	 */
	private void assertSmsEquals(SMS expected, SMS found) {
		assertEquals(expected.getFrom().getNumber(), found.getFrom()
				.getNumber());
		assertEquals(expected.getDateTime(), found.getDateTime());
		assertEquals(expected.getContent(), found.getContent());
	}

}
