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
package module.graph.resources;

import java.util.HashMap;
import java.util.Map;

import module.graph.dependencymapping.SemanticMappingResource;

public class ResourceProvider {

	private static Map<String, IResource> resourceIdToResourcemapping;
	
	static {
		resourceIdToResourcemapping = new HashMap<String, IResource>(10);
		
		resourceIdToResourcemapping.put("jigsaw", new JIGSAWResource());
		resourceIdToResourcemapping.put("dependencyparser", new DependencyParserResource());
		resourceIdToResourcemapping.put("articles", new ArticlesSetResource());
		resourceIdToResourcemapping.put("conjunctions", new ConjunctionFileResource());
		
		resourceIdToResourcemapping.put("pronounRoles", new PronounRolesResource());
		resourceIdToResourcemapping.put("propbank", new PropbankResource());
	
		resourceIdToResourcemapping.put("jaws", new JAWSResource());
		
		resourceIdToResourcemapping.put("semanticMapping", new SemanticMappingResource());
		
		resourceIdToResourcemapping.put("wekaClassification", new WekaClassifierResource());
		
		resourceIdToResourcemapping.put("adjectiveClasses", new AdjectiveClassResource());
		
		resourceIdToResourcemapping.put("contractions", new ContractionsResourse());
	}
	
	public static IResource getJIGSAWResource() {
		return resourceIdToResourcemapping.get("jigsaw");
	}

	public static IResource getJAWSResource() {
		return resourceIdToResourcemapping.get("jaws");
	}

	public static IResource getPropbankResource() {
		return resourceIdToResourcemapping.get("propbank");
	}

	public static IResource getPronounRolesResource() {
		return resourceIdToResourcemapping.get("pronounRoles");
	}

	public static IResource getConjuntionResource() {
		return resourceIdToResourcemapping.get("conjunctions");
	}

	public static IResource getArticlesSetResource() {
		return resourceIdToResourcemapping.get("articles");
	}

	public static IResource getDependencyParserResource() {
		return resourceIdToResourcemapping.get("dependencyparser");
	}
	
	public static IResource getSemanticMappingResource() {
		return resourceIdToResourcemapping.get("semanticMapping");
	}
	
	public static IResource getWekaClassifierResource() {
		return resourceIdToResourcemapping.get("wekaClassification");
	}
	
	public static IResource getAdjClassesResource() {
		return resourceIdToResourcemapping.get("adjectiveClasses");
	}
	
	public static IResource getContractionsResource() {
		return resourceIdToResourcemapping.get("contractions");
	}
}
