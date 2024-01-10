package b100.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class FileUtils {
	
	public static void createFolder(File file) {
		try {
			if(file == null) throw new NullPointerException();
			
			if(file.exists()) {
				if(file.isDirectory()) return;
				else file.delete();
			}
			if(!file.mkdirs()) {
				throw new RuntimeException("Folder not created!");
			}
		}catch (Exception e) {
			throw new RuntimeException("Creating Folder: "+file);
		}
	}
	
	public static void createFolderForFile(File file) {
		if(file == null) throw new NullPointerException();
		File parent = file.getParentFile();
		if(parent != null) createFolder(parent);
	}
	
	public static File createNewFile(File file) {
		if(file == null) throw new NullPointerException();
		
		if(file.exists()) {
			file.delete();
		}
		createFile(file);
		return file;
	}
	
	public static void createFile(File file) {
		if(file == null) throw new NullPointerException();
		if(file.exists()) return;
		
		try {
			createFolderForFile(file);
			if(!file.createNewFile()) {
				throw new RuntimeException("File not created!");
			}
		}catch (Exception e) {
			throw new RuntimeException("Creating File: "+file, e);
		}
	}
	
	public static String getFileExtension(File file) {
		if(file == null) throw new NullPointerException();
		
		String path = file.getAbsoluteFile().getAbsolutePath();
		
		String ext = "";
		
		for(int i=0; i < path.length(); i++) {
			char c = path.charAt(i);
			
			if(c == '/' || c == '\\') {
				ext = "";
			}else if(c == '.') {
				ext = "";
			}else {
				ext += c;
			}
		}
		
		return ext;
	}
	
	public static String getFileExtension(String path) {
		StringUtils.validateStringNotEmpty(path);
		
		return getFileExtension(new File(path));
	}
	
	public static void copyAll(String from, String to) {
		StringUtils.validateStringNotEmpty(from);
		StringUtils.validateStringNotEmpty(to);
		
		copyAll(new File(from), new File(to));
	}
	
	public static void copy(String from, String to) {
		StringUtils.validateStringNotEmpty(from);
		StringUtils.validateStringNotEmpty(to);
		
		copy(new File(from), new File(to));
	}
	
	public static void copyAll(File from, File to) {
		if(from == null) throw new NullPointerException();
		if(to == null) throw new NullPointerException();
		
		List<File> files = getAllFiles(from);
		
		String fromPath = from.getAbsolutePath();
		String toPath = to.getAbsolutePath();
		
		for(File oldFile : files) {
			String oldPath = oldFile.getAbsolutePath();
			String newPath = oldPath;
			if(!newPath.startsWith(fromPath)) {
				throw new RuntimeException(oldFile.getAbsolutePath());
			}
			newPath = toPath + newPath.substring(fromPath.length());
			
			File newFile = new File(newPath);
			copy(oldFile, newFile);
		}
	}
	
	public static void copy(File from, File to) {
		if(from == null) throw new NullPointerException();
		if(to == null) throw new NullPointerException();
		if(!from.exists()) throw new RuntimeException("File doesn't exist: "+from.getAbsolutePath());
		
		try {
			createNewFile(to);
			
			InputStream in = new FileInputStream(from);
			OutputStream out = new FileOutputStream(to);
			
			StreamUtils.transferData(in, out);
			
			in.close();
			out.close();
		}catch (Exception e) {
			throw new RuntimeException("Could not copy from "+from.getAbsolutePath()+" to "+to.getAbsolutePath(), e);
		}
	}
	
	public static List<File> getAllFiles(File folder){
		return getAllFiles(folder, false);
	}
	
	public static List<File> getAllFiles(List<File> files, File folder) {
		return getAllFiles(files, folder, false);
	}
	
	public static List<File> getAllFiles(File folder, boolean includeFolders){
		if(folder == null) throw new NullPointerException();
		
		return getAllFiles(new ArrayList<>(), folder, includeFolders);
	}
	
	public static List<File> getAllFiles(List<File> files, File folder, boolean includeFolders) {
		if(files == null) throw new NullPointerException();
		if(folder == null) throw new NullPointerException();
		
		File[] files2 = folder.listFiles();
		for(File file : files2) {
			if(file.isFile()) {
				files.add(file);
			}
			if(file.isDirectory()) {
				getAllFiles(files, file, includeFolders);
				if(includeFolders) {
					files.add(file);
				}
			}
		}
		return files;
	}
	
	public static void validateFileExists(File file) {
		if(file == null) throw new NullPointerException();
		if(!file.exists()) throw new RuntimeException("File "+file+" doesn't exist!");
		if(!file.isFile()) throw new RuntimeException("Not a file: "+file);
	}
	
	public static void validateFolderExists(File folder) {
		if(folder == null) throw new NullPointerException();
		if(!folder.exists()) throw new RuntimeException("Folder "+folder+" doesn't exist!");
		if(!folder.isDirectory()) throw new RuntimeException("Not a folder: "+folder);
	}
	
	public static void downloadFile(String url, File file) throws IOException {
		downloadFile(new URL(url), file);
	}
	
	public static void downloadFile(URL url, File file) throws IOException {
		FileUtils.createNewFile(file);
		
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		
		StreamUtils.transferDataAndClose(in, out);
	}
	
}
