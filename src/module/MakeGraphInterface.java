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
package module;
/**
 * This is an interface for MakeGraph class, which is used to create graph for any input sentence
 * 
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;

import module.graph.helper.NodePassedToViewer;

public interface MakeGraphInterface {
	/**
	 * This method takes a sentence as input and returns an array of graphs.
	 * @param sent input sentence
	 * @return list of output graphs
	 */
	public ArrayList<NodePassedToViewer> createGraphUsingSentence(String sent);
}
