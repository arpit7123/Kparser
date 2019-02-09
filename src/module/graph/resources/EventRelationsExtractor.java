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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * This class implements an algorithm to find the relation between two event nodes.
 * It is a singleton class.
 * 
 * @author Arpit Sharma
 * @since 01/12/2015
 */
public class EventRelationsExtractor {
	
	private static EventRelationsExtractor eventRelationsInstance = null;
	
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ResourceHandler resHandler = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> posMap = null;
	
	static {
		eventRelationsInstance = new EventRelationsExtractor();
	}

	/**
	 * This is a private constructor.
	 */
	private EventRelationsExtractor(){
	}

	/**
	 * This is a static method to get the instance of EventRelationsExtractor class.
	 * @return eventRelationsInstance, an instance of EventRelationsExtractor class.
	 */
	public static EventRelationsExtractor getInstance() {
		return eventRelationsInstance;
	}

	/**
	 * This method implements the basic algorithm to find events and the relationships between them.
	 * @param sent, input sentence.
	 * @param inDep, an instance of InputDependencies class. It contains the syntactically parsed output for the input sentence.
	 * @return it returns an array list of Strings each string represents an edge between two event nodes. For example, <i>E1,R,E2</i>
	 * shows that there is a relation <i>R</i> between the event nodes <i>E1</i> and <i>E2</i>.
	 */
	public ArrayList<String> divide(String sent, InputDependencies inDep){
		ArrayList<String> result = Lists.newArrayList();
		
		posMap = inDep.getPosMap();
		sent = sent.replaceAll("'", " '");
		sent = sent.replaceAll("\\.", " .");
		ArrayList<String> words = Lists.newArrayList(Splitter.on(" ").split(sent));
		ArrayList<String> words2 = Lists.newArrayList();
		for(String w : words){
			words2.add(w.split("/")[0]);
		}

		sent = Joiner.on(" ").join(words2).trim();
		if(sent.charAt(sent.length()-1)=='.'){
			sent = sent.substring(0,sent.length()-1);
		}
		//		String bypass = " for like in at so of through behind In ";
		ArrayList<String> listOfParts = new ArrayList<String>();
		Pattern p = null;
		for(String conj : this.resHandler.getAllConjunctions()){
			if(conj.equalsIgnoreCase(".")){
				p = Pattern.compile("(.*)(\\"+conj+")(.*)");
			}else{
				p = Pattern.compile("(.*)("+conj+")(.*)");
			}
			Matcher m = p.matcher(sent);
			if(m.find()){
				int tempIndex;
//				if(this.backGroundFlag){
//					tempIndex=BG_INDEX+1;
//				}else{
					tempIndex=1;
//				}
				String tempSentPart = "";
				String[] part = m.group(1).trim().split(" ");
				for(int j=0;j<part.length;++j){
					tempSentPart += part[j]+"-"+(tempIndex) + " ";
					tempIndex++;
				}
				listOfParts.add(tempSentPart);

				tempSentPart = "";
				part = m.group(2).trim().split(" ");
				for(int j=0;j<part.length;++j){
					tempSentPart += part[j] + " ";
					tempIndex++;
				}
				listOfParts.add(tempSentPart);

				tempSentPart = "";
				part = m.group(3).trim().split(" ");
				for(int j=0;j<part.length;++j){
					tempSentPart += part[j]+"-"+(tempIndex) + " ";
					tempIndex++;
				}
				listOfParts.add(tempSentPart);
			}
		}
		if(listOfParts.size()%3 == 0){
			for(int i=0;i<listOfParts.size();){
				ArrayList<String> verb1 = findVerb(listOfParts.get(i));
				ArrayList<String> verb2 = findVerb(listOfParts.get(i+2));
				for(int j=0;j<verb1.size();++j){
					for(int k=0;k<verb2.size();++k){
						String v1 = verb1.get(j);
						String v2 = verb2.get(k);
						if(verb1!=null && verb2!=null){
//							result.add(v1+","+this.resHandler.getConjunctionLabel(listOfParts.get(i+1).trim())+","+v2);
							result.addAll(this.resHandler.getConjunctionLabel(listOfParts.get(i+1).trim(),v1,v2));
						}
					}
				}
				i+=3;
			}
		}
		
		return result;
	}

	/**
	 * This is a private helper method used by the divide method.
	 * @param sent any input sentence/phrase.
	 * @return result a list of verbs/events in the input sentence/phrase.
	 */
	private ArrayList<String> findVerb(String sent){
		ArrayList<String> result = new ArrayList<String>();
//		String commonVerbs = "";//is was has were are have be am been have had";
		String[] array = sent.split(" ");
		for(int i=0;i<array.length;++i){
			if(posMap.containsKey(array[i])){
				if(posMap.get(array[i]).startsWith("V")){
//					if(!this.resHandler.isAnArticle(array[i].split("-")[0])){
						result.add(array[i]);
//					}
				}
			}
		}
		return result;
	}
	
}
