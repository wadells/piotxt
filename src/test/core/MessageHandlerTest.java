package core;

import java.util.GregorianCalendar;
import static org.junit.Assert.*;
import org.junit.*;

public abstract class MessageHandlerTest {

	protected MessageHandler handler;

	protected final GregorianCalendar time = new GregorianCalendar();

	/** The maximum length of a single text message. */
	private static final int MAX_TEXT_LENGTH = 160;

	@Test
	public void testResponseLengths() {
		for (Keyword key : Keyword.values()) {
			String response = handler.getResponse(key, time);
			assertTrue(response.length() <= MAX_TEXT_LENGTH);
		}
	}

	@Test
	public void testHelp() {
		String expected = "txt4raz keywords\n\n" //
				+ "HELP" // TODO: fill in what help should actually deliver
		;
		String response = handler.getResponse("help", time);
		assertEquals(expected, response);
	}

}
