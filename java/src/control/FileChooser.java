package control;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileChooser {

	public static File choose() throws FileNotFoundException {
		File file = null;

		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else {
			throw new FileNotFoundException();
		}
		return file;
	}
}
