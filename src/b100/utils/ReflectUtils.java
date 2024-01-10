package b100.utils;

import static b100.utils.Utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class ReflectUtils {
	
	public static Field getField(Class<?> clazz, String... names) {
		if(clazz == null) throw new NullPointerException();
		if(names.length == 0) throw new RuntimeException("No Name!");
		
		try {
			for(String name : names) {
				try {
					Field field = clazz.getDeclaredField(name);
					return field;
				}catch (Exception e) {}
				try {
					Field field = clazz.getField(name);
					return field;
				}catch (Exception e) {}
			}
			StringBuilder allNames = new StringBuilder();
			for(int i=0; i < names.length; i++) {
				if(i > 0) allNames.append(' ');
				allNames.append(names[i]);
			}
			
			throw new RuntimeException("Class "+clazz.getName()+" doesn't have any of these Fields: "+allNames.toString());
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <E> E getValue(Field field, Object object, Class<E> clazz) {
		if(field == null) throw new NullPointerException("Field is null!");
		if(clazz == null) throw new NullPointerException("Class is null!");
		
		setFieldAccessible(field);
		
		Object value = getValue(field, object);
		
		if(value == null) return null;
		
		if(value.getClass().isAssignableFrom(clazz)) {
			return clazz.cast(value);
		}else {
			throw new ClassCastException(value.getClass().getName()+" cannot be cast to "+clazz.getName());
		}
	}
	
	public static Object getValue(Field field, Object object) {
		if(field == null) throw new NullPointerException("Field is null!");
		
		if(!isStatic(field) && object == null) {
			throw new NullPointerException("Object is null!");
		}
		
		setFieldAccessible(field);
		
		try{
			return field.get(object);
		}catch (Exception e) {
			throw new RuntimeException("Could not get value!", e);
		}
	}
	
	public static void setFieldAccessible(Field field) {
		try {
			field.setAccessible(true);
		}catch (Exception e) {
			throw new RuntimeException("Could not set field accessible: "+field+": "+e.getClass().getName()+": "+e.getMessage(), e);
		}
	}
	
	public static float getFloatValue(Field field, Object object) {
		return getValue(field, object, Float.class);
	}
	
	public static void setValue(Field field, Object object, Object value) {
		try{
			field.set(object, value);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void printAllStaticFields(Class<?> clazz) {
		if(clazz == null) throw new NullPointerException("Class is null!");
		for(Field field : Utils.combineArray(Field.class, clazz.getFields(), clazz.getDeclaredFields())) {
			if(isStatic(field)) printField(field, null);
		}
	}
	
	public static void printAllFields(Class<?> clazz, Object object) {
		if(clazz == null) throw new NullPointerException("Class is null!");
		if(object == null) throw new NullPointerException("Object is null!");
		for(Field field : Utils.combineArray(Field.class, clazz.getFields(), clazz.getDeclaredFields())) {
			if(!isStatic(field)) printField(field, object);
		}
	}
	
	private static void printField(Field field, Object object) {
		try {
			field.setAccessible(true);
			Object value = field.get(object);
			System.out.println(field.getType().getName()+" "+field.getName()+" = "+value);
		}catch (Exception e) {
			System.out.println(field.getType().getName()+" "+field.getName()+" (Could not get Value: "+e.getClass().getName()+": "+e.getMessage()+")");
		}
	}
	
	public static boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}
	
	public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass, F instance) {
		return getAllObjects(fromClass, ofClass, instance, null);
	}
	
	public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass) {
		return getAllObjects(fromClass, ofClass, null);
	}
	
	/**
	 * Get all objects of type E in class F as array
	 * @param fromClass The Class that has fields of class E
	 * @param instance The instance where to get the objects, null for static values
	 */
	@SuppressWarnings("unchecked")
	public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass, F instance, Condition<Field, E> condition) {
		List<E> list = new ArrayList<>();
		Field[] fields = fromClass.getDeclaredFields();
		for(Field field : fields) {
			try {
				E e = (E) field.get(instance);
				if(e != null && ofClass.isAssignableFrom(e.getClass())) {
					if(condition == null || condition.isTrue(field, e)) {
						list.add(e);
					}
				}
			}catch (Exception e) {}
		}
		return toArray(ofClass, list);
	}
	
	public static interface Condition<E, F>{
		
		public boolean isTrue(E e, F f);
		
	}
	
}
