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

/**
 * This class has consists of two data variables, and their setter and getter methods.
 * Following are the five variables:
 * 1. classesMap: It is a HashMap of words and their conceptual classes
 * 2. superclassesMap: it is a HashMap of words and their conceptual super-classes
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.io.Serializable;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ClassesResource implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4253860830276591952L;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> classesMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> superclassesMap = null;
	
	/**
	 * This is a constructor for this class.
	 * @param classesMap it is a HashMap of words and their conceptual class
	 * @param superclassesMap it is a HashMap of words and their conceptual superclass
	 */
	public ClassesResource(HashMap<String,String> classesMap,HashMap<String,String> superclassesMap){
		this.classesMap = classesMap;
		this.superclassesMap = superclassesMap;
	}
}
