package b100.utils;

import java.util.Iterator;

import b100.utils.interfaces.Listener;

public class ArrayIterator<E> implements Iterator<E>, Iterable<E>{
	
	private int pos;
	private E[] array;
	
	public ArrayIterator(E[] array) {
		this.array = array;
		this.pos = 0;
	}
	
	public boolean hasNext() {
		return pos < array.length;
	}

	public E next() {
		return array[pos++];
	}
	
	public Iterator<E> iterator() {
		return this;
	}
	
	public static <E> void forEach(E[] arr, Listener<E> listener) {
		Utils.requireNonNull(arr);
		Utils.requireNonNull(listener);
		for(int i=0; i < arr.length; i++) {
			listener.listen(arr[i]);
		}
	}
	
}
