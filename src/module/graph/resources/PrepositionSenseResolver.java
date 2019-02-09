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

import java.io.File;

import module.graph.helper.Configurations;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * This class uses weka classifier to classify the new instance of a preposition in a sentence
 * 
 * @author Arpit Sharma
 *
 */
public class PrepositionSenseResolver {

	private static Instances instances;

	static {
		try{
			String arffStructureFile = Configurations.getProperty("prepsClassifierArff");
			if(arffStructureFile.startsWith("./")){
				arffStructureFile = Configurations.getCWD() + arffStructureFile.substring(2);
			}
			ArffLoader loader = new ArffLoader();
			loader.setFile(new File(arffStructureFile));
			instances = loader.getStructure();
		}catch(Exception e){
			System.err.println("ERROR: PSD ARFF structure file not loaded !!!");
		}
	}

	public static Instances getArffStructure() {
		return instances;
	}

	public static void main(String[] args) {
		System.out.println(getArffStructure());
	}
}
