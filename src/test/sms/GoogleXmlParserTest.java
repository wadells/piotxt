package sms;

import static org.junit.Assert.*;

import gvjava.org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.*;
import org.xml.sax.SAXException;

import core.Query;
import static sms.GoogleXmlParser.*;
import static utils.FileUtils.readFile;

public class GoogleXmlParserTest {

	@Test
	public void testMessageParsing() throws IOException, SAXException,
			JSONException {
		File f = new File("resources/test_gv_dump.xml");
		Query[] expected = new Query[] {
				new Query(new Date(1288921051731l), "help", "+15037777777"),
				new Query(new Date(1288945197661l), "Square", "+15035555555") };

		String xml = readFile(f);
		List<Query> parsed = parse(xml);
		assertEqual(expected[0], parsed.get(0));
		assertEqual(expected[1], parsed.get(1));

	}

	private void assertEqual(Query q1, Query q2) {
		assertTrue(q1.getPhoneNumber().equals(q2.getPhoneNumber()));
		assertTrue(q1.getBody().equals(q2.getBody()));
		if (q1.getKeyword() != null || q2.getKeyword() != null) {
			assertTrue(q1.getKeyword().equals(q2.getKeyword()));
		}
		assertTrue(!q1.getTimeSent().equals(q2.getTimeSent()));
	}

}
