package b100.installer.gui.classic;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GridPanel extends JPanel {
	
	private GridBagConstraints c = new GridBagConstraints();
	
	public double defaultWeightX = 0.0;
	public double defaultWeightY = 0.0;
	
	public GridPanel(int insets, double defaultWeightX, double defaultWeightY) {
		this();
		
		this.c.insets = new Insets(insets, insets, insets, insets);
		this.defaultWeightX = defaultWeightX;
		this.defaultWeightY = defaultWeightY;
	}
	
	public GridPanel() {
		setLayout(new GridBagLayout());
		
		c.fill = GridBagConstraints.BOTH;
	}
	
	public Component add(Component component, int x, int y) {
		setConstraints(x, y, defaultWeightX, defaultWeightY, 1, 1);
		super.add(component, c);
		return component;
	}
	
	public Component add(Component component, int x, int y, double weightX, double weightY) {
		setConstraints(x, y, weightX, weightY, 1, 1);
		super.add(component, c);
		return component;
	}
	
	public Component add(Component component, int x, int y, double weightX, double weightY, int width, int height) {
		setConstraints(x, y, weightX, weightY, width, height);
		super.add(component, c);
		return component;
	}
	
	private void setConstraints(int x, int y, double weightX, double weightY, int width, int height) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		c.weightx = weightX;
		c.weighty = weightY;
	}
	
	public GridBagConstraints getGridBagConstraints() {
		return c;
	}
	
	@Override
	@Deprecated
	public Component add(Component comp) {
		return super.add(comp);
	}
	
	@Override
	@Deprecated
	public Component add(Component comp, int index) {
		return super.add(comp, index);
	}
	
	@Override
	@Deprecated
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
	}
	
	@Override
	@Deprecated
	public void add(Component comp, Object constraints, int index) {
		super.add(comp, constraints, index);
	}
	
	@Override
	@Deprecated
	public Component add(String name, Component comp) {
		return super.add(name, comp);
	}
	
}
