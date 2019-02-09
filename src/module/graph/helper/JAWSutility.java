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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.smu.tspell.wordnet.*;
import edu.smu.tspell.wordnet.impl.file.ReferenceSynset;

/**
 * Displays word forms and definitions for synsets containing the word form
 * specified on the command line. To use this application, specify the word
 * form that you wish to view synsets for, as in the following example which
 * displays all synsets containing the word form "airplane":
 * <br>
 * java TestJAWS airplane
 */
public class JAWSutility
{
	private String wordnet_dir = null;//"/host/stuff/CreateGraph/Files/WordNet-3.0/dict";
//	
	public JAWSutility(){
		String wordnetDir = Configurations.getProperty("wordnetFolder");
		if(wordnetDir.startsWith("./")){
			wordnetDir = Configurations.getCWD() + wordnetDir.substring(2);
		}
		wordnet_dir = wordnetDir+"/dict";
	}
	
	public String getSynonymWordnet(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();

		List<Synset> synset_list = null;
		if(use.equals("v"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.VERB));
		else if(use.equals("n"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.NOUN));
		else if(use.equals("a"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADJECTIVE));
		else if(use.equals("ad"))
			synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADVERB));

		for (Synset synset : synset_list) {
			//System.out.println("\nsynset type: " +SynsetType.ALL_TYPES[synset.getType().getCode()]);
			//System.out.println(" definition: " +synset.getDefinition());
			// word forms are synonyms:
			for (String wordForm : synset.getWordForms()) {
				if (!wordForm.equals(word) && wordForm!=null && wordForm!=""&&!wordForm.contains(" ")) {
					//System.out.println(" synonym: " +wordForm);
					return wordForm;
				}
			}
		}
		return null;
	}
	
	// returns the list of synonyms of this word
	public List<String> getAllSynonymsWordnet(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		List<String> returnList = new ArrayList<String>();

		if(!word.contains("_")){
			List<Synset> synset_list = null;
			if(use.equals("v"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.VERB));
			else if(use.equals("n"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.NOUN));
			else if(use.equals("j"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADJECTIVE));

			if(synset_list!=null){
				for (Synset synset : synset_list) {
					//System.out.println("\nsynset type: " +SynsetType.ALL_TYPES[synset.getType().getCode()]);
					//System.out.println(" definition: " +synset.getDefinition());
					// word forms are synonyms:
					for (String wordForm : synset.getWordForms()) {
						if (!wordForm.equals(word) && wordForm!=null && wordForm!="" && !wordForm.contains(" ")) {
							//System.out.println(" synonym: " +wordForm);
							//return wordForm;
							returnList.add(wordForm);
						}
					}
				}
			}
		}
		return returnList;
	}

	/**
	 * This method gets the list of synonyms of a word, given its sense id in WordNet and 
	 * its usage (POS)
	 * @param word
	 * @param use
	 * @param hashcode
	 * @return synsList
	 */
	public HashSet<String> getSynonyms(String word,String use, int hashcode){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		HashSet<String> synsList = new HashSet<String>();

		if(!word.contains("_")){
			List<Synset> synset_list = null;
			if(use.equals("v"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.VERB));
			else if(use.equals("n"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.NOUN));
			else if(use.equals("j"))
				synset_list=Arrays.asList(database.getSynsets(word, SynsetType.ADJECTIVE));

			if(synset_list!=null){
				for (Synset synset : synset_list) {
					if(synset.hashCode()==hashcode){
						
						// word forms are synonyms:
						for (String wordForm : synset.getWordForms()) {
//							if (!wordForm.equals(word) && wordForm!=null && wordForm!="" && !wordForm.contains(" ")) {
								synsList.add(wordForm);
//							}
						}
					}
				}
			}
		}
		return synsList;
	}
	
	// Returns the base form of the input word. We return the first base form from the output.
	public String getBaseForm(String word,String use){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		/*Morphology id=Morphology.getInstance();
		String[] arr = id.getBaseFormCandidates("winner", SynsetType.ADJECTIVE);
		System.out.println(arr[0]);*/
		List<String> base_list = null;
		edu.smu.tspell.wordnet.SynsetType stype=null;
		if(use.equalsIgnoreCase("v"))
			stype=SynsetType.VERB;
		else if(use.equalsIgnoreCase("n"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("p"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equalsIgnoreCase("r"))
			stype=SynsetType.ADVERB;
		else
			stype=SynsetType.NOUN;
		
		base_list=Arrays.asList(database.getBaseFormCandidates(word, stype));

		/*System.out.println("Base Form : ");
		for(String s:base_list)
			System.out.println(s);*/
		
		// check for conversions like asked--> aske
		if(base_list.size()>0){
			for(String str:base_list){
				if( Arrays.asList(database.getSynsets(str, stype)).size()>0 )
					return str;
			}
//			if(word.endsWith("ed") && base_list.get(0).endsWith("e")){
//				List<Synset> synset_list=Arrays.asList(database.getSynsets(base_list.get(0), stype));
//				if(synset_list.size()==0){
//					synset_list=Arrays.asList(database.getSynsets(base_list.get(0).substring(0, base_list.get(0).length()-2), stype));
//					if(synset_list.size()>0)
//						return base_list.get(0).substring(0, base_list.get(0).length()-2);
//				}
//			}
//			else{
				// check if the returned base form is a word in the wordnet..if not then don;t return this form
				List<Synset> synset_list=Arrays.asList(database.getSynsets(base_list.get(0), stype));
				if(synset_list.size()!=0){
					return base_list.get(0);
				}
//			}
		}

		return word;
	}

	public String returnVerbForm(String word,String use){
		System.setProperty("wordnet.database.dir",this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		//String word="winner";
		edu.smu.tspell.wordnet.SynsetType stype=null;
		if(use.equals("v"))
			stype=SynsetType.VERB;
		else if(use.equals("n"))
			stype=SynsetType.NOUN;
		else if(use.equals("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equals("ad"))
			stype=SynsetType.ADVERB;
		List<Synset> synset_list = Arrays.asList(database.getSynsets(word, stype));
		for (int i=0;i<synset_list.size();i++) {
			Synset synset=synset_list.get(i);
			WordSense[] wordsense = synset.getDerivationallyRelatedForms(word);
			//System.out.println(wordsense[0].getWordForm());
			//System.out.println(wordsense[0].getSynset().getType());
			if(wordsense.length>0 && wordsense[0].getSynset().getType()==SynsetType.VERB)
				return wordsense[0].getWordForm();
		}

		return word;
	}
	
	public int getAncester(String word, String use){
		int lexicalCode = -1;
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		
		edu.smu.tspell.wordnet.SynsetType stype=null;
		try{
		if(use.equalsIgnoreCase("v"))
			stype=SynsetType.VERB;
		else if(use.equalsIgnoreCase("n"))
			stype=SynsetType.NOUN;
		else if(use.equalsIgnoreCase("j"))
			stype=SynsetType.ADJECTIVE;
		else if(use.equalsIgnoreCase("ad"))
			stype=SynsetType.ADVERB;
		
		ReferenceSynset referenceSynset = (ReferenceSynset) database.getSynsets(word, stype)[0];
		lexicalCode =referenceSynset.getLexicalFileNumber();
		}catch(ArrayIndexOutOfBoundsException e){
//			System.out.println("Out of bounds");
		}
		return lexicalCode;
	}
	
	public String getWordFromOffset(String word){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] result = database.getSynsets(word);
		
		
		return result.toString();
	}

	
	public boolean isAnAdjective(String word){
		System.setProperty("wordnet.database.dir", this.wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		if((database.getSynsets(word, SynsetType.ADJECTIVE)).length > 0){
			return true;
		}
		
		return false;
	}
	
	public Synset[] getSynsets(String word, SynsetType usage){
		System.setProperty("wordnet.database.dir",wordnet_dir);
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(word, usage); 
		return synsets;
	}
	

	public static void main(String[] args){
		JAWSutility jaws = new JAWSutility();
		System.out.println(jaws.getBaseForm("off","r"));
//		HashSet<String> syns = jaws.getSynonyms("bank", "n",9236472);
//		for(String syn : syns){
//			System.out.println(syn);
//		}
		
	}
	
}
