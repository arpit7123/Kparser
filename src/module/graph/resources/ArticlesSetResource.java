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
import java.util.HashSet;

import module.graph.helper.Configurations;


public class ArticlesSetResource implements IResource {

	private HashSet<String> articleSet;
	
	public ArticlesSetResource() {
		try{
			String articleFileName = Configurations.getProperty("articleFileName");
			if(articleFileName.startsWith("./")){
				articleFileName = Configurations.getCWD() + articleFileName.substring(2);
			}
			articleSet = populateArticleSet(articleFileName);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the articles file !!!");
		}	
	}
	
	public boolean contains(String word) {
		return articleSet.contains(word);
	}
	
	private HashSet<String> populateArticleSet(String articleFileName){
		HashSet<String> articleSet = new HashSet<String>();
		BufferedReader file = null;
		InputStream in = null;
		try{
			in = new FileInputStream(articleFileName);
			file = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=file.readLine())!=null){
				articleSet.add(line.trim());
			}
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return articleSet;
	}
}
