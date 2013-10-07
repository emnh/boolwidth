package util;

// got it off the net

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCloner {
	// returns a deep copy of an object
	@SuppressWarnings("unchecked")
	static public <T> T deepCopy(T oldObj) throws Exception {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
			oos = new ObjectOutputStream(bos); // B
			// serialize and pass the object
			oos.writeObject(oldObj); // C
			oos.flush(); // D
			ByteArrayInputStream bin = new ByteArrayInputStream(bos
					.toByteArray()); // E
			ois = new ObjectInputStream(bin); // F
			// return the new object
			return (T) ois.readObject(); // G
		} catch (Exception e) {
			System.out.println("Exception in ObjectCloner = " + e);
			throw e;
		} finally {
			oos.close();
			ois.close();
		}
	}

	// so that nobody can accidentally create an ObjectCloner object
	private ObjectCloner() {
	}

}