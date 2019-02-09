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
 * This class has consists of five data variables, and their setter, getter, and equality methods.
 * This class is used in semantic mapping of dependency relations to semantic relations.
 * Following are the five variables:
 * 1. nodeId: It is an instance of java.lang.String class. It is used to save the id of a word that is a part of a syntactic dependency.
 * 2. nodeValue: It is an instance of java.lang.String class. It is used to save the value of a word that is a part of a syntactic dependency.
 * 3. nodePOS: It is an instance of java.lang.String class. It is used to save the POS tag of a word that is a part of a syntactic dependency.
 * 4. nodeSuperclass: It is an instance of java.lang.String class. It is used to save the conceptual class of a word that is a part of a syntactic dependency.
 * 5. nodeSuffix: It is an instance of java.lang.String class. It is used to save the suffix of a word that is a part of a syntactic dependency. (This is not used in current version of the semantic mapping)
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class NodeProperties {
	private final String NULLFLAG = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nodeId = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nodeValue = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nodePOS = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nodeSuperclass = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nodeSuffix = null;
	
	/**
	 * This is a constructor of this class.
	 * @param properties It is a java.lang.String instance containing nodeValue, nodePOS, and nodeSuperclass separated by comma (,)
	 */
	public NodeProperties(String properties){
		String[] temp = properties.split(",");
		if(temp.length==1){
			nodeValue = temp[0];
			nodePOS = this.NULLFLAG;
			nodeSuperclass = this.NULLFLAG;
		}else if(temp.length==2){
			nodeValue = temp[0];
			nodePOS = temp[1];
			nodeSuperclass = this.NULLFLAG;
		}else if(temp.length==3){
			nodeValue = temp[0];
			nodePOS = temp[1];
			nodeSuperclass = temp[2];
		}else{
			System.err.println("more than 3 properties in mappings file");
		}
	}

	/**
	 * This is a constructor of this class.
	 * @param node It is an instance of module.graph.dependencymapping.NodeProperties class. It is used to set the variables in this class.
	 */
	public NodeProperties(NodeProperties node){
		this.nodeId = node.nodeId;
		this.nodePOS = node.nodePOS;
		this.nodeSuffix = node.nodeSuffix;
		this.nodeSuperclass = node.nodeSuperclass;
		this.nodeValue = node.nodeValue;
	}
	
	/**
	 * This is a constructor of this class.
	 */
	public NodeProperties(){		
	}

	/**
	 * This method is used to check if an input instance of module.graph.dependencymapping.NodeProperties is equal to the local instance of this class.
	 * @param props it is an instance of module.graph.dependencymapping.NodeProperties class.
	 * @return true/false
	 */
	public boolean equals(NodeProperties props){		
		if(this.getNodePOS().startsWith("non")){
			if(!props.getNodePOS().substring(0, 1).equalsIgnoreCase(this.getNodePOS().substring(this.getNodePOS().indexOf(" ")+1,this.getNodePOS().length()))){
				if(this.getNodeSuperclass().equalsIgnoreCase(props.getNodeSuperclass())){
					return true;
				}
				if(this.getNodeSuperclass().startsWith("non")){
					if(props.getNodeSuperclass()!=null){
						if((props.getNodeSuperclass().equalsIgnoreCase(this.getNodeSuperclass().substring(this.getNodeSuperclass().indexOf(" ")+1,this.getNodeSuperclass().length())))){
							return false;
						}else{
							return true;
						}
					}else{
						return true;
					}
				}
				if(this.getNodeSuperclass().startsWith("X")){
					return true;
				}	
			}
		}
		
		if((this.getNodePOS().subSequence(0, 1).equals(props.getNodePOS().substring(0, 1))
				|| (this.getNodePOS().startsWith("X")))){
			if(this.getNodeSuperclass().equalsIgnoreCase(props.getNodeSuperclass())){
				return true;
			}
			if(this.getNodeSuperclass().startsWith("non")){
				if(props.getNodeSuperclass()!=null){
					if((props.getNodeSuperclass().equalsIgnoreCase(this.getNodeSuperclass().substring(this.getNodeSuperclass().indexOf(" ")+1,this.getNodeSuperclass().length())))){
						return false;
					}else{
						return true;
					}
				}else{
					return true;
				}
			}
			if(this.getNodeSuperclass().startsWith("X")){
				return true;
			}		
		}
		return false;
	}
}
