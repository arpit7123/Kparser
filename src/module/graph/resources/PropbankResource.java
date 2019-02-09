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

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import module.graph.helper.Configurations;
import kparser.devel.PropbankData;

import com.google.common.collect.Table;

public class PropbankResource implements IResource {

	private Table<String,String,String> SRLTable;
	private HashMap<String,PropbankData> mapOfPropbank;
	private HashSet<String> allVerbs;
	private HashSet<String> allAdjectives;
	
	public PropbankResource() {
		try{
			String fileName = Configurations.getProperty("semRoleTableFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			SRLTable = loadFrameFilesTable(fileName);
			
			fileName = Configurations.getProperty("propBnkDataMapFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			mapOfPropbank = loadPropbankDataMap(fileName);
			
			fileName = Configurations.getProperty("propBnkVerbsSetFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			allVerbs = loadSetFile(fileName);
			
			fileName = Configurations.getProperty("propBnkAdjsSetFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			allAdjectives = loadSetFile(fileName);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the propbank data files !!!");
		}
	}
	
	public String getSemanticRole(String key, String group) {
		return SRLTable.get(key, group);
	}
	
	public PropbankData getPropbankData(String driverVerb) {
		PropbankData result = null;
		if(driverVerb.equalsIgnoreCase("move")){
			result = new PropbankData();
			result.setDriverVerb("move");
			ArrayList<String> listOfVerbs = new ArrayList<String>();
			listOfVerbs.add("move_out");
			result.setVerbs(listOfVerbs);
		}
		
		if(driverVerb.equalsIgnoreCase("zoom")){
			result = new PropbankData();
			result.setDriverVerb("zoom");
			ArrayList<String> listOfVerbs = new ArrayList<String>();
			listOfVerbs.add("zoom_by");
			result.setVerbs(listOfVerbs);
		}
		
		if(mapOfPropbank.containsKey(driverVerb)){
			if(result!=null){
				ArrayList<String> listOfVerbs = result.getVerbs();
				listOfVerbs.addAll(mapOfPropbank.get(driverVerb).getVerbs());
				result.setVerbs(listOfVerbs);
			}else{
				return mapOfPropbank.get(driverVerb);
			}
		}
		return result;
	}
	
	public boolean verbUsedAsAdj(String verb){
		if(verb.equalsIgnoreCase("concerned")){
			return true;
		}
		if(allVerbs!=null && allAdjectives!=null){
			if(allVerbs.contains(verb)){
				if(allAdjectives.contains(verb)|| allAdjectives.contains(verb+"ed")){
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private Table<String,String,String> loadFrameFilesTable(String fileName){
		Table<String,String,String> table = null;
		try{
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(fin);   
			table = (Table<String,String,String>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.out.println("Semantic roles map-file not found.");
			System.err.println("File name is: "+fileName);
			return table;
		}
		return table;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String,PropbankData> loadPropbankDataMap(String fileName){
		HashMap<String,PropbankData> table = null;
		try{
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(fin);   
			table = (HashMap<String,PropbankData>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.out.println("Propbank data map-file not found.");
			System.err.println("File name is: "+fileName);
			return table;
		}
		return table;
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<String> loadSetFile(String fileName){
		HashSet<String> table = null;
		try{
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(fin);   
			table = (HashSet<String>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.err.println("Propbank set-file not found.");
			System.err.println("File name is: "+fileName);
			return table;
		}
		table.add("disappointed");
		table.add("gripped");
		table.add("ached");
		return table;
	}
	
	public static void main(String[] args){
		PropbankResource pr = new PropbankResource();
		System.out.println(pr.verbUsedAsAdj("concern"));
		System.exit(0);
	}
}
