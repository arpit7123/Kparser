/**
 * 
 */
package module.graph.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author arpit
 *
 */
public class ObjectFilesIO {
	
	public static Object load(String fileName){
		Object obj = null;
		try{
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return obj;
	}
}
