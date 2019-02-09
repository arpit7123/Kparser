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
package module.graph.dependencymapping;

/**
 * This class reads the semantic mapping rules from mappings.txt file.
 * The rules are based on syntactic dependencies between words in a sentence.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import module.graph.helper.Configurations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MappingsReader {

	private HashMap<MappingRuleBody,LinkedList<MappingLabel>> depLabelsMap = null;

	/**
	 * This is a constructor for this class. It reads the mapping rules files and saves the rules in a data structure,
	 * HashMap<MappingRuleBody,LinkedList<MappingLabel>> 
	 */
	public MappingsReader() {
		try{
			String mappingsFile = Configurations.getProperty("semMappingFile");
			if(mappingsFile.startsWith("./")){
				mappingsFile = Configurations.getCWD() + mappingsFile.substring(2);
			}
			depLabelsMap = readMappings(mappingsFile);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the mapping rules file !!!");
		}
	}
	
	/**
	 * This is a constructor for this class. It reads the mapping rules files and saves the rules in a data structure,
	 * HashMap<MappingRuleBody,LinkedList<MappingLabel>> 
	 */
	public MappingsReader(String mappingRulesFile) {
		String mappingsFile = mappingRulesFile;
		depLabelsMap = readMappings(mappingsFile);
	}

	/**
	 * This method reads mapping rules from the input file and saves them in the data structure,
	 * HashMap<MappingRuleBody,LinkedList<MappingLabel>>
	 * @param mappingsFile it is the complete path of the mapping rules file.
	 * @return rules it is the output HashMap of mapping rules.
	 */
	private HashMap<MappingRuleBody,LinkedList<MappingLabel>> readMappings(String mappingsFile){
		HashMap<MappingRuleBody,LinkedList<MappingLabel>> rules = Maps.newHashMap();

		try(BufferedReader br = new BufferedReader(new FileReader
				(mappingsFile))){
			String line = null;
			while((line=br.readLine())!=null){
				if(!line.startsWith("//")){
					LinkedList<SyntacticDependency> dependencies = Lists.newLinkedList();
					LinkedList<MappingLabel> labels = Lists.newLinkedList();
					String[] temp = line.split(" LABEL=");
					String[] depRules = temp[0].split("\t");
					for(int i=0;i<depRules.length;++i){
						//					System.out.println(temp[i]);
						String[] temp1 = depRules[i].split(";");
						SyntacticDependency sd = new SyntacticDependency();
						NodeProperties startNode = new NodeProperties(temp1[1]);
						startNode.setNodeId(temp1[1].substring(0,temp1[1].indexOf(",")));
						NodeProperties endNode = new NodeProperties(temp1[2]);
						endNode.setNodeId(temp1[2].substring(0,temp1[2].indexOf(",")));
						sd.setDep(temp1[0]);
						sd.setStartNode(startNode);
						sd.setEndNode(endNode);
						dependencies.add(sd);
					}

					String[] mapLabels = temp[1].split("\t");
					for(int i=0;i<mapLabels.length;++i){
						//					System.out.println(temp[i]);
						String[] temp1 = mapLabels[i].split(";");
						MappingLabel label = new MappingLabel(temp1[0],temp1[1],temp1[2],temp1[3],temp1[4]);
						labels.add(label);
					}
					rules.put(new MappingRuleBody(dependencies), labels);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rules;
	}

	/**
	 * This method is used to get the set of bodies of all the mapping rules.
	 * @return Set<MappingRuleBody> it is the Set of all the heads of dependency rules in mappings.txt file.
	 */
	public Set<MappingRuleBody> getKeySet() {
		return depLabelsMap.keySet();
	}
	
	/**
	 * This method is used to get the body of semantic mapping rule given the semantic mapping head.
	 * @param key it is an input head to match a semantic mapping rule
	 * @return LinkedList<MappingLabel> it is the body of a semantic mapping rule in form of a linked list.
	 */
	public LinkedList<MappingLabel> getLabel(MappingRuleBody key) {
		LinkedList<MappingLabel> result = null;
		if(depLabelsMap.containsKey(key)){
			result = depLabelsMap.get(key);
		}
		return result;
	}

	/**
	 * This method returns the HashMap of semantic mapping rules.
	 * @return HashMap<MappingRuleBody,LinkedList<MappingLabel>> it is a HashMap of semantic mapping rules.
	 */
	public HashMap<MappingRuleBody,LinkedList<MappingLabel>> getDepLabelMap(){
		return depLabelsMap;
	}

	/**
	 * This is the main method to test the basic functionality of this class.
	 * @param args it is an empty array of strings
	 */
	@SuppressWarnings("unused")
	public static void main(String args[]){
		MappingsReader mr = new MappingsReader();
		System.out.println("Hello");
	}



}
