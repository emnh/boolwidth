package sadiasrc.util;

import java.util.ArrayList;
import java.util.Collection;

public class InPlaceArrayList<E> extends ArrayList<E> {

	
	/* 
	 * Swaps the last element in the list with the element at index i, 
	 * then removes the last element in the list.
	 * @return returns the removed element
	 */
	@Override
 	public E remove(int index) {
		E toremove = get(index);
		if(index >= size()-1 || index <0)
			return super.remove(index);
		
		set(index, super.remove(size()-1));
		
		return toremove;
	}
}
