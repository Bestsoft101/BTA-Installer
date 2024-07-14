package b100.installer.gui.utils;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.border.TitledBorder;

import b100.installer.Utils;

public abstract class GuiUtils {
	
	public static GridPanel createTitledPanel(Component component, String title) {
		GridPanel panel = new GridPanel();
		panel.setBorder(new TitledBorder(title));
		panel.getGridBagConstraints().insets.set(4, 4, 4, 4);
		panel.add(component, 0, 0, 1, 1);
		return panel;
	}
	
	public static ImagePanel createImagePanel(String path) {
		BufferedImage image = Utils.readImage(path);
		if(image == null) {
			throw new NullPointerException("Image is null!");
		}
		return new ImagePanel(image);
	}


}
