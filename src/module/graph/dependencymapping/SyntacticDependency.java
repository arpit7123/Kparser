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
 * This class has consists of three data variables, and their setter, getter, and equality methods.
 * This class is used in semantic mapping of dependency relations to semantic relations.
 * Following are the five variables:
 * 1. dep: It is an instance of java.lang.String class. It is used to save the label of a syntactic dependency.
 * 2. startNode: It is an instance of module.graph.dependencymapping class. It is used to save the start node, and its various properties of the syntactic dependency.
 * 3. endNode: It is an instance of module.graph.dependencymapping class. It is used to save the end node, and its various properties of the syntactic dependency.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class SyntacticDependency {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String dep = null;
	@Getter (AccessLevel.PUBLIC) private NodeProperties startNode = null;
	@Getter (AccessLevel.PUBLIC) private NodeProperties endNode = null;

	/**
	 * This is a setter method for syntactic dependency startNode.
	 * @param node it is an instance of module.graph.dependencymapping.NodeProperties class.
	 */
	public void setStartNode(NodeProperties node){
		this.startNode = node;
	}

	/**
	 * This is a setter method for syntactic dependency endNode.
	 * @param node it is an instance of module.graph.dependencymapping.NodeProperties class.
	 */
	public void setEndNode(NodeProperties node){
		this.endNode = node;
	}

	/**
	 * This method is used to check if the input instance of module.graph.semanticmapping.SyntacticDependency is equal to the local instance.
	 * @param dependency it is an instance of module.graph.semanticmapping.SyntacticDependency
	 * @return true/false
	 */
	public boolean equals(SyntacticDependency dependency){
		if(this.getDep().equals(dependency.getDep())){
			if(this.getStartNode().equals(dependency.getStartNode())
					&& this.getEndNode().equals(dependency.getEndNode())){
				return true;
			}
		}
		return false;
	}
}
