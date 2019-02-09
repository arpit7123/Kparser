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
import java.util.HashMap;
import java.util.TreeSet;

import module.graph.helper.Configurations;

/**
 * @author arpit
 *
 */
public class ContractionsResourse implements IResource{

	private TreeSet<String> setOfContractions;
	private HashMap<String,String> mapOfContractions;
	
	public ContractionsResourse() {
		try{
			String contFileName = Configurations.getProperty("contFileName");
			if(contFileName.startsWith("./")){
				contFileName = Configurations.getCWD() + contFileName.substring(2);
			}
			mapOfContractions = new HashMap<String,String>();
			setOfContractions = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			loadContractions(contFileName);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the contradictions' serialized file !!!");
		}	
	}

	public String getExpandedForm(String contraction){
		if(mapOfContractions.containsKey(contraction.toLowerCase())){
			return mapOfContractions.get(contraction);
		}
		return null;
	}
	
	public TreeSet<String> getContractions(){
		return this.setOfContractions;
	}

	private void loadContractions(String fileName){
		try(BufferedReader cFile = new BufferedReader(new FileReader(fileName))){
			String line = null;
			while((line=cFile.readLine())!=null){
				if(!line.startsWith("//")){
					String[] tmpStr = line.split("\t");
					if(tmpStr.length==2){
						this.mapOfContractions.put(tmpStr[0], tmpStr[1]);
						this.setOfContractions.add(tmpStr[0]);
					}
				}
			}
		}catch(Exception ex){
			System.out.println("Contractions' file not found.");
		}
	}

	public static void main(String args[]){
		
//		ContractionsResourse cr = new ContractionsResourse();
		System.exit(0);
	}

}
