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
import module.graph.helper.ObjectFilesIO;
import module.graph.helper.WekaModelHandler;
import weka.classifiers.Classifier;

public class WekaClassifierResource implements IResource{
	
	private HashMap<String,Classifier> mapOfClassifiers;
	private HashMap<String,Integer> feature7Map = null;
	private HashMap<String,Integer> feature8Map = null;
	private HashMap<String,String> prepClassesMap = null;
	
	@SuppressWarnings("unchecked")
	public WekaClassifierResource(){
		try{
			String fileName = Configurations.getProperty("prepsWekaModelFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD()+fileName.substring(2);
			}
			mapOfClassifiers = WekaModelHandler.loadClassifiers(fileName);

			fileName = Configurations.getProperty("prepsFtr7File");//path + "hashmapEight_ALL";
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD()+fileName.substring(2);
			}
			feature7Map = (HashMap<String, Integer>) ObjectFilesIO.load(fileName);
			
			fileName = Configurations.getProperty("prepsFtr8File");//path + "hashmapSeven_ALL";
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD()+fileName.substring(2);
			}
			feature8Map = (HashMap<String, Integer>) ObjectFilesIO.load(fileName);

			fileName = Configurations.getProperty("prepClassesFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD()+fileName.substring(2);
			}
			prepClassesMap = populatePrepsClassMap(fileName);

		}catch(Exception e){
			System.err.println("ERROR: Resources for Preposition Sense Disambiguation are not loaded !!!");
		}
	}

	public Classifier getClassifier(String preposition){
		if(mapOfClassifiers.containsKey(preposition)){
			return mapOfClassifiers.get(preposition);
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getFeature7Value(String s){
		Integer result = -999;
		if(feature7Map.containsKey(s)){
			result = feature7Map.get(s);
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getFeature8Value(String s){
		Integer result = -999;
		if(feature8Map.containsKey(s)){
			result = feature8Map.get(s);
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getClassesForPrep(String prep){
		String result = null;
		if(prepClassesMap.containsKey(prep)){
			result = prepClassesMap.get(prep);
		}
		return result;
	}
	
	private HashMap<String,String> populatePrepsClassMap(String prepClassesFile){
		HashMap<String,String> prepsClassesMap = new HashMap<String,String>();
		BufferedReader file = null;
		try{
			file = new BufferedReader(new FileReader(prepClassesFile));
			String line = null;
			while((line=file.readLine())!=null){
				String[] temp = line.split(":::");
				prepsClassesMap.put(temp[0].trim(), temp[1].trim());
			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return prepsClassesMap;
	}
	
	public static void main(String[] args){
		WekaClassifierResource wcr = new WekaClassifierResource();
		for(String s : wcr.mapOfClassifiers.keySet()){
			System.out.println(s);
//			Classifier c = wcr.mapOfClassifiers.get(s);
//			System.out.println(c.toString());
		}
	}
}
