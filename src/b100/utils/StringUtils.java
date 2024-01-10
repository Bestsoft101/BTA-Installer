package b100.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public abstract class StringUtils {
	
	public static String getFileContentAsString(String path) {
		validateStringNotEmpty(path);
		
		return getFileContentAsString(new File(path));
	}
	
	public static String getFileContentAsString(File file) {
		FileUtils.validateFileExists(file);
		
		try{
			return readInputString(new FileInputStream(file));
		}catch (Exception e) {
			throw new RuntimeException("Error while reading file", e);
		}
	}
	
	public static void saveStringToFile(String path, String content) {
		validateStringNotEmpty(path);
		validateStringNotEmpty(content);
		
		saveStringToFile(new File(path).getAbsoluteFile(), content);
	}
	
	public static void saveStringToFile(File file, String content) {
		FileUtils.createNewFile(file);
		validateStringNotEmpty(content);
		
		if(file == null) throw new NullPointerException();
		if(content == null) throw new NullPointerException();
		
		try {
			FileUtils.createNewFile(file);
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		}catch (Exception e) {
			throw new RuntimeException(file.getAbsolutePath(), e);
		}
	}
	
	public static String readInputString(InputStream inputStream) {
		if(inputStream == null) throw new NullPointerException();
		
		try {
			InputStreamReader reader = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(reader);
			
			StringBuilder builder = new StringBuilder();
			String line = null;
			boolean firstLine = true;
			
			while((line = br.readLine()) != null) {
				if(firstLine) {
					firstLine = false;
				}else {
					line = "\n" + line;
				}
				
				builder.append(line);
			}
			
			br.close();
			reader.close();
			
			return builder.toString();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getWebsiteContentAsString(String url) {
		validateStringNotEmpty(url);
		
		URL u = null;
		InputStream is = null;
		
		try {
			u = new URL(url);
		} catch (Exception e) {
			throw new RuntimeException(url, e);
		}
		
		try {
			is = u.openStream();
		}catch (Exception e) {
			throw new RuntimeException(u.toString(), e);
		}
		
		return readInputString(is);
	}
	
	public static boolean isStringEmpty(String string) {
		return string == null || string.length() == 0;
	}
	
	public static void validateStringNotEmpty(String string) {
		if(string == null) throw new NullPointerException();
		if(string.length() == 0) throw new RuntimeException("Empty String");
	}
	
	public static String[] toArray(List<String> list) {
		String[] array = new String[list.size()];
		
		for(int i=0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		
		return array;
	}

	public static String getResourceAsString(String string) {
		return readInputString(StringUtils.class.getResourceAsStream(string));
	}
	
}
