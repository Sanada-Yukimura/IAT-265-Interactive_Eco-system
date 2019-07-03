package sound;

import java.io.FileInputStream;
import java.io.InputStream;

public class MinimHelper {

	public MinimHelper() {

	}

	public String sketchPath(String fileName) {
		return "assets\\" + fileName;
	}

	public InputStream createInput(String fileName) {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(sketchPath(fileName));
		} catch (Exception e) {

		}
		return inStream;
	}

}
