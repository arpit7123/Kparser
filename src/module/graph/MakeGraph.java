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
package module.graph;
/**
 * This class implements an algorithm to create an object based graph by using the list of RDF style
 * <i>has(X,R,Y)</i> strings.
 * 
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







import module.graph.helper.ClassesResource;
//import module.graph.helper.EdgeClass;
import module.graph.helper.Node;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.NodePassedToViewer;
import module.graph.qasrl.Graph2Qasrl;
import module.graph.qasrl.VerbQA;


public class MakeGraph {
	
	private boolean overflowFlag = false;
	private HashMap<String, HashSet<String>> childrenMap;
	private HashMap<String,Boolean> hasParentMap;
	private HashSet<String> wordsSet;
	private HashMap<String,HashMap<String,String>> edgeMap;
	public HashMap<String,String> posMap;
	public HashMap<String,ArrayList<String>> wordSenseMap;
	public HashMap<String,String> classMap;
	public HashMap<String,String> superClassMap;
	private Pattern wordPat = Pattern.compile("(.*)(-)([0-9]{1,7})");
	
	private SentenceToGraph stg = null;
	private Graph2Qasrl g2qasrl = null;
	
	/**
	 * a regex pattern to read the RDF style <i>has(X,R,Y)</i> statements.
	 */
	private static Pattern p = Pattern.compile("(has\\()(.*)(\\).)");

	/**
	 * It is the constructor that initializes all the global variables of this class.
	 */
	public MakeGraph() {
		this.stg = new SentenceToGraph();
		this.g2qasrl = new Graph2Qasrl();
		childrenMap = new HashMap<String, HashSet<String>>();
		hasParentMap = new HashMap<String,Boolean>();
		wordsSet = new HashSet<String>();
		edgeMap = new HashMap<String,HashMap<String,String>>();
	}
	
	/**
	 * This is the main method used to test the basic functionality of this class.
	 * @param args empty array of strings
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MakeGraph mg = new MakeGraph();
		String sent = "John loves Mia.";
		sent = "Later in the day the calm weather changed and a nearby flag began to flutter , I .";
		sent = "The kids were hungry and so their mother began to get their lunch of sandwiches , salad , turkey meat , chips , and Gatorade and placed all on a blanket .";
		sent = "john gave #20 to tom .";
		sent = "The drain is clogged with hair. It has to be removed.";
		sent = "The table won't fit through the doorway because it is too wide.";
		sent = "The fish ate the worm. It was tasty.";
		sent = "As Ollie carried Tommy up the long winding steps, his legs dangled.";
		sent = "John loved Mia.";
		sent = "John loves his wife.";
//		sent = "John went to Italy.";
		ArrayList<NodePassedToViewer> npvArrayList = mg.createGraphUsingSentence(sent, false, true, false);
//		System.out.println(npvArrayList.size());
//		if(npvArrayList.size()>0){
//			NodePassedToViewer npv = npvArrayList.get(0);
//			ArrayList<String> rdfList = npv.getTextGraph();
//			for(String s : rdfList){
//				System.out.println(s);
//			}
//		}
		
		ArrayList<VerbQA> l = mg.graphToQasrl(npvArrayList);
		
		for(VerbQA vqa : l){
			System.out.println(vqa.toString());
		}
		
		System.exit(0);
	}
	
	/**
	 * This method is used to create the ArrayList of objective graphs for the input sentence.
	 * @param sent it is the input English sentences.
	 * @param bg_flag this flag is used to add 100 to the index of each word in each sentence.
	 * @param ws_flag this is a flag that allows to use word sense disambiguation to find the correct superclasses of 
	 * the words in the input sentences.
	 * @param useCoreference this flag is used to specify if you want to resolve coreferences in the input sentence. 
	 * @return gpnArrayList an ArrayList of objective graphs generated for the input sentence.
	 */
	public ArrayList<NodePassedToViewer> createGraphUsingSentence(String sent, boolean bg_flag, boolean ws_flag, boolean useCoreference) throws Exception{
		return createGraphUsingSentence(sent,bg_flag,ws_flag,useCoreference,0);
	}
	
	/**
	 * This method is used to create the ArrayList of objective graphs for the input sentence.
	 * @param sent it is the input English sentences.
	 * @param bg_flag this flag is used to add 100 to the index of each word in each sentence.
	 * @param ws_flag this is a flag that allows to use word sense disambiguation to find the correct superclasses of 
	 * the words in the input sentences.
	 * @param useCoreference this flag is used to specify if you want to resolve coreferences in the input sentence. 
	 * @param index this flag is used to specify the start index of the words in the input sentence.
	 * @return gpnArrayList an ArrayList of objective graphs generated for the input sentence.
	 */
	public ArrayList<NodePassedToViewer> createGraphUsingSentence(String sent, boolean bg_flag, boolean ws_flag, boolean useCoreference, int index) throws Exception{
		GraphPassingNode gpn = this.stg.extractGraph(sent,bg_flag,ws_flag, useCoreference,index);
		ArrayList<NodePassedToViewer> npvArrayList = new ArrayList<NodePassedToViewer>();
		this.posMap = gpn.getposMap();
		this.wordSenseMap = gpn.getWordSenseMap();
		ClassesResource cr = gpn.getConClassRes();
		this.classMap = cr.getClassesMap();
		this.superClassMap = cr.getSuperclassesMap();
		ArrayList<String> aspGraph = gpn.getAspGraph();
		String sentence = gpn.getSentence();
		ArrayList<Node> parseTreeList = this.generateParseTreeList(aspGraph);
		if(parseTreeList.size()==0){
			NodePassedToViewer npv = new NodePassedToViewer(null,sentence,aspGraph,new HashMap<String,String>(this.posMap));
			npv.setExtraStuff(gpn.getExtraStuff());
			npvArrayList.add(npv);
		}else{
			for(Node gn : parseTreeList){
				NodePassedToViewer npv = new NodePassedToViewer(gn,sentence,aspGraph,new HashMap<String,String>(this.posMap));
				npv.setExtraStuff(gpn.getExtraStuff());
				npvArrayList.add(npv);
			}
		}
		
		if(overflowFlag){
			overflowFlag = false;
//			return new ArrayList<NodePassedToViewer>();
		}

		return npvArrayList;
	}
	
	/**
	 * This is a method to generate the list of objective graphs from the RDF style graph output from SentenceToGraph class.
	 * @param inputArray it is an ArrayList of RDF style graph
	 * @return parseTreeList it is an ArrayList of Root nodes of output graphs.
	 */
	public ArrayList<Node> generateParseTreeList(ArrayList<String> inputArray) throws Exception{
		ArrayList<Node> parseTreeList = new ArrayList<Node>();

		childrenMap.clear();
		hasParentMap.clear();
		wordsSet.clear();
		edgeMap.clear();
		
		ArrayList<String> roots = new ArrayList<String>();

		for (String line : inputArray) {
			Matcher m = p.matcher(line);
			if(m.find()){
				String[] s = m.group(2).split(",");
				String parent = s[0];
				String edge = s[1];
				String child = s[2];
				
				if(parent.equalsIgnoreCase(child)){
					child = "_"+child;
					if( this.posMap.containsKey(s[2])){
						this.posMap.put(child, this.posMap.get(s[2]));
					}
				}
				
				wordsSet.add(parent);
				wordsSet.add(child);
				
				HashMap<String,String> temp = null;
				if(edgeMap.containsKey(parent)){
					temp = edgeMap.get(parent);
				}else{
					temp = new HashMap<String,String>();
				}
				temp.put(child, edge);
				edgeMap.put(parent,temp);
				
				HashSet<String> childrenList = null;
				if(childrenMap.containsKey(parent)){
					childrenList = childrenMap.get(parent);
				}else{
					childrenList = new HashSet<String>();
				}
				childrenList.add(child);
				childrenMap.put(parent, childrenList);
				hasParentMap.put(child, true);
			}
		}
		
		for(String s : wordsSet){
			if(!hasParentMap.containsKey(s)){
				roots.add(s);
			}
		}

		for(String s : roots){
			Node node = createGraphNode(s);
			Matcher m = wordPat.matcher(s);
			if(m.matches()){
				String wsdKey = m.group(1)+"_"+m.group(3);
				if(this.wordSenseMap.containsKey(wsdKey)){
					node.setWordSense(this.wordSenseMap.get(wsdKey).get(0));
				}
				if(classMap.containsKey(s)){
					node.setLemma(classMap.get(s));
				}
			}
			
			node.setPOS(posMap.get(s));//removed on 9th June 2015 :- .replaceAll("_", "-")));

//			if(this.wordSenseMap.containsKey(s)){
//				String tempSense = this.wordSenseMap.get(s).get(0);
//				node.setWordSense(tempSense);
//			}
			
//			removed on 9th June 2015 :-
//			if(posMap.get(s.replaceAll("_", "-")).startsWith("V")){ 
//			System.out.println(s);
			if(posMap.get(s).startsWith("V")){
				node.setEvent(true);
			}else{
				node.setEntity(true);
			}
			
			try{
				createParseTree(node,s);//, rootToken, semanticsMap, true);
			}catch(StackOverflowError e){
				System.err.println("Infinite loop occurred while creating the graph !!!");
				overflowFlag = true;
				return new ArrayList<Node>();
			}
			parseTreeList.add(node);
		}

		return parseTreeList;
	}

	/**
	 * This method recursively creates the parse graph using the childrenMap and root node.
	 * @param node it is a node of the graph.
	 * @param nodeValue it is the value of the node
	 */	
	private void createParseTree(Node node, String nodeValue) throws Exception{
		if(childrenMap.containsKey(nodeValue)) {
			HashSet<String> children = childrenMap.get(nodeValue);
			for(String child : children){
				Node n = new Node(child);
				
				if(this.superClassMap!=null){
					if(this.superClassMap.containsKey(child)){
						n.setSuperClass(this.superClassMap.get(child));
					}
					if(classMap.containsKey(child)){
						n.setLemma(classMap.get(child));
					}
				}
//				removed on 9th June 2015 :-
//				n.setPOS(posMap.get(child.replaceAll("_", "-")));
				n.setPOS(posMap.get(child));
				
//				System.out.println(child);
				Matcher m = wordPat.matcher(child);
				if(m.matches()){
					String wsdKey = (m.group(1)+"_"+m.group(3)).toLowerCase();
					if(this.wordSenseMap.containsKey(wsdKey)){
						n.setWordSense(this.wordSenseMap.get(wsdKey).get(0));
					}
				}
				
				if(edgeMap.get(nodeValue).get(child).equalsIgnoreCase("instance_of")
						||edgeMap.get(nodeValue).get(child).equalsIgnoreCase("prototype_of")
						||edgeMap.get(nodeValue).get(child).equalsIgnoreCase("is_subclass_of")){
					n.setClass(true);
				}else if(edgeMap.get(nodeValue).get(child).equalsIgnoreCase("semantic_role")){
					n.setASemanticRole(true);
				} else if(edgeMap.get(nodeValue).get(child).equalsIgnoreCase("has_coreferent")) {
					n.setACoreferent(true);
				}else {
//					removed on 9th June 2015 :-
//					if(posMap.get(child.replaceAll("_", "-")).startsWith("V")){
//					System.out.println(child);
					if(posMap.get(child).startsWith("V")){
						n.setEvent(true);
					}else{
						n.setEntity(true);
					}
				}

				
				node.addChild(n);
				node.addEdgeName(edgeMap.get(nodeValue).get(child));
				if(childrenMap.containsKey(child)){
					createParseTree(n,child);
				}
			}
		}
		
	}
	
	/**
	 *  This method Creates a parse graph node 
	 * @param contentItems it is the label of the graph node.
	 * @return GraphNode it is the graph node.
	 */
	private Node createGraphNode(String contentItems) {
		Node node = new Node(contentItems);
		if(this.superClassMap!=null){
			if(this.superClassMap.containsKey(contentItems)){
				node.setSuperClass(this.superClassMap.get(contentItems));
			}
		}
		return node;
	}
	
	/**
	 * This method returns the local instance of SentenceToGraph class.
	 * @return stg the private local instance of SentenceToGraph class.
	 */
	public SentenceToGraph getSentenceToGraphInstance(){
		return this.stg;
	}
	
	public ArrayList<VerbQA> graphToQasrl(ArrayList<NodePassedToViewer> listOfGraphs) throws Exception{
		return this.g2qasrl.getQASRLOutput(listOfGraphs);
	}
}























