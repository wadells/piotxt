package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Various utilites for eaisly reading and saving Strings to files.
 */
public class FileUtils {

	/**
	 * Reads the content of a file into a string.
	 * 
	 * @param f
	 *            the file to read
	 * @return the contents of the file
	 * @throws IOException
	 *             when the file does not exist or cannot be read
	 */
	public static String readFile(File f) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String nl = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(nl);
		}
		return stringBuilder.toString();
	}

	/**
	 * Reads the content of a file into a string.
	 * 
	 * @param url
	 *            the location of the file
	 * @return the contents of the file
	 * @throws IOException
	 *             when the file does not exist or cannot be read
	 */
	public static String readFile(String url) throws IOException {
		File f = new File(url);
		return readFile(f);
	}

	/**
	 * Writes a string to a file.
	 * 
	 * @param f
	 *            the file to write to
	 * @param contents
	 *            the text to write to the file
	 * @param overwrite
	 *            if the writer should overwrite a preexisting file
	 * @throws IOException
	 *             if the file cannot be written to
	 */
	public static void writeFile(File f, String contents, boolean overwrite)
			throws IOException {
		if (!overwrite && f.exists()) {
			throw new IOException("File already exists: " + f.getAbsolutePath());
		}
		FileWriter out = new FileWriter(f);
		out.write(contents);
		out.close();
	}

	/**
	 * Writes a string to a file.
	 * 
	 * @param url
	 *            the location of the file
	 * @param contents
	 *            the text to write to the file
	 * @param overwrite
	 *            if the writer should overwrite a preexisting file
	 * @throws IOException
	 *             if the file cannot be written to
	 */
	public static void writeFile(String url, String contents, boolean overwrite)
			throws IOException {
		File f = new File(url);
		writeFile(f, contents, overwrite);
	}

}
