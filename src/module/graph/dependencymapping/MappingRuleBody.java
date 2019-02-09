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
 * This class has consists of one data variable, and its setter, getter, and equality methods.
 * This class is used in semantic mapping of dependency relations to semantic relations. It is used to save the body of a dependency mapping rule.
 * Following are the three variables:
 * 1. dependencyRule: It is a LinkedList of instances of module.graph.dependencymapping.SyntacticDependency class.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.LinkedList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class MappingRuleBody {

	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private LinkedList<SyntacticDependency> dependencyRule = null;
	
	/**
	 * This is a constructor
	 * @param dependencies it is the linked list of module.graph.dependencymapping.SyntacticDependency
	 */
	public MappingRuleBody(LinkedList<SyntacticDependency> dependencies){
		this.dependencyRule = dependencies;
	}
	
	/**
	 * This method is used to check if an input instance of MappingRuleBody is equal to the local instance. 
	 * @param body it is an instance of module.graph.dependencymapping.MappingRuleBody class
	 * @return true/false
	 */
	public boolean equals(MappingRuleBody body){
		LinkedList<SyntacticDependency> defaultDepRule = body.dependencyRule;
		SyntacticDependency defaultDep = defaultDepRule.getFirst();
		for(SyntacticDependency inputDep : this.dependencyRule){
			if(defaultDep.getDep().equals(inputDep.getDep())){
				if(defaultDep.getStartNode().equals(inputDep.getStartNode())
						&& defaultDep.getEndNode().equals(inputDep.getEndNode())){
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * This method is to check if the local instance of this class contains an input SyntacticDependency instance.
	 * @param dependency it is a linked list of instances of module.graph.mappingdependency.SyntacticDependency class.
	 * @return
	 */
	public LinkedList<SyntacticDependency> contains(SyntacticDependency dependency){
		LinkedList<SyntacticDependency> list = this.dependencyRule;
		if(list.contains(dependency)){
			list.remove(dependency);
			return list;
		}
		return null;
	}
}
