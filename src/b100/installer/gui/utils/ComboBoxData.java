package b100.installer.gui.utils;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class ComboBoxData<E> implements ComboBoxModel<E> {
	
	private List<ListDataListener> listDataListeners = new ArrayList<>();
	public List<E> content = new ArrayList<>();
	private Object selectedItem;
	
	public ComboBoxData() {
		
	}
	
	public ComboBoxData(List<E> content) {
		this.content.addAll(content);
	}
	
	@Override
	public int getSize() {
		return content.size();
	}

	@Override
	public E getElementAt(int index) {
		return content.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(l);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}
}