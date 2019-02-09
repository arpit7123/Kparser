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
 * This class has consists of eleven data variables, and their setter and getter methods.
 * This class is used to save a node in the output semantic graph of K-Parser and its various properties.
 * Following are the five variables:
 * 1. value: label of the node.
 * 2. isASemanticRole: boolean flag; is it a semantic role.
 * 3. isACoreferent: boolean flag; is it a co-referent of some other node.
 * 4. children: An array of its children nodes. 
 * 5. edgeName: An array of edge labels going out of this node.
 * 6. pos: Its POS tag.
 * 7. isClass: boolean flag; is it a class node.
 * 8. isEvent: boolean flag; is it an event node.. 
 * 9. isEntity: boolean flag; is it an entity node.
 * 10. wordSense: It is the wordsense id from WordNet.
 * 11. superClass: It is the superclass of the node.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.Setter;

public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5231074769567799139L;
	@Getter (AccessLevel.PUBLIC) private String value = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean isASemanticRole = false;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private boolean isACoreferent = false;
	private ArrayList<Node> children = null;
	private ArrayList<String> edgeName = null;
	private String pos = null;
	private boolean isClass = false;
	private boolean isEvent = false;
	private boolean isEntity = false;
	private String wordSense = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String superClass = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String lemma = null;
	
	/**
	 * This is a constructor for this class.
	 * @param val it is the label of this node
	 */
	public Node(String val){
		this.value = val;
		this.children = new ArrayList<Node>();
		this.edgeName = new ArrayList<String>();
	}
	
	/**
	 *  This is a constructor for this class.
	 */
	public Node(){
		this.children = new ArrayList<Node>();
		this.edgeName = new ArrayList<String>();
	}
	
	/**
	 * This is a getter method for POS tag.
	 * @return java.lang.String POS tag of the node
	 */
	public String getPOS(){
		return this.pos;
	}
	
	/**
	 * This is a setter method for POS tag.
	 */
	public void setPOS(String pos){
		this.pos = pos;
	}
	
	/**
	 * This is a method to add a children node in the array of children.
	 * @param node it is an instance of module.graph.helper.Node class.
	 */
	public void addChild(Node node){
		this.children.add(node);
	}
		
	/**
	 * This is a getter method for children array.
	 * @return ArrayList<module.graph.helper.Node> array of children nodes.
	 */
	public ArrayList<Node> getChildren(){
		return this.children;
	}
	
	/**
	 * This is a setter method for children array.
	 */
	public void setChildren(ArrayList<Node> children){
		this.children = children;
	}
	/**
	 * This is a method to add an edge in the array of edges.
	 * @param edge it is the label of the edge to be added.
	 */
	public void addEdgeName(String edge){
		this.edgeName.add(edge);
	}
	
	/**
	 * This is a getter method for edges array.
	 * @return ArrayList<java.lang.String> array of edge labels.
	 */
	public ArrayList<String> getEdgeList(){
		return this.edgeName;
	}
	
	/**
	 * This is a setter method for edges array.
	 */
	public void setEdgeList(ArrayList<String> edgeList){
		this.edgeName = edgeList;
	}
	
	/**
	 * This is a getter method to check if a Node is a class.
	 * @return true/false
	 */
	public boolean isAClass(){
		return this.isClass;
	}
	
	/**
	 * This is a getter method to check if a Node is an entity.
	 * @return true/false
	 */
	public boolean isAnEntity(){
		return this.isEntity;
	}
	
	/**
	 * This is a getter method to check if a Node is an event.
	 * @return true/false
	 */
	public boolean isAnEvent(){
		return this.isEvent;
	}
	
	/**
	 * This is a setter method to specify that a Node is a class.
	 * @param flag true/false
	 */
	public void setClass(boolean flag){
		this.isClass = flag;
	}
	
	/**
	 * This is a setter method to specify that a Node is an entity.
	 * @param flag true/false
	 */
	public void setEntity(boolean flag){
		this.isEntity = flag;
	}
	
	/**
	 * This is a setter method to specify that a Node is an event.
	 * @param flag true/false
	 */
	public void setEvent(boolean flag){
		this.isEvent = flag;
	}
	
	/**
	 * This is a getter method to get the wordsense id of a Node.
	 * @return java.lang.String WordNet sense id.
	 */
	public String getWordSense(){
		return this.wordSense;
	}
	
	/**
	 * This is a setter method to set the wordsense id of a Node.
	 * @param wordSense it is sense id of a word from WordNet.
	 */
	public void setWordSense(String wordSense){
		this.wordSense = wordSense;
	}

	/**
	 * This is a setter method to set the label of a Node.
	 * @param value it is the label of the Node.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * This is a method to transform the Object graph into a list of RDF strings.
	 * @return
	 */
	public HashSet<String> toRDFString(){
		return toRDFString(this);
	}
	
	/**
	 * This is a method to transform the Object graph into a list of RDF strings.
	 * @param node
	 * @return
	 */
	private HashSet<String> toRDFString(Node node){
		HashSet<String> rdfStringList = new HashSet<String>();
		ArrayList<String> edges = node.getEdgeList();
		ArrayList<Node> children = node.getChildren();
		int indx = 0;
		for(Node child : children){
			String rdf = node.getValue()+","+edges.get(indx)+","+child.getValue();
			rdfStringList.add(rdf);
			rdfStringList.addAll(toRDFString(child));
			indx++;
		}
		return rdfStringList;
	}
}
























