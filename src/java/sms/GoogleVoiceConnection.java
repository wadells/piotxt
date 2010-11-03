package sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.techventus.server.voice.Voice;
import com.techventus.server.voice.exception.CaptchaRequiredException;

public class GoogleVoiceConnection {

	static BufferedReader br = new BufferedReader(new InputStreamReader(
			System.in));
	static String userName = null;
	static String pass = null;
	static boolean connectOnStartup = false;
	static Properties testProps = null;
	private Voice voice;

	public static void main(String[] args) {
		GoogleVoiceConnection connection = new GoogleVoiceConnection();

		connection.setup();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\n\rEnter Number to Send SMS:");
		String number;
		try {
			String ans;
			do {
				number = br.readLine();
				System.out.println("Enter Message:");
				String txt = br.readLine();
				connection.sendSMS(number, txt);
				System.out
						.println("Message sent! Would you like to send another?\n");
				ans = br.readLine();
			} while (ans.charAt(0) == 'y');

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Message not sent:");
			e.printStackTrace();
		}
	}

	/**
	 * Reads properties file and creates a new connection to Google Voice.
	 */
	public void setup() {
		String file = "test/privateTestData.properties";
		try {
			testProps = load(file);
		} catch (IOException e1) {
			System.out.println("Could not read " + file + ".");
			e1.printStackTrace();
		}
		userName = testProps.getProperty("username");
		pass = testProps.getProperty("password");

		try {
			voice = new Voice(userName, pass);
		} catch (CaptchaRequiredException captEx) {
			System.out.println("A captcha is required.");
			System.out.println("Image URL  = " + captEx.getCaptchaUrl());
			System.out.println("Capt Token = " + captEx.getCaptchaToken());
			System.out.println("Goodbye.");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("IO error creating voice! - "
					+ e.getLocalizedMessage());
			System.out.println("Goodbye.");
			System.exit(1);
		}
	}

	public void sendSMS(String number, String txt) throws IOException {
		voice.sendSMS(number, txt);
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
		InputStream in = null;

		if (!propsFile.endsWith(".properties"))
			propsFile = propsFile.concat(".properties");

		// Returns null on lookup failures:
		in = ClassLoader.getSystemClassLoader().getResourceAsStream(propsFile);
		if (in != null) {
			result = new Properties();
			result.load(in); // Can throw IOException
		}
		testProps = result;
		return result;
	}

}
