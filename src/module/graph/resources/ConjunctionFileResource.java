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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import module.graph.helper.Configurations;

public class ConjunctionFileResource implements IResource {

	private HashMap<String,ArrayList<String>> fwdConjMap;
	private HashMap<String,ArrayList<String>> bwdConjMap;
	
	public ConjunctionFileResource() {
		try{
			String conjunctionsFile = Configurations.getProperty("conjunctionsFile");
			if(conjunctionsFile.startsWith("./")){
				conjunctionsFile = Configurations.getCWD() + conjunctionsFile.substring(2);
			}
			populateConjMaps(conjunctionsFile);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the conjunctions file !!!");
		}
	}
	
	public Set<String> getKeySet() {
		Set<String> fwd = fwdConjMap.keySet();
		Set<String> bwd = bwdConjMap.keySet();
		Set<String> result = new HashSet<String>();
		for(String s : fwd){
			result.add(s);
		}
		for(String s : bwd){
			result.add(s);
		}
		return result;
	}
	
	public ArrayList<String> getConjLabel(String conj) {
		ArrayList<String> labelledTriples = null;
		if(fwdConjMap.containsKey(conj)){
			labelledTriples = new ArrayList<String>();
			ArrayList<String> rels = fwdConjMap.get(conj);
			for(String rel : rels){
				labelledTriples.add(rel + "##F");
			}
		}else if(bwdConjMap.containsKey(conj)){
			labelledTriples = new ArrayList<String>();
			ArrayList<String> rels = bwdConjMap.get(conj);
			for(String rel : rels){
				labelledTriples.add(rel + "##B");
			}
		}
		return labelledTriples;
	}
	
	public ArrayList<String> getConjLabel(String conj, String arg1, String arg2 ) {
		ArrayList<String> labelledTriples = null;
		if(fwdConjMap.containsKey(conj)){
			labelledTriples = new ArrayList<String>();
			ArrayList<String> rels = fwdConjMap.get(conj);
			for(String rel : rels){
				labelledTriples.add(arg1 + "," + rel + "," + arg2);
			}
		}
		if(bwdConjMap.containsKey(conj)){
			if(labelledTriples==null){
				labelledTriples = new ArrayList<String>();
			}
			ArrayList<String> rels = bwdConjMap.get(conj);
			for(String rel : rels){
				labelledTriples.add(arg2 + "," + rel + "," + arg1);
			}
		}
		return labelledTriples;
	}
	
	private void populateConjMaps(String conjunctionsFile){
		fwdConjMap = new HashMap<String,ArrayList<String>>();
		bwdConjMap = new HashMap<String,ArrayList<String>>();
		BufferedReader br = null;
		InputStream in = null;
		try{
			in = new FileInputStream(conjunctionsFile);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=br.readLine())!=null){
				if(!line.startsWith("//")){
					String[] temp = line.split("\t");
					if(temp.length==3){
						if(temp[2].equals("X") && !temp[1].equals("X")){
							ArrayList<String> tmpList = null;
							if((tmpList=fwdConjMap.get(temp[0]))==null){
								tmpList = new ArrayList<String>();
							}
							tmpList.add(temp[1]);
							fwdConjMap.put(temp[0], tmpList);
						}else if(temp[1].equals("X") && !temp[2].equals("X")){
							ArrayList<String> tmpList = null;
							if((tmpList=bwdConjMap.get(temp[0]))==null){
								tmpList = new ArrayList<String>();
							}
							tmpList.add(temp[2]);
							bwdConjMap.put(temp[0], tmpList);
						}
					}
				}
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
