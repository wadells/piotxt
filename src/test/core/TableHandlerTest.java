package core;

import java.io.File;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.*;

public class TableHandlerTest extends MessageHandlerTest {

	private static final File TEST_SCHEDULE = new File(
			"resources/test_schedule.txt");

	/** Mon Nov 15 09:05:00 PST 2010 */
	private static final Date monday = new Date(1289840700887l);

	@Before
	public void setUp() {
		handler = new TableHandler();
		((TableHandler) handler).initialize(TEST_SCHEDULE);
		keywords = handler.keywords;
	}

	@Test
	public void testAllStops() {
		Query query = new Query(monday, "all stops please", phoneNumber);
		query.setTimeReceived(monday);
		String response = handler.getResponse(query);
		String expected = "PioTxt\n" //
				+ "Mon, Nov 15, 09:05 AM\n\n" //
				+ "09:10am @ Seattle\n" //
				+ "09:31am @ Missoula\n" //
				+ "09:50am @ Seattle\n" //
				+ "10:00am @ Portland\n" //
				+ "10:10am @ Seattle\n" //
				+ "10:31am @ Missoula\n" //
				+ "10:50am @ Seattle";
		assertEquals(expected, response);
	}

	@Test
	public void testSingleStop() {
		Query query = new Query(monday, "mso", phoneNumber);
		query.setTimeReceived(monday);
		String response = handler.getResponse(query);
		String expected = "PioTxt\n" //
				+ "Mon, Nov 15, 09:05 AM\n\n" //
				+ "@ Missoula\n" //
				+ "09:31am\n10:31am\n11:31am\n12:31pm\n01:31pm\n" //
				+ "02:31pm\n03:31pm\n04:31pm\n05:31pm\n06:31pm\n" //
				+ "07:31pm\n08:31pm\n09:31pm\n10:31pm";
		assertEquals(expected, response);
	}

	@Test
	public void testSingleStopDirectional() {
		fail("Not implemented yet.");
	}

	@Test
	public void testUnknownKeyword() {
		fail("Not implemented yet.");
	}

}
