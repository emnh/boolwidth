package sadiasrc.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.List;

/** Get graphs from disk **/
public class DiskGraph {

	private static final List<String> GRAPH_EXT = Arrays
	.asList("dgf", "dimacs","clq","txt","mis","sadia");

	@SuppressWarnings("unused")
	private final static String SOURCE_FILENAME_FIELD = "sourceFileName";
	
	public List<File> getGraphs(String path) throws FileNotFoundException
	{
		File startingDirectory= new File(path);
	    List<File> files = getFileListing(startingDirectory);

	   	return files;
	  }
/**
 * Recursively walk a directory tree and return a List of all
 * Files found; the List is sorted using File.compareTo().
 *
 * @param aStartingDir is a valid directory, which can be read.
 */
 static public List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
	 validateDirectory(aStartingDir);
	 List<File> result = getFileListingNoSort(aStartingDir);
	 Collections.sort(result);
	 return result;
 }
 
 /**
  * Directory is valid if it exists, does not represent a file, and can be read.
  */
  static private void validateDirectory (File aDirectory)
  	throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }
 
static private List<File> getFileListingNoSort(File aStartingDir)
	throws FileNotFoundException {
	
	// It is also possible to filter the list of returned files.
	// This example does not return any files that start with `.'.
	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return !name.startsWith(".");
	    }
	};
	    List<File> result = new ArrayList<File>();
	    File[] filesAndDirs = aStartingDir.listFiles(filter);
	    List<File> filesDirs = Arrays.asList(filesAndDirs);
	    for(File file : filesDirs) {
	    	if (file.isFile() )
	    	{
	    		String ext = file.toString().replaceFirst("^.*\\.", "");
				if (GRAPH_EXT.contains(ext)) 
					result.add(file); //always add, even if directory
	    	}
	    	if ( ! file.isFile() ) {
	    		//must be a directory
	    		//recursive call!
	    		List<File> deeperList = getFileListingNoSort(file);
	    		result.addAll(deeperList);
	      }
	    }
	    return result;
	  }

}
