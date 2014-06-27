package sadiasrc.util;

import java.util.ArrayList;
import java.util.Iterator;

public class MultiIterator<E> implements Iterator<E> {
	Iterator<Iterator<? extends E>> iters;
	Iterator<? extends E> current;
	Iterator<? extends E> last;

	public MultiIterator(Iterator<Iterator<? extends E>> iters) {
		this.iters = iters;
		if (iters.hasNext()) {
			this.current = iters.next();
		} else {
			this.current = new ArrayList<E>(1).iterator();
		}

		this.last = this.current;
	}

	public boolean hasNext() {
		while (this.iters.hasNext() && !this.current.hasNext()) {
			this.current = this.iters.next();
		}
		return this.current.hasNext();
	}

	public E next() {
		this.last = this.current;
		return this.current.next();
	}

	public void remove() {
		this.last.remove();
	}
}
