package exceptions;

@SuppressWarnings("serial")
public class InvalidGraphFileFormatException extends RuntimeException {
	public InvalidGraphFileFormatException(String message) {
		super(message);
	}
}
