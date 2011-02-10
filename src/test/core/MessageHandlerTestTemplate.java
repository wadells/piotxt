package core;

import java.util.Date;
import static org.junit.Assert.*;
import org.junit.*;
import static core.MessageHandler.STD_DATE_FORM;

public abstract class MessageHandlerTest {

	protected MessageHandler handler;
	
	protected Keywords keywords;

	protected final Date time = new Date();

	protected final String timeString = STD_DATE_FORM.format(time);

	protected final String phoneNumber = "+15037777777";

	/** The maximum length of a single text message. */
	private static final int MAX_TEXT_LENGTH = 160;

	@Test
	public void testResponseLengths() {
		for (String key : keywords.words()) {
			Query q = new Query(time, key, phoneNumber);
			String response = handler.getResponse(q);
			assertTrue(response.length() <= MAX_TEXT_LENGTH);
		}
	}

	@Test
	public void testHelp() {
		String expected = "PioTxt\n" //
				+ timeString + "\n";
		for (String k : keywords.words()) {
			expected += "\n" + k.toUpperCase() + " " + keywords.getDefinition(k);
		}
		String response = handler.getResponse(new Query(time, "help", phoneNumber));
		assertEquals(expected, response);
	}

}
