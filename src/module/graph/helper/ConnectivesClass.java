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
 * This class has consists of three data variables, and their setter and getter methods.
 * This class is used in the Discourse Parsing of a sentence.
 * Following are the five variables:
 * 1. arg1: It is an instance of java.lang.String class. It is first argument of the discourse connective in the sentence.
 * 2. arg2: It is an instance of java.lang.String class. It is second argument of the discourse connective in the sentence.
 * 3. conn: It is an instance of java.lang.String class. It is the discourse connective in the sentence.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ConnectivesClass implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6669946009820097180L;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String arg1 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String arg2 = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String conn = null;
	
	/**
	 * This is a constructor for this class.
	 * @param arg1 It is an instance of java.lang.String class. It is first argument of the discourse connective in the sentence.
	 * @param arg2 It is an instance of java.lang.String class. It is second argument of the discourse connective in the sentence.
	 * @param conn It is an instance of java.lang.String class. It is the discourse connective in the sentence.
	 */
	public ConnectivesClass(String arg1, String arg2, String conn){
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.conn = conn;
	}
	
	@Override
	public String toString(){
		String result = "Arg1 = "+arg1+"\n";
		result = result + "Connective = "+conn+"\n";
		result = result + "Arg2 = "+arg2+"\n";
		return result;
	}
}
