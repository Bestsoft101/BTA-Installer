package b100.installer.gui.modern.util;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.function.Consumer;

import b100.installer.gui.modern.element.GuiElement;

public final class ListenerList<ListenerType> {
	
	private GuiElement parentElement;
	private ArrayList<ListenerType> listeners = new ArrayList<>();
	private ArrayList<ListenerType> previousListeners = new ArrayList<>();
	private boolean iterating = false;
//	private String name;
	
	public ListenerList(GuiElement parentElement) {
		this.parentElement = parentElement;
	}
	
	public GuiElement add(ListenerType e) {
		if(iterating) {
			throw new ConcurrentModificationException("Modified list " + this + " while iterating!");
		}
		
		if(listeners.contains(e)) {
			listeners.remove(e);
		}
		
		listeners.add(e);
		return parentElement;
	}
	
	public boolean remove(ListenerType e) {
		if(iterating) {
			throw new ConcurrentModificationException("Modified list " + this + " while iterating!");
		}
		
		return listeners.remove(e);
	}
	
	public void forEach(Consumer<ListenerType> action) {
		if(listeners.size() == 0) {
			return;
		}

		if(iterating) {
			throw new RuntimeException("Already iterating " + this);
		}
		
		iterating = true;
		
		previousListeners.addAll(listeners);
		
		for(ListenerType listener : previousListeners) {
			action.accept(listener);
		}
		
		previousListeners.clear();
		
		iterating = false;
	}
	
//	@Override
//	public String toString() {
//		return parentElement.getClass().getSimpleName() + "." + getName() + "[" + listeners.size() + "] in " + parentElement;
//	}
//	
//	private String getName() {
//		if(name != null) {
//			return name;
//		}
//		try {
//			List<Field> allFields = ReflectUtils.getAllFields(parentElement.getClass());
//			
//			for(Field field : allFields) {
//				int mod = field.getModifiers();
//				if(field.getType() == ListenerList.class && !Modifier.isStatic(mod)) {
//					field.setAccessible(true);
//					Object obj = field.get(parentElement);
//					if(obj == this) {
//						return field.getName();
//					}
//				}
//			}
//			
//			FullscreenFix.print("All Fields: ");
//			for(Field field : allFields) {
//				FullscreenFix.print("    " + field.getType().getName() + " " + field.getName());	
//			}
//			
//			throw new RuntimeException("Could not find list field in class " + parentElement.getClass().getName() + "!");
//		}catch (Exception e) {
//			throw new RuntimeException("Could not get list name!", e);
//		}
//	}

}
