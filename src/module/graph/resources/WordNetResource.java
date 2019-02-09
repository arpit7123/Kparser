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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import module.graph.helper.Configurations;

public class WordNetResource implements IResource {

	private HashMap<Integer,String> lexFileNamMap = null; 
	
	public WordNetResource() {
		try{
			String wordnetLexicalFile = Configurations.getProperty("lexnamesFile");
			if(wordnetLexicalFile.startsWith("./")){
				wordnetLexicalFile = Configurations.getCWD()+wordnetLexicalFile.substring(2);
			}
			lexFileNamMap = populateLexicalFileMap(wordnetLexicalFile);
		}catch(Exception e){
			System.err.println("ERROR: Wordnet resources are not loaded !!!");
		}
	}
	
	private HashMap<Integer,String> populateLexicalFileMap(String wordnetLexicalFile){
		HashMap<Integer,String> lexFileNamMap = new HashMap<Integer,String>();
		BufferedReader file = null;
		URL in = null;
		Pattern p = Pattern.compile("(.*)(\t)(.*)(\\.)(.*)(\t)(.*)");
		try{
			in = new URL(wordnetLexicalFile);
			file = new BufferedReader(new InputStreamReader(in.openStream()));
			String line = null;
			while((line=file.readLine())!=null){
				Matcher m = p.matcher(line);
				if(m.find()){
					lexFileNamMap.put(Integer.parseInt(m.group(1)),m.group(5));
				}
			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return lexFileNamMap;
	}
	
	public String getLexName(int num){
		if(lexFileNamMap.containsKey(num)){
			return lexFileNamMap.get(num);
		}
		return null;
	}
}
