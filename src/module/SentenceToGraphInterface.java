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
 * This is an interface that declares two methods that are declared in the classes that implement this interface
 * 
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import module.graph.helper.GraphPassingNode;

public interface SentenceToGraphInterface {

	/**
	 * This method is used to ectract RDF style graph from an input sentence
	 * @param sent input sentence
	 * @param bg_flag background flag, 100 is added to the index of each word in the sentence if the flag is set true.
	 * @param ws_flag word sense disambiguation flag. Performs WSD if this is true.
	 * @return
	 * @throws Exception 
	 */
	public GraphPassingNode extractGraph(String sent, boolean bg_flag, boolean ws_flag) throws Exception;
}
