package core;

import java.util.Date;
import static org.junit.Assert.*;
import org.junit.*;
import static core.MessageHandler.STD_DATE_FORM;

public abstract class MessageHandlerTest {

	protected MessageHandler handler;

	protected final Date time = new Date();

	protected final String timeString = STD_DATE_FORM.format(time);

	protected final Keywords keywords = Keywords.instance();

	protected final String phone = "123-456-7890";

	/** The maximum length of a single text message. */
	private static final int MAX_TEXT_LENGTH = 160;

	@Test
	public void testResponseLengths() {
		for (String key : keywords.words()) {
			Query q = new Query(time, key, phone);
			String response = handler.getResponse(q);
			assertTrue(response.length() <= MAX_TEXT_LENGTH);
		}
	}

	@Test
	public void testHelp() {
		String expected = "TXT4RAZ\n" //
				+ timeString + "\n";
		for (String k : keywords.words()) {
			expected += "\n" + k.toUpperCase() + " " + keywords.getDefinition(k);
		}
		String response = handler.getResponse(new Query(time, "help", phone));
		assertEquals(expected, response);
	}

}
