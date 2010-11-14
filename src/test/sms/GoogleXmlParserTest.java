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
		File f = new File("resources/test_gv_dump0.xml");
		// all values pulled from the file by hand
		GvQuery[] expected = new GvQuery[] {
				new GvQuery(new Date(1288945197661l), "help", "+15037777777",
						"1d43d847806afe0040d7d4f361c28d157c0ce2b1"),
				new GvQuery(new Date(1288921051731l), "Square", "+15035555555",
						"17dcd7078a6bcebd8aa40d9d62a72a3d42cd0aaf") };

		String xml = readFile(f);
		List<GvQuery> parsed = parse(xml);
		assertEqual(expected[0], parsed.get(0));
		assertEqual(expected[1], parsed.get(1));

	}

	/**
	 * This case tests on a file that has consitentantly caused errors. First
	 * real world test case. This case is a result of html sometime using '' for
	 * attributes and other times "". Both need to be caught when removing query
	 * strings.
	 */
	@Test
	public void testCase1() throws IOException, SAXException, JSONException {
		File f = new File("resources/test_gv_dump1.xml");
		// all values pulled from the file by hand
		GvQuery[] expected = new GvQuery[] {
				new GvQuery(new Date(1289108018380l), "all your base are belong to us", "+15035555555",
						"5aa2777b98c43426c21500b3c8cc815cb3d9a5fc"),
				new GvQuery(new Date(1288947820930l), "sld", "+15037777777",
						"3d5cffd02883ef6f31dc9e5b66656ecb54546d1e") };
		String xml = readFile(f);
		List<GvQuery> parsed = parse(xml);
		assertEqual(expected[0], parsed.get(0));
		assertEqual(expected[1], parsed.get(1));
	}

	private void assertEqual(Query q1, Query q2) {
		assertEquals(q1.getPhoneNumber(), q2.getPhoneNumber());
		assertEquals(q1.getBody(), q2.getBody());
		if (q1.getKeyword() != null || q2.getKeyword() != null) {
			assertEquals(q1.getKeyword(), q2.getKeyword());
		}
		assertEquals(q1.getTimeSent(), q2.getTimeSent());
	}

}
