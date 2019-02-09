/*******************************************************************************
 * Copyright (C) 2017 Arpit Sharma
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package module.graph.resources;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;

import module.graph.helper.Configurations;

/**
 * @author arpit
 *
 */
public class AdjectiveClassResource implements IResource{

	private HashMap<String,String> mapOfAdjClasses;

	public AdjectiveClassResource() {
		try{
			String adjClassMapFile = Configurations.getProperty("adjClassMapFile");
			if(adjClassMapFile.startsWith(".")){
				adjClassMapFile = Configurations.getCWD()+adjClassMapFile.substring(2);
			}
			mapOfAdjClasses = loadAdjClasses(new FileInputStream(adjClassMapFile));
		}catch(Exception e){
			System.err.println("ERROR: Could not load the adjectives classes' resource file !!!");
		}
	}

	public String getAdjectiveClass(String adjective){
		if(mapOfAdjClasses.containsKey(adjective.toLowerCase())){
			return mapOfAdjClasses.get(adjective);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String,String> loadAdjClasses(InputStream fileName){
		HashMap<String,String> mapOfAdjClasses = null;
		try{
			InputStream fin = fileName;
			ObjectInputStream is = new ObjectInputStream(fin);   
			mapOfAdjClasses = (HashMap<String,String>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.out.println("Map of adjective classes' file not found.");
			return mapOfAdjClasses;
		}
		return mapOfAdjClasses;
	}

	public static void main(String args[]){
//		AdjectiveClassResource acr = new AdjectiveClassResource();
		//		if(acr.mapOfAdjClasses.containsKey("tasty")){
		//			System.out.println("Yes! it contains!");
		//		}else{
		//			System.out.println("No! it does not contain!");
		//		}
//		HashSet<String> set = new HashSet<String>();

		try(BufferedReader br = new BufferedReader(new FileReader("prep_rels.txt"))){

			String line = null;
			while((line=br.readLine())!=null){
				String prep = "";
				HashSet<String> rels = new HashSet<String>();
				while(!line.trim().equalsIgnoreCase("")){
					if(line.trim().endsWith(":::")){
						prep=line.trim().substring(0, line.trim().length()-3);
					}else{
						rels.add(line.trim());
					}
					line=br.readLine();
				}

				//				if(prep.equalsIgnoreCase("towards")){
				//					System.err.println("We are here !!!");
				//				}

				if(rels.size()!=0){
					System.out.println("<tr>");
					System.out.println("<td rowspan=\""+(rels.size()+1)+"\">"+prep+"</td>");
					System.out.println("</tr>");

					for(String s : rels){
						System.out.println("<tr>");
						System.out.println("<td>"+s+"</td>");
						System.out.println("</tr>");	
					}
				}


			}
		}catch(IOException e){
			e.printStackTrace();
		}

		//		for(String ss : set){
		//			System.out.println(ss);
		//		}
	}

}
