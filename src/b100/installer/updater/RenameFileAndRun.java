package b100.installer.updater;

import static b100.installer.util.Utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import b100.utils.Utils;

public class RenameFileAndRun {
	
	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Usage: --from <path> --to <path>");
			return;
		}
		
		List<String> argsList = Utils.toList(args);
		List<String> runArgs = new ArrayList<String>();
		
		String fromPath = null;
		String toPath = null;
		
		while (argsList.size() > 0) {
			String arg = argsList.remove(0);
			
			if(arg.equals("--from")) {
				fromPath = argsList.remove(0);
			}else if(arg.equals("--to")) {
				toPath = argsList.remove(0);
			}else {
				runArgs.add(arg);
			}
		}
		
		if(fromPath == null) {
			throw new RuntimeException("Missing '--from' arg!");
		}
		if(toPath == null) {
			throw new RuntimeException("Missing '--to' arg!");
		}
		
		File from = new File(fromPath);
		File to = new File(toPath);
		
		boolean success = false;
		for(int i=0; i < 10; i++) {
			System.out.println("Renaming file, attempt " + (i + 1));
			try {
				if(to.delete() && from.renameTo(to)) {
					success = true;
					System.out.println("Success!");
					break;
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(500);
			}catch (Exception e1) {}
		}
		
		if(!success) {
			System.out.println("Could not rename file!");
			return;
		}

		String javaExe = new File(System.getProperty("java.home"), "bin/java.exe").getAbsolutePath();
		
		List<String> cmd = new ArrayList<>();
		cmd.add(javaExe);
		cmd.add("-jar");
		cmd.add(getEscapedPath(to));
		cmd.addAll(runArgs);
		startProcess(cmd);
	}
}
