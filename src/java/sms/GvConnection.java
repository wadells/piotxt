package sms;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.xml.sax.SAXException;

import persistance.Log;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.exception.CaptchaRequiredException;

import core.Query;
import static sms.GoogleXmlParser.*;

/**
 * A connection to send and recieve texts from Google Voice. The setup() method
 * must be called before any other request, and can be called again to refresh
 * the connection. This connection is pull only and must have the
 * getNewMessages() method actively called to recieve messages.
 */
public class GvConnection implements SmsConnection {

	/** The file where the connection information is stored. */
	private final static String PROPERTY_FILE = "resources/pio_text.properties";

	/** The username of the account this connects to. */
	private final String username;

	/** The password to login to the account. */
	private final String password;

	/** The pre-processing connection supplied by the google-voice-java library. */
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
		List<Query> messages = null;
		try {
			String page = voice.getSMS();
			messages = parse(page);
		} catch (IOException e) {
			throw new SmsRecieveException("Could retrieve sms from Google Voice.", e);
		} catch (SAXException e) {
			throw new SmsRecieveException("Could not parse sms xml returned by Google Voice.", e);
		}
		return messages;
	}

	@Override
	public void sendSms(String number, String message) throws SmsSendException {
		try {
			voice.sendSMS(number, message);
		} catch (IOException e) {
			throw new SmsSendException("Unable to send sms though Google Voice", e);
		}
	}

	/**
	 * Connects to Google Voice. This must be called before any other methods
	 * may be called.
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
	 * Load a Properties file.
	 * 
	 * @param propsFile
	 * @return Properties
	 * @throws IOException
	 */
	static Properties load(String propsFile) throws IOException {
		Properties result = null;
		InputStream in = new FileInputStream(propsFile);
		if (in != null) {
			result = new Properties();
			result.load(in); // Can throw IOException
		}
		return result;
	}

	// a simple test that prints all pending queries to the console
	public static void main(String[] args) throws IOException {
		GvConnection connection = new GvConnection();
		connection.setup();
		List<Query> list = connection.getNewMessages();
		System.out.println("\n" + list.size() + " new queries:");
		for (Query q : list) {
			System.out.println(Log.queryToString(q));
		}
	}

}
