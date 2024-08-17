package tn.famytech.esprit.utils;

public class FileUtils {

	 public static String getFileExtension(String filename) {
	        if (filename == null || filename.isEmpty()) {
	            return "";
	        }
	        int lastDotIndex = filename.lastIndexOf('.');
	        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1);
	    }
}
