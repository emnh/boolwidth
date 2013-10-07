package interfaces;

/**
 * An interface for a position, which is a holder object storing a single
 * element.
 * 
 * @author Roberto Tamassia, Michael Goodrich
 */
public interface IPosition<E> extends ISetPosition {
	/** Return the element stored at this position. */
	E element();

	void setElement(E element);
	// int id();
	// void setId(int newId);
}
