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
package module.graph.helper;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import weka.classifiers.Classifier;

public class WekaModelHandler {
	
	@SuppressWarnings("unchecked")
	public static HashMap<String,Classifier> loadClassifiers(String fileName){
		HashMap<String,Classifier> mapOfClassifiers = null;
		try{
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream is = new ObjectInputStream(fin);   
			mapOfClassifiers = (HashMap<String,Classifier>)is.readObject();
			is.close();
		}catch(Exception ex){
			System.out.println("Weka Classifiers map of classifiers file not found.");
			return mapOfClassifiers;
		}
		return mapOfClassifiers;
	}
}
