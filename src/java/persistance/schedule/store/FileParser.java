package persistance.schedule.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {

	public FileParser(File schedule) {
		BufferedReader reader = null;
		// Oh Java's unnecessary exception handling
		try {
			reader = new BufferedReader(new FileReader(schedule));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		String line;
		try {
			while( (line = reader.readLine()) != null) {
				parseLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void parseLine(String line) {
		// TODO Auto-generated method stub
		
	}
}
