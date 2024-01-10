package b100.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class Utils {
	
	public static <E> List<E> toList(E[] array){
		List<E> list = new ArrayList<>();
		
		for(int i=0; i < array.length; i++) {
			list.add(array[i]);
		}
		
		return list;
	}
	
	public static <E> E[] toArray(Class<E> clazz, List<E> list) {
		E[] array = createArray(clazz, list.size());
		
		for(int i=0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public static <E> E[] createArray(Class<E> clazz, int length) {
		try{
			return (E[]) Array.newInstance(clazz, length);
		}catch (Exception e) {
			throw new RuntimeException("Creating Array of class "+clazz+" and length "+length);
		}
	}
	
	public static <E> E[] combineArray(Class<E> clazz, E[] array1, E[] array2) {
		E[] newArray = createArray(clazz, array1.length + array2.length);
		
		for(int i=0; i < array1.length; i++) {
			newArray[i] = array1[i];
		}
		
		for(int i=0; i < array2.length; i++) {
			newArray[array1.length + i] = array2[i];
		}
		
		return newArray;
	}
	
	public static <E> E requireNonNull(E e) {
		if(e == null) throw new NullPointerException();
		
		return e;
	}
	
	public static byte[] arraycopyShortToByte(short[] shortArray, byte[] byteArray) {
		return arraycopyShortToByte(shortArray, 0, byteArray, 0, shortArray.length << 1);
	}
	
	public static byte[] arraycopyShortToByte(short[] shortArray, int shortArrayOffset, byte[] byteArray, int byteArrayOffset, int byteCount) {
		int shortArrayLength = byteCount >> 1;
		
		for(int i=0; i < shortArrayLength; i++) {
			int j = (i << 1) + byteArrayOffset;
			
			int shortVal = shortArray[i + shortArrayOffset] & 0xFFFF;
			
			byteArray[j + 0] = (byte) (shortVal >> 8);
			byteArray[j + 1] = (byte) (shortVal >> 0);
		}
		return byteArray;
	}
	
	public static short[] arraycopyByteToShort(byte[] byteArray, short[] shortArray) {
		return arraycopyByteToShort(byteArray, 0, shortArray, 0, byteArray.length);
	}
	
	public static short[] arraycopyByteToShort(byte[] byteArray, int byteArrayOffset, short[] shortArray, int shortArrayOffset, int byteCount) {
		int shortArrayLength = byteCount >> 1;
		
		for(int i=0; i < shortArrayLength; i++) {
			int j = (i << 1) + byteArrayOffset;
			
			int byte0 = byteArray[j + 0] & 0xFF;
			int byte1 = byteArray[j + 1] & 0xFF;
			
			shortArray[i + shortArrayOffset] = (short) (byte0 << 8 | byte1);
		}
		return shortArray;
	}
	
}
