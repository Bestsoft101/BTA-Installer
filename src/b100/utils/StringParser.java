package b100.utils;

import java.io.File;
import java.io.InputStream;

public interface StringParser<E>{
	
	public E parseString(String string);

	public default E parseStream(InputStream stream) {
		return parseString(StringUtils.readInputString(stream));
	}
	
	public default E parseFileContent(File file) {
		return parseString(StringUtils.getFileContentAsString(file));
	}
	
	public default E parseFileContent(String path) {
		return parseFileContent(new File(path));
	}
	
	public default E parseWebsite(String url) {
		return parseString(StringUtils.getWebsiteContentAsString(url));
	}
	
}
