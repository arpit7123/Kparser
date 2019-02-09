/**
 * Copyright (C) 2015 WING, NUS and NUS NLP Group.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see http://www.gnu.org/licenses/.
 */

package module.graph.pdtbp.resources;

import java.io.Serializable;

import edu.stanford.nlp.trees.Tree;

public class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5434443275034390171L;
	public Node(Tree tree, int index) {
		this.tree = tree;
		this.index = index;
	}

	public String toString() {
		return tree + ":" + index;
	};

	public Tree tree;
	public int index;
}
