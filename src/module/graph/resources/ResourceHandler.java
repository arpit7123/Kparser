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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import kparser.devel.PropbankData;
import weka.classifiers.Classifier;
import module.graph.dependencymapping.MappingHandlerOutput;
import module.graph.dependencymapping.SemanticMappingResource;

public class ResourceHandler {

	private IResource jigsawResource;
	private IResource dependencyParserResource;
	private IResource articlesSetResource;
	private IResource conjunctionResource;
	private IResource pronounRolesResource;
	private IResource propbankResource;
	private IResource jawsResource;
	private IResource semanticMappingResource;
	private IResource wekaClassifierResource;
	private IResource adjectiveClassesResource;
	private IResource contractionsResource;
	
	private static ResourceHandler resHandler = null;
	
	private ResourceHandler() {
		jigsawResource = ResourceProvider.getJIGSAWResource();
		dependencyParserResource = ResourceProvider.getDependencyParserResource();
		articlesSetResource = ResourceProvider.getArticlesSetResource();
		conjunctionResource = ResourceProvider.getConjuntionResource();
		pronounRolesResource = ResourceProvider.getPronounRolesResource();
		propbankResource= ResourceProvider.getPropbankResource();
		jawsResource = ResourceProvider.getJAWSResource();
		semanticMappingResource = ResourceProvider.getSemanticMappingResource();
		wekaClassifierResource = ResourceProvider.getWekaClassifierResource();
		adjectiveClassesResource = ResourceProvider.getAdjClassesResource();
		contractionsResource = ResourceProvider.getContractionsResource();
	}
	
	static{
		resHandler = new ResourceHandler();
	}
	
	public static ResourceHandler getInstance(){
		return resHandler;
	}
	
	public InputDependencies getSyntacticDeps(String sentence, boolean bgFlag, int index) {
		return ((DependencyParserResource)dependencyParserResource).extractDependencies(sentence, bgFlag, index);
	}
	
	public Object getSyntacticTree(String sentence) {
		return ((DependencyParserResource)dependencyParserResource).getSyntacticTree(sentence);
	}
	
	public HashMap<String, ArrayList<String>> getWordSenses(String sentence,int index) {
		return ((JIGSAWResource)jigsawResource).getWordSenses(sentence,index);
	}
	
	public boolean isAnAdjective(String word) {
		return ((JAWSResource)jawsResource).isAnAdjective(word);
	}
	
	public boolean isAnArticle(String word) {
		return ((ArticlesSetResource)articlesSetResource).contains(word);
	}
	
	public String getBaseFormOfWord(String word, String use) {
		return ((JAWSResource)jawsResource).getBaseForm(word, use);
	}
	
	public boolean isAPronounRole(String word) {
		return ((PronounRolesResource)pronounRolesResource).containsKey(word);
	}
	
	public String getPronounRole(String word) {
		return ((PronounRolesResource)pronounRolesResource).get(word);
	}
	
	public Set<String> getAllConjunctions() {
		return ((ConjunctionFileResource)conjunctionResource).getKeySet();
	}
	
	public ArrayList<String> getConjunctionLabel(String conjunction) {
		return ((ConjunctionFileResource)conjunctionResource).getConjLabel(conjunction);
	}
	
	public ArrayList<String> getConjunctionLabel(String conjunction, String arg1, String arg2) {
		return ((ConjunctionFileResource)conjunctionResource).getConjLabel(conjunction,arg1,arg2);
	}
	
	public String getSemanticRoleLabel(String baseWord, String group) {
		return ((PropbankResource)propbankResource).getSemanticRole(baseWord, group);
	}
	
	public MappingHandlerOutput getSemanticMapping(InputDependencies inDep) {
		return ((SemanticMappingResource)semanticMappingResource).getSemanticMappings(inDep);
	}
	
	public Classifier getWekaClassifier(String preposition) {
		return ((WekaClassifierResource)wekaClassifierResource).getClassifier(preposition);
	}
	
	public Integer getFeature7Value(String input) {
		return ((WekaClassifierResource)wekaClassifierResource).getFeature7Value(input);
	}
	
	public Integer getFeature8Value(String input) {
		return ((WekaClassifierResource)wekaClassifierResource).getFeature8Value(input);
	}
	
	public String getPrepClasses(String input) {
		return ((WekaClassifierResource)wekaClassifierResource).getClassesForPrep(input);
	}
	
	public String getAdjectiveClass(String adjective) {
		return ((AdjectiveClassResource)adjectiveClassesResource).getAdjectiveClass(adjective);
	}
	
	/**
	 * This method returns the set of most popular contractions in English language.
	 * @return
	 */
	public TreeSet<String> getContractionsSet() {
		return ((ContractionsResourse)contractionsResource).getContractions();
	}
	
	/**
	 * This method takes an English contraction as input and returns its expanded form.
	 * @param contraction
	 * @return
	 */
	public String getExpandedForm(String contraction) {
		return ((ContractionsResourse)contractionsResource).getExpandedForm(contraction);
	}
	
	/**
	 * This method takes an English verb as input and returns all the verbs related (originated) from it in the Propbank framefiles.
	 * @param driverVerb
	 * @return
	 */
	public PropbankData getPrpbnkVrbGrp(String driverVerb) {
		return ((PropbankResource) propbankResource).getPropbankData(driverVerb);
	}
	
	/**
	 * This method takes an English verb as input and returns true is that word also exists as an adjective in the Propbank data. 
	 * Otherwise returns false.
	 * @param verb
	 * @return true/false
	 */
	public boolean verbUsedAsAdj(String verb) {
		return ((PropbankResource) propbankResource).verbUsedAsAdj(verb);
	}
}
