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

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class Preprocessor {
	private static Preprocessor preprocessor;
	
	static {
		preprocessor = new Preprocessor();
	}
	
	private Preprocessor(){
		preprocessor = Preprocessor.getInstance();
	}

	public static Preprocessor getInstance() {
		return preprocessor;
	}
	
	public String preprocessText(String sentence,ResourceHandler resourceHandler,
								HashMap<String,String> mweMap,
								HashMap<String,String> stringToNamedEntityMap) {
		sentence = sentence.trim();
		if((sentence.substring(sentence.length()-1).equalsIgnoreCase("."))){
			sentence = sentence.substring(0, sentence.length()-1).trim() + " .";
		}else if((sentence.substring(sentence.length()-1).equalsIgnoreCase("?"))){
			sentence = sentence.substring(0, sentence.length()-1).trim() + " ?";
		}else{
			sentence = sentence + " .";
		}
		
		sentence = sentence.replaceAll("\\[", "");
        sentence = sentence.replaceAll("\\]", "");
		sentence = sentence.replaceAll("\t", " ");
		sentence = sentence.replaceAll("\n", " ");
		sentence = sentence.replaceAll(",", " , ");
		sentence = sentence.replaceAll(";", " ; ");
		sentence = sentence.replaceAll(" +", " ");
		
		TreeSet<String> contractions = resourceHandler.getContractionsSet();
		HashSet<String> sentWordsSet = Sets.newHashSet(Splitter.on(" ").split(sentence));
		
		SetView<String> intersectSet = Sets.intersection(sentWordsSet,contractions);
		
		for(String s : intersectSet){
			String expndForm = resourceHandler.getExpandedForm(s);
			if(expndForm!=null){
				sentence = sentence.replaceAll("(?i)"+s, expndForm);
			}
		}
		
		if(stringToNamedEntityMap!=null){
			String[] words = sentence.split(" ");
			String[] newWords = new String[words.length];
			int i = 0;
			for(String word : words){
				if(stringToNamedEntityMap.containsKey(word)){
					if(stringToNamedEntityMap.get(word).equalsIgnoreCase("DATE")){
						newWords[i] = "Wednesday";
						stringToNamedEntityMap.put("Wednesday", "DATE");
						mweMap.put("Wednesday-"+(i+1), word);	
					}else if(stringToNamedEntityMap.get(word).equalsIgnoreCase("TIME")){
						newWords[i] = "six_am";
						stringToNamedEntityMap.put("six_am", "TIME");
						mweMap.put("six_am-"+(i+1), word);
					}else if(stringToNamedEntityMap.get(word).equalsIgnoreCase("PERCENT")){
						newWords[i] = "9999999";
						stringToNamedEntityMap.put("9999999", "PERCENTAGE");
						mweMap.put("9999999-"+(i+1), word);
					}else if(stringToNamedEntityMap.get(word).equalsIgnoreCase("MONEY")){
						newWords[i] = "dollars_20";
						stringToNamedEntityMap.put("dollars_20", "MONEY");
						mweMap.put("dollars_20-"+(i+1), word);
					}else{
						newWords[i] = word;
					}
				}else{
					newWords[i] = word;
				}
				i++;
			}			
			sentence = Joiner.on(" ").join(newWords);
		}else{
			System.err.println("Named Entities' Map is NULL in Preprocessor.prprocessText()");
		}
		
		

		
		
		
//		System.out.println(sentence);
		
		
		
		
//************************************************************************
/*		sentence = sentence.replaceAll("I'm", "I am ");
		sentence = sentence.replaceAll(" i'm ", " i am ");
		
		sentence = sentence.replaceAll("It's", "It is ");
		sentence = sentence.replaceAll(" it's ", " it is ");
		sentence = sentence.replaceAll(",it's ", ",it is ");
		
		sentence = sentence.replaceAll(" +", " ");

		String[] words = sentence.split(" ");
		ArrayList<String> newwords = new ArrayList<>(Arrays.asList(words));
		String temp;
		Pattern p1 = Pattern.compile(".*n't");
		Pattern p2 = Pattern.compile(".*'ve");
		Pattern p3 = Pattern.compile(".*'ll");
		for(int i=0; i<newwords.size();){
			String s = newwords.get(i);

			if(Pattern.matches(p1.toString(), s)){
				temp = s.substring(0, s.length()-3);
				newwords.add(i, temp);
				newwords.add(i+1,"not");
				newwords.remove(i+2);
				i=i+2;
			}else if(Pattern.matches(p2.toString(), s)){
				temp = s.substring(0, s.length()-3);
				newwords.add(i, temp);
				newwords.add(i+1,"have");
				newwords.remove(i+2);
				i=i+2;
			}else if(Pattern.matches(p3.toString(), s)){
				temp = s.substring(0, s.length()-3);
				newwords.add(i, temp);
				newwords.add(i+1,"will");
				newwords.remove(i+2);
				i=i+2;
			}else{
				i++;
			}
		}

		StringBuffer sbuf = new StringBuffer();

		for(int i=0; i<newwords.size();i++){
			if(newwords.get(i).equals("i"))
				newwords.set(i,"I");
			sbuf = sbuf.append(newwords.get(i));
			if(i!=newwords.size()-1)
				sbuf.append(" ");
		}
		String tempStr = sbuf.toString();*/
//************************************************************************
		
		return sentence;//sentence.replaceAll("[^a-zA-Z0-9,.'\\s_:?]", "");
	}
}
