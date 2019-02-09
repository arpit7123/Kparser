/**
 * 
 */
package kparser.devel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Sets;

/**
 * @author Arpit Sharma
 * @date Jun 6, 2017
 *
 */
public class Utilities {
	
	public static void saveObject(Object c, String path){
	    ObjectOutputStream oos = null;
	    try {
	        oos = new ObjectOutputStream(new FileOutputStream(path));
	    	oos.writeObject(c);
			oos.flush();
	    } catch (FileNotFoundException e1) {
	        e1.printStackTrace();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}
	
	public static ArrayList<String> listFilesForFolder(File folder, boolean scanDirs) {
		HashSet<String> listOfFiles = Sets.newHashSet();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				if(scanDirs){
					listFilesForFolder(fileEntry,scanDirs);
				}
			} else {
				listOfFiles.add(fileEntry.getName());
			}
		}
		return new ArrayList<String>(listOfFiles);
	}
	
	public static Object load(String path){
		Object obj = null;
		try{
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return obj;
	}
}
