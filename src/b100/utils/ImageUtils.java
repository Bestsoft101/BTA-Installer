package b100.utils;

import static b100.utils.Utils.*;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class ImageUtils {
	
	public static void copyImageToClipboard(BufferedImage image) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TransferableImage(image), null);
	}
	
	public static BufferedImage loadExternalImage(String path) {
		return loadExternalImage(new File(path));
	}
	
	public static BufferedImage loadExternalImage(File file) {
		Utils.requireNonNull(file);

		if(!file.exists()) {
			throw new RuntimeException("File doesn't exist: "+file.getAbsolutePath());
		}
		if(!file.isFile()) {
			throw new RuntimeException("Not a file: "+file.getAbsolutePath());
		}
		
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
		}catch (Exception e) {
			throw new RuntimeException("Could not open file", e);
		}
		
		BufferedImage image;
		try {
			image = ImageIO.read(stream);
		}catch (Exception e) {
			throw new RuntimeException("Could not read Image", e);
		}finally {
			StreamUtils.close(stream);
		}
		
		return image;
	}
	
	public static void saveExternalImage(BufferedImage image, String path) {
		saveExternalImage(image, new File(path));
	}
	
	public static void saveExternalImage(BufferedImage image, File file) {
		saveExternalImage(image, "png", file);
	}
	
	public static void saveExternalImage(BufferedImage image, String format, File file) {
		FileUtils.createNewFile(file);
		
		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			throw new RuntimeException("Error saving Image", e);
		}
	}
	
	private static class TransferableImage implements Transferable{

		private Image image;
		
		public TransferableImage(Image image) {
			this.image = requireNonNull(image);
		}
		
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor == DataFlavor.imageFlavor;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if(flavor == DataFlavor.imageFlavor) {
				return image;
			}else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
		
	}
	
}
