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
 * This class is used for semantic mapping of dependency relations to semantic relations.
 * Following are the three variables:
 * 1. mappings: It is a hashSet of instances of java.lang.String class.
 * 2. posMap: It is a hashMap with a word (java.lang.String) as key and its POS tag (java.lang.String) as value.
 * 3. flaggedDeps: it is a HashSet of instances of module.graph.dependencymapping.SyntacticDependency class.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.HashMap;
import java.util.HashSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class MappingHandlerOutput {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashSet<String> mappings = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> posMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashSet<SyntacticDependency> flaggedDeps = null;
}
