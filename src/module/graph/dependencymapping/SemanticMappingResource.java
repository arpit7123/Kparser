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
 * This class is semantic mapping resource. It is used to read mapping rules and initialize the resources.
 * @author Arpit Sharma
 * @since 01/15/2015
 */
import module.graph.resources.IResource;

public class SemanticMappingResource extends SemanticMappingHandler implements IResource  {

		public SemanticMappingResource() {
			super();
		}
}
