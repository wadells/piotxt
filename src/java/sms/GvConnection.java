package sms;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import persistance.Log;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.exception.CaptchaRequiredException;

import core.Query;
import static sms.GoogleXmlParser.*;

public class GvConnection implements SmsConnection {

	private final static String PROPERTY_FILE = "resources/pio_text.properties";
	private final String username;
	private final String password;
	private Voice voice;

	/**
	 * Creates a new connection to a Google Voice voice accoutnt from parameters
	 * stored in the PROPERTY_FILE. The connection then needs to be setup().
	 * 
	 * @throws IOException
	 */
	public GvConnection() throws IOException {
		try {
			Properties props = load(PROPERTY_FILE);
			username = props.getProperty("username");
			password = props.getProperty("password");
		} catch (IOException e) {
			System.out.println("Could not read " + PROPERTY_FILE + ".");
			throw e;
		}
	}

	/**
	 * Creates a new connection to a Google Voice voice accoutnt from the
	 * supplied arguments. The connection then needs to be setup().
	 * 
	 * @param username
	 *            the username or email addess of the account
	 * @param the
	 *            cleartext password of the account
	 */
	public GvConnection(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public List<Query> getNewMessages() throws SmsRecieveException {
		// TODO: implement this
		try {
			String page = voice.getSMS();
			
		} catch (IOException e) {
			throw new SmsRecieveException(e);
		}
		return null;
	}

	String getRawSmsData() throws IOException {
		return voice.getSMS();
	}

	@Override
	public void sendSms(String number, String message) throws SmsSendException {
		try {
			voice.sendSMS(number, message);
		} catch (IOException e) {
			throw new SmsSendException(e);
		}
	}

	/**
	 * Connects to Google Voice.  This must be called before any other methods may be called.
	 * 
	 * @throws IOException
	 */
	public void setup() throws IOException {

		try {
			voice = new Voice(username, password);
		} catch (CaptchaRequiredException captEx) {
			// TO DO: Intelligent handling of this instead of automatic shutdown
			System.out.println("A captcha is required.");
			System.out.println("Image URL  = " + captEx.getCaptchaUrl());
			System.out.println("Capt Token = " + captEx.getCaptchaToken());
			System.out.println("Goodbye.");
			System.exit(1);
		}
	}
	
	/**
	 * Load a Properties File
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	private static Properties load(String propsFile) throws IOException {
		Properties result = null;
		InputStream in = new FileInputStream(propsFile);
		if (in != null) {
			result = new Properties();
			result.load(in); // Can throw IOException
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		GvConnection connection = new GvConnection();

		connection.setup();
		
//		for (int i = 0; i < 5; i++) {
//			String xml = connection.getRawSmsData();
//			utils.FileUtils.writeFile("resources/gv_dump" + i + ".xml", xml, true);
//			
//		}

		List<Query> list = parse(connection.getRawSmsData());
		
		System.out.println(list.size() + " new queries:\n");
		for (Query q : list) {
			System.out.println(Log.queryToString(q));
		}
	}

}
