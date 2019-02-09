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

/**
 * This class has consists of four data variables, and their setter and getter methods.
 * This class is used to save the output semantic graph of an input sentence extracted using module.graph.SentenceToGraph class.
 * Following are the five variables:
 * 1. aspGraph: It is an array of semantic graph in RDF like has(X,R,Y) strings.
 * 2. posMap: It is a HashMap of words and their POS tags.
 * 3. sentence: It is the sentence for which the semantic graph is saved in this class.
 * 4. wordSenseMap: It is a HashMap of words in the input sentence and their senses from WordNet. 
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class GraphPassingNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6307219797855383905L;
	private HashMap<String,String> posMap = null;
	private ArrayList<String> aspGraph = null;
	private String sentence = null;
	private HashMap<String,ArrayList<String>> wordSenseMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ClassesResource conClassRes = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,ArrayList<String>> extraStuff = null;
	
	/**
	 * This is a constructor for this class.
	 * @param aspGraph it is an array of RDF style strings.
	 * @param posMap it is a HashMap of words and their POS tags.
	 * @param sentence it is the sentence.
	 * @param wordSenseMap it is a map of words and their senses from WordNet.
	 */
	public GraphPassingNode(ArrayList<String> aspGraph, HashMap<String,String> posMap,String sentence,HashMap<String,ArrayList<String>> wordSenseMap) {
		this.aspGraph = new ArrayList<String>(aspGraph);
		this.posMap = new HashMap<String,String>(posMap);
		this.sentence = sentence;
		this.wordSenseMap = new HashMap<String,ArrayList<String>>(wordSenseMap);
	}
	
	/**
	 * This is a getter method for POS tag map of words.
	 * @return HashMap<String,String> it is the POS tag map of the words in the input sentence.
	 */
	public HashMap<String,String> getposMap(){
		return this.posMap;
	}
	
	/**
	 * This is a getter method for RDF style has strings of the semantic graph edges.
	 * @return ArrayList<String> it is the list of has(X,R,Y) strings. X is start node, Y is end Node and R is the semantic relation between X and Y.
	 */
	public ArrayList<String> getAspGraph(){
		return this.aspGraph;
	}
	
	/**
	 * This is a getter method for the input sentence.
	 * @return java.lang.String it returns the input sentence.
	 */	
	public String getSentence(){
		return this.sentence;
	}
	
	/**
	 * This is a getter method for WordNet senses' map of words.
	 * @return HashMap<String,ArrayList<String>> it is the word sense map of the words in the input sentence.
	 */
	public HashMap<String,ArrayList<String>> getWordSenseMap(){
		return this.wordSenseMap;
	}

}
