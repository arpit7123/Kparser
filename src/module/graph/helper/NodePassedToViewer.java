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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author	Arpit Sharma
 * 
 */
public class NodePassedToViewer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7791463147445223261L;
	private Node graphNode = null;
	private String sentence = null;
	private ArrayList<String> textGraph = null;
	private HashMap<String,String> posMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,ArrayList<String>> extraStuff = null;
	
	public NodePassedToViewer(Node graphNode, String sentence, ArrayList<String> textGraph, HashMap<String,String> posMap) {
		this.graphNode = graphNode;
		this.sentence = sentence;
		this.textGraph = textGraph;
		this.posMap = posMap;
	}
	
	public Node getGraphNode(){
		return this.graphNode;
	}
	
	public String getSentence(){
		return this.sentence;
	}
	
	public ArrayList<String> getTextGraph(){
		return textGraph;
	}
	
	public HashMap<String,String> getPosMap(){
		return this.posMap;
	}
}
