/**
 * 
 */
package module.graph.helper;

/**
 * @author Arpit Sharma
 * @date Aug 7, 2017
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurations {
	
	private static String configFile = null;
	private static String path = null;
	private static Properties props = null;
	
	static{
		try{
			path = Configurations.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			path = path.substring(0,path.lastIndexOf("/")+1) + "resources/";
			configFile = path + "config.properties";
		}catch(Exception e){
			System.err.println("ERROR: Could not load the articles file !!!");
		}	
		
		props = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(configFile);
			// load a properties file
			props.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getProperty(String propName){
		return props.getProperty(propName);
	}
	
	public static String getCWD(){
		return path;
	}
	
	
  public static void main(String[] args) {
	  System.out.println(Configurations.getProperty("clingopath"));

  }
}