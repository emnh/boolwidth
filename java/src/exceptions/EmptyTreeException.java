package exceptions;

/**
 * Runtime exception thrown when one tries to access the root of an empty tree.
 */

@SuppressWarnings("serial")
public class EmptyTreeException extends RuntimeException {
	public EmptyTreeException(String err) {
		super(err);
	}
}
