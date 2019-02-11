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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.google.common.collect.Maps;

import module.graph.helper.Configurations;
import module.graph.helper.NEObject;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * This class uses the Stanford NER module to tag the Named Entities present
 * in a particular sting and put them in a map.
 * 
 * @author Arpit Sharma
 *
 */
public class NamedEntityTagger {

	public String NAMED_MULTI_WORD_SEPARATOR = "_";
	public final int CLASS_3 = 3;
	public final int CLASS_7 = 7;
	public final int CLASS_4 = 4;
	
	private AbstractSequenceClassifier<CoreLabel> threeClassClassifier;
	private AbstractSequenceClassifier<CoreLabel> sevenClassClassifier;
	
	private HashMap<String, String> stringToNamedEntityMap;
	private String modifiedSentence;
	
	private static NamedEntityTagger netInstance = null;
	
	static {
		netInstance = new NamedEntityTagger();
	}

	/**
	 * This is a private constructor.
	 */
	private NamedEntityTagger(){
		try {
			String classifierFile = Configurations.getProperty("threeClassNEClassifier");
			if(classifierFile.startsWith("./")){
				classifierFile = Configurations.getCWD() + classifierFile.substring(2);
			}
			System.out.println("CLASSIFIER 3: " + classifierFile);
			threeClassClassifier = CRFClassifier.getClassifier(new File(classifierFile));
			
			System.out.println("CLASSIFIER mod: " + threeClassClassifier);
			
			classifierFile = Configurations.getProperty("sevenClassNEClassifier");
			if(classifierFile.startsWith("./")){
				classifierFile = Configurations.getCWD() + classifierFile.substring(2);
			}
			System.out.println("CLASSIFIER 7: " + classifierFile);
			sevenClassClassifier = CRFClassifier.getClassifier(new File(classifierFile));
			System.out.println("CLASSIFIER mod: " + sevenClassClassifier);
				
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			System.out.println("Error in reading NE classifiers!");
		}
	}

	/**
	 * This is a static method to get the instance of EventRelationsExtractor class.
	 * @return eventRelationsInstance, an instance of EventRelationsExtractor class.
	 */
	public static NamedEntityTagger getInstance() {
		return netInstance;
	}
	
//	public NamedEntityTagger(HashMap<String, String> stringToNamedEntityMap,
//			String modifiedSentence) {
//		super();
//		this.stringToNamedEntityMap = stringToNamedEntityMap;
//		this.modifiedSentence = modifiedSentence;
//	}

	
	public HashMap<String, String> getStringToNamedEntityMap() {
		return stringToNamedEntityMap;
	}


	public String getModifiedSentence() {
		return modifiedSentence;
	}


	public NEObject tagNamedEntities(String sentence) {
		char[] tempSentenceA = threeClassClassifier.classifyWithInlineXML(sentence)
				.toCharArray();
		char[] tempSentenceB = sevenClassClassifier.classifyWithInlineXML(sentence)
				.toCharArray();
		
		NEObject neObj1 = processTaggedOutput(tempSentenceA,CLASS_3);
		NEObject neObj2 = processTaggedOutput(tempSentenceB,CLASS_7);

		return mergeNEObjs(sentence,neObj1,neObj2);
	}
	
	private NEObject mergeNEObjs(String text, NEObject neObj1, NEObject neObj2){
		String modifiedText = text;
		HashMap<String,String> neMap = Maps.newHashMap();
		
		HashMap<String,String> neMap1 = neObj1.getNamedEntityMap();
		HashMap<String,String> neMap2 = neObj2.getNamedEntityMap();
		for(String ne : neMap1.keySet()){
			if(ne.contains(NAMED_MULTI_WORD_SEPARATOR)){
				String newNe = ne.replaceAll(NAMED_MULTI_WORD_SEPARATOR, " ");
				if(modifiedText.contains(newNe)){
					modifiedText = modifiedText.replaceAll(newNe, ne);
				}
			}			
			neMap.put(ne, neMap1.get(ne));
		}
		
		for(String ne : neMap2.keySet()){
			if(ne.contains(NAMED_MULTI_WORD_SEPARATOR)){
				String newNe = ne.replaceAll(NAMED_MULTI_WORD_SEPARATOR, " ");
				if(modifiedText.contains(newNe)){
					modifiedText = modifiedText.replaceAll(newNe, ne);
				}
			}			
			neMap.put(ne, neMap2.get(ne));
		}
		
		return new NEObject(neMap,modifiedText);
	}
	
	private NEObject  processTaggedOutput(char[] tempSentenceA, int model){
		StringBuffer newSentence = new StringBuffer();
		HashMap<String, String> stringToNamedEntityMap = new HashMap<String, String>();
		for (int i = 0; i < tempSentenceA.length;) {

			if (tempSentenceA[i] == '<') {
				/**
				 * we will process <PERSON>Rajat Raina</PERSON> in this part
				 */

				// ignore first < of starting tag
				i++;
				// collect the entity type such as PERSON in the buffer
				StringBuffer namedEntityType = new StringBuffer();
				for (; i < tempSentenceA.length && tempSentenceA[i] != '>'; i++){
					namedEntityType.append(tempSentenceA[i]);
				}
				
				// ignore > of starting tag
				i++;
				// collect the actual entity inside the XML tags
				StringBuffer theEntity = new StringBuffer();
				for (; i < tempSentenceA.length && tempSentenceA[i] != '<'; i++){				
					if(model==CLASS_3){
						if(!namedEntityType.toString().equalsIgnoreCase("PERSON")
								&& !namedEntityType.toString().equalsIgnoreCase("ORGANIZATION")
								&& !namedEntityType.toString().equalsIgnoreCase("LOCATION")){
							newSentence.append(tempSentenceA[i]);
						}else{
							theEntity.append(tempSentenceA[i]);
						}
					}else if(model == CLASS_7){
						if(!namedEntityType.toString().equalsIgnoreCase("DATE")
								&& !namedEntityType.toString().equalsIgnoreCase("TIME")
								&& !namedEntityType.toString().equalsIgnoreCase("PERCENT")
								&& !namedEntityType.toString().equalsIgnoreCase("MONEY")){
							newSentence.append(tempSentenceA[i]);
						}else{
							theEntity.append(tempSentenceA[i]);
						}
					}
				}

				// ignore whole end tag
				i++;
				for (; i < tempSentenceA.length && tempSentenceA[i] != '>'; i++);
				i++;

				String entity = theEntity.toString().replaceAll("\\s+",
						NAMED_MULTI_WORD_SEPARATOR);

				if(!entity.equalsIgnoreCase("")){
				stringToNamedEntityMap.put(entity, namedEntityType.toString());
//				stringToNamedEntityMap.put(
//						entity.replaceAll(NAMED_MULTI_WORD_SEPARATOR, ""), namedEntityType.toString());
				newSentence.append(entity);
				}
			} else {
				newSentence.append(tempSentenceA[i]);
				i++;
			}
		}
		
		return new NEObject(stringToNamedEntityMap,newSentence.toString());
	}
	
	@Override
	public String toString() {
		return "NamedEntityTagger [stringToNamedEntityMap="
				+ stringToNamedEntityMap + ", modifiedSentence="
				+ modifiedSentence + "]";
	}


	public void main(String[] args) {
		String text = "Rajat Raina goes to school on Wednesday at 1630PST";
		text = "John took twenty-five percent of the stakes.";
		text = "Mr Moss said he refused to do so because he feared the journey would compromise the integrity of the fragile pieces .";
				
//		text = "John loved Mia on Wednesday at 630am.";
		System.out.println(tagNamedEntities(text));
	}
}
