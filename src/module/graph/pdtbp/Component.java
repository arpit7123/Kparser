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

package module.graph.pdtbp;

import java.io.IOException;
import java.util.List;

import module.graph.pdtbp.resources.ModelsResource;
import module.graph.pdtbp.utils.CompOutput;
import module.graph.pdtbp.utils.SynParseData;
/**
 * Common class for parser component.
 * 
 * @author ilija.ilievski@u.nus.edu
 *
 */
public abstract class Component {

	protected String name = "not_set";

	protected abstract List<String[]> generateFeatures(String inputText, SynParseData synParseData, List<String> explicitSpans)
			throws IOException;
	
	public abstract CompOutput parseAnyText(String modelName, String inputText, SynParseData synPData, ModelsResource modelsResource) throws IOException;

}
