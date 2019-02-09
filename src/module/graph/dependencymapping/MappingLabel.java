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
 * This class has consists of three data variables, and their setter and getter methods.
 * This class is used in semantic mapping of dependency relations to semantic relations.
 * Following are the three variables:
 * 1. startNode: It is an instance of module.graph.dependencymapping.NodeProperties class.
 * 2. endNode: It is an instance of module.graph.dependencymapping.NodeProperties class.
 * 3. label: It is an instance of java.lang.String class.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class MappingLabel {
	@Getter (AccessLevel.PUBLIC) private NodeProperties startNode = null;
	@Getter (AccessLevel.PUBLIC) private NodeProperties endNode = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String label = null;
	
	public MappingLabel(String startNode,String startNodeSuffix,String endNode,String endNodeSuffix,String label){
		this.startNode = new NodeProperties();
		this.startNode.setNodeValue(startNode);
		this.startNode.setNodeSuffix(startNodeSuffix);
		this.endNode = new NodeProperties();
		this.endNode.setNodeValue(endNode);
		this.endNode.setNodeSuffix(endNodeSuffix);
		this.label = label;
	}
	
	public MappingLabel(MappingLabel label){
		this.startNode = label.getStartNode();
		this.endNode = label.getEndNode();
		this.label = label.getLabel();
	}
	
	public MappingLabel(){
	}
	
	public void setStartNode(String nodeValue){
		this.startNode = new NodeProperties();
		this.startNode.setNodeValue(nodeValue);
	}
	
	public void setStartNode(NodeProperties nodeValue){
		this.startNode = nodeValue;
	}
	
	public void setEndNode(String nodeValue){
		this.endNode = new NodeProperties();
		this.endNode.setNodeValue(nodeValue);
	}
	
	public void setEndNode(NodeProperties nodeValue){
		this.endNode = nodeValue;
	}
}
