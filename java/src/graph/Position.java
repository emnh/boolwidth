package graph;

import interfaces.IPosition;

/** Implementation of a position */
public class Position<T> implements IPosition<T> {

	/** The element stored at this position. */
	protected T elem;
	protected int id;

    // For serialization only
    @Deprecated
    public Position() {

    }

	Position(T element, int id) {
		this.elem = element;
		this.id = id;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        if (id != position.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    /** Returns the element stored at this position. */
	public T element() {
		return this.elem;
	}

	public int id() {
		return this.id;
	}

	/** Sets the element stored at this position. */
	public void setElement(T o) {
		this.elem = o;
	}

	public void setId(int newId) {
		this.id = newId;
	}
}