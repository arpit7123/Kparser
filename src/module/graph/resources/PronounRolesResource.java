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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import module.graph.helper.Configurations;

public class PronounRolesResource implements IResource {

	private HashMap<String,String> pronounRolesMap = new HashMap<String,String>();
	
	public PronounRolesResource() {
		try{
			String pronounRolesFile = Configurations.getProperty("pronounRolesFile");
			if(pronounRolesFile.startsWith("./")){
				pronounRolesFile = Configurations.getCWD() + pronounRolesFile.substring(2);
			}
			pronounRolesMap = populatePronounsMap(pronounRolesFile);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the pronoun roles file !!!");
		}
	}
	
	public boolean containsKey(String key) {
		return pronounRolesMap.containsKey(key);
	}
	
	public String get(String key) {
		return pronounRolesMap.get(key);
	}
	
	private HashMap<String,String> populatePronounsMap(String pronounRolesFile){
		HashMap<String,String> pronounRolesMap = new HashMap<String,String>();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(pronounRolesFile));
			String line = null;
			while((line=br.readLine())!=null){
				String[] temp = line.split("\t");
				pronounRolesMap.put(temp[0].trim(), temp[1].trim());
			}
			br.close();
		}catch(IOException e){
//			e.printStackTrace();
			System.err.println("Pronoun roles file not read properly !!!");
		}
		return pronounRolesMap;
	}
}
