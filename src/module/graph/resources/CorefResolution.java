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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * @author arpit
 *
 */
public class CorefResolution {

	private static CorefResolution corefInstance = null;
	private StanfordCoreNLP pipeline = null;

	static{
		corefInstance = new CorefResolution();
	}

	private CorefResolution(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("dcoref.score", true);
		props.setProperty("ner.useSUTime", "false");
		pipeline = new StanfordCoreNLP(props);
	}

	public static CorefResolution getInstance(){
		return corefInstance;
	}

	public static void main(String[] args){
		CorefResolution cr = CorefResolution.getInstance();
		HashMap<String, String> pronominalCoreferenceMap = cr.extractCoreferenceChain("John loves his wife.");
		for(String s : pronominalCoreferenceMap.keySet()){
			System.out.println("Pronoun: "+s+"\tCo-referent: "+ pronominalCoreferenceMap.get(s));
		}
	}

	/**
	 * extracts co-references and put them in {@link #corefMap}
	 * @param sentence
	 */
	public HashMap<String, String> extractCoreferenceChain(String sentence) {
		HashMap<String, String> corefMap = new HashMap<String, String>();

		Annotation document = new Annotation(sentence);

		pipeline.annotate(document);
		Map<Integer, CorefChain> coref = document.get(CorefChainAnnotation.class);

		for(Map.Entry<Integer, CorefChain> entry : coref.entrySet()) {
			CorefChain c = entry.getValue();

			CorefMention cm = c.getRepresentativeMention();
			String clust = "";
			List<CoreLabel> tks = document.get(SentencesAnnotation.class).get(cm.sentNum-1).get(TokensAnnotation.class);
			for(int i = cm.startIndex-1; i < cm.endIndex-1; i++)
				clust += tks.get(i).get(TextAnnotation.class) + " ";
			clust = clust.trim();
			//			System.out.println("representative mention: \"" + clust + "\" is mentioned by:");

			for(CorefMention m : c.getMentionsInTextualOrder()){
				String clust2 = "";
				tks = document.get(SentencesAnnotation.class).get(m.sentNum-1).get(TokensAnnotation.class);
				for(int i = m.startIndex-1; i < m.endIndex-1; i++)
					clust2 += tks.get(i).get(TextAnnotation.class) + " ";
				clust2 = clust2.trim();
				//don't need the self mention
				if(clust.equals(clust2))
					continue;

				//				System.out.println("\t" + clust2);
				corefMap.put(clust2.toLowerCase()+"_"+m.startIndex, 
						getModifiedRepresentativeMention(clust, cm));
			}
		}

		return corefMap;
	}

	private String getModifiedRepresentativeMention(String clust, CorefMention representativeMention) {
		StringBuffer strB = new StringBuffer();
		strB.append(":");
		int modifiedStartIndex = representativeMention.startIndex;

		String[] tokens= clust.toLowerCase().split("\\s+");
		int i=0;
		for(; i < tokens.length; i++) {
			if(tokens[i].equals("a") || tokens[i].equals("an") || tokens[i].equals("the"))
				modifiedStartIndex++;
			else
				break;
		}
		for(; i < tokens.length; i++) {
			strB.append(tokens[i]);
			if(i != tokens.length-1)
				strB.append("_");
		}
		strB.append("_").append(modifiedStartIndex);
		return strB.toString();
	}

}
