package sadiasrc.exceptions;

/**
 * Runtime exception thrown when one tries to add a root to an non empty tree.
 */

@SuppressWarnings("serial")
public class NonEmptyTreeException extends RuntimeException {
	public NonEmptyTreeException(String err) {
		super(err);
	}
}
