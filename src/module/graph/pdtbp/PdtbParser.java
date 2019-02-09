/**
 * 
 */
package module.graph.pdtbp;

/**
 * @author arpit
 *
 */
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;
import module.graph.pdtbp.resources.ModelsResource;
import module.graph.pdtbp.utils.ArgExtOutput;
import module.graph.pdtbp.utils.ArgPosOutput;
import module.graph.pdtbp.utils.ClassifierOutputs;
import module.graph.pdtbp.utils.ConCompOutput;
import module.graph.pdtbp.utils.ExpCompOutput;
import module.graph.pdtbp.utils.NoExpCompOutput;
import module.graph.pdtbp.utils.SpanTreeExtractor;
import module.graph.pdtbp.utils.SynParseData;

public class PdtbParser {
	private ModelsResource modelsResource = null;
	
	public PdtbParser(){
		this.modelsResource = ModelsResource.getInstance();
	}
	
	public static void main(String[] args) throws IOException {
		String sentence = "The older students were bullying the younger students so we rescued them.";
		sentence = "John loves Mia because she loves men , but she is a bad girl.";
//		sentence = "The Treasury said the U.S. will default on Nov. 9 if Congress doesn’t act by then.";
//		sentence = "Political and currency gyrations can whipsaw the funds. Another concern: The funds’ share prices tend to swing more than the broader market";
		String pTreeStr = "(ROOT (S (NP (DT The) (JJR older) (NNS students)) (VP (VBD were) (VP (VBG bullying) (NP (DT the) (JJR younger) (NNS students)) (SBAR (IN so) (S (NP (PRP we)) (VP (VBD rescued) (NP (PRP them))))))) (. .)))";
		pTreeStr = "(ROOT (S (S (NP (NNP John)) (VP (VBZ loves) (NP (NNP Mia)) (SBAR (IN because) (S (NP (PRP she)) (VP (VBZ loves) (NP (NNS men))))))) (, ,) (CC but) (S (NP (PRP she)) (VP (VBZ is) (NP (DT a) (JJ bad) (NN girl)))) (. .)))";
//		pTreeStr = "(ROOT (S (NP (DT The) (NNP Treasury)) (VP (VBD said) (SBAR (S (NP (DT the) (NNP U.S.)) (VP (MD will) (VP (VB default) (PRT (RP on)) (NP (NNP Nov.) (CD 9)) (SBAR (IN if) (S (NP (NNP Congress)) (VP (VBZ does) (RB n't) (VP (VB act) (PP (IN by) (NP (RB then)))))))))))) (. .)))";
//		pTreeStr = "(ROOT (S (NP (NP (NNP Political)) (CC and) (NP (NP (NP (NN currency) (NNS gyrations)) (SBAR (S (VP (MD can) (VP (VB whipsaw) (NP (DT the) (NNS funds))))))) (. .) (NP (NP (DT Another) (NN concern)) (: :) (NP (NP (DT The) (NNS funds) (POS ')) (NN share) (NNS prices))))) (VP (VBP tend) (S (VP (TO to) (VP (VB swing) (NP (JJR more)) (PP (IN than) (NP (DT the) (JJR broader) (NN market)))))))))";
		Tree pTree = Tree.valueOf(pTreeStr);
		
		PdtbParser pdtbp = new PdtbParser();
		ClassifierOutputs output = pdtbp.parseSentence(sentence,pTree);
		for(String pipe : output.getArgExtOutput().getPipeOutList()){
			System.out.println(pipe);
		}
	}
	
	public ClassifierOutputs parseSentence(String sentence, Tree pTree, ArrayList<String> depTreeList) throws IOException{
		ClassifierOutputs result = new ClassifierOutputs();
		
		ArrayList<String> spanList = SpanTreeExtractor.anyTextToSpanGen(pTree, sentence);
		
		SynParseData synPData = new SynParseData();
		synPData.setPennTree(pTree);
		synPData.setSpanList(spanList);
		synPData.setDepList(depTreeList);
		
		//		log.info("Running the PDTB parser");
		Component parser = new ConnComp();
//		log.info("Running connective classifier...");
		ConCompOutput connResult = (ConCompOutput) parser.parseAnyText("conn",sentence,synPData,this.modelsResource);
//		System.out.println(connResult.getOutput());
//		System.out.println();//connResult.getConSpan());
		result.setConOutput(connResult);
//		System.exit(0);
////		log.info("Done.");
		
		parser = new ArgPosComp(connResult);
////		log.info("Running argument position classifier...");
		ArgPosOutput argPosResult = (ArgPosOutput) parser.parseAnyText("argpos",sentence,synPData,this.modelsResource);
////		log.info("Done.");
//		System.out.println(argPosResult.getOutput());
		result.setArgPosOutput(argPosResult);
//		System.out.println(connResult.getConSpan());
//		System.exit(0);
		
		parser = new ArgExtComp(connResult,argPosResult);
////		log.info("Running argument extractor classifier...");
		ArgExtOutput argExtResult = (ArgExtOutput) parser.parseAnyText("argext",sentence,synPData,this.modelsResource);
		Map<String, String> pipeMap = genPipeMap(argExtResult.getPipeOutList());
		result.setArgExtOutput(argExtResult);
//		System.exit(0);
////		log.info("Done.");
		
		parser = new ExplicitComp(connResult);
////		log.info("Running Explicit classifier...");
		ExpCompOutput expResult = (ExpCompOutput) parser.parseAnyText("exp",sentence,synPData,this.modelsResource);
		joinSense(pipeMap, expResult.getPipeOut(), argExtResult.getPipeOutList());
		result.setExpOutput(expResult);
////		log.info("Done.");
		
		parser = new NonExplicitComp(expResult);
//////		log.info("Running NonExplicit classifier...");
		NoExpCompOutput nonExpResult = (NoExpCompOutput) parser.parseAnyText("nonexp",sentence,synPData,this.modelsResource);
		result.setNoExpOutput(nonExpResult);
//		appendToFile(expResult.getPipeOut(), nonExpResult.getOutputList());
//////		log.info("Done with everything. The PDTB relations for the file are in: " + pipeFile);
	
		return result;
	}
	
	public ClassifierOutputs parseSentence(String sentence, Tree pTree) throws IOException{
		ClassifierOutputs result = new ClassifierOutputs();
		
		ArrayList<String> spanList = SpanTreeExtractor.anyTextToSpanGen(pTree, sentence);
		
		SynParseData synPData = new SynParseData();
		synPData.setPennTree(pTree);
		synPData.setSpanList(spanList);
		
		//		log.info("Running the PDTB parser");
		Component parser = new ConnComp();
//		log.info("Running connective classifier...");
		ConCompOutput connResult = (ConCompOutput) parser.parseAnyText("conn",sentence,synPData,this.modelsResource);
//		System.out.println(connResult.getOutput());
//		System.out.println();//connResult.getConSpan());
		result.setConOutput(connResult);
//		System.exit(0);
////		log.info("Done.");
		
		parser = new ArgPosComp(connResult);
////		log.info("Running argument position classifier...");
		ArgPosOutput argPosResult = (ArgPosOutput) parser.parseAnyText("argpos",sentence,synPData,this.modelsResource);
////		log.info("Done.");
//		System.out.println(argPosResult.getOutput());
		result.setArgPosOutput(argPosResult);
//		System.out.println(connResult.getConSpan());
//		System.exit(0);
		
		parser = new ArgExtComp(connResult,argPosResult);
////		log.info("Running argument extractor classifier...");
		ArgExtOutput argExtResult = (ArgExtOutput) parser.parseAnyText("argext",sentence,synPData,this.modelsResource);
		Map<String, String> pipeMap = genPipeMap(argExtResult.getPipeOutList());
		result.setArgExtOutput(argExtResult);
//		System.exit(0);
////		log.info("Done.");
		
		parser = new ExplicitComp(connResult);
////		log.info("Running Explicit classifier...");
		ExpCompOutput expResult = (ExpCompOutput) parser.parseAnyText("exp",sentence,synPData,this.modelsResource);
		joinSense(pipeMap, expResult.getPipeOut(), argExtResult.getPipeOutList());
		result.setExpOutput(expResult);
////		log.info("Done.");
		
//		parser = new NonExplicitComp(expResult);
//////		log.info("Running NonExplicit classifier...");
//		NoExpCompOutput nonExpResult = (NoExpCompOutput) parser.parseAnyText("nonexp",sentence,synPData);
//		appendToFile(expResult.getPipeOut(), nonExpResult.getOutputList());
//////		log.info("Done with everything. The PDTB relations for the file are in: " + pipeFile);
	
		return result;
	}
	
//	private static void appendToFile(File pipeFile, File nonExpSenseFile) throws IOException {
//
//		try (FileWriter writer = new FileWriter(pipeFile, true); BufferedReader reader = Util.reader(nonExpSenseFile)) {
//			String line;
//			while ((line = reader.readLine()) != null) {
//				writer.write(line + Util.NEW_LINE);
//			}
//		}
//	}
	
	private void joinSense(Map<String, String> pipeMap, ArrayList<String> expSenseFile, ArrayList<String> pipeList) throws IOException {
				for(String line : expSenseFile){
				String[] tmp = line.split("\\|", -1);
				String pipe = pipeMap.get(tmp[0]);
				if (pipe == null) {
//					log.error("Cannot find connective span in pipe map.");
				}
				String[] cols = pipe.split("\\|", -1);

				StringBuilder resultLine = new StringBuilder();

				for (int i = 0; i < cols.length; i++) {
					String col = cols[i];
					if (i == 11) {
						resultLine.append(tmp[1] + "|");
					} else {
						resultLine.append(col + "|");
					}
				}
				resultLine.deleteCharAt(resultLine.length() - 1);
				cols = resultLine.toString().split("\\|", -1);

				pipeMap.put(tmp[0], resultLine.toString());
			}

		pipeList.clear();
		for (String pipe : pipeMap.values()) {
			pipeList.add(pipe);
		}
		
	}

	private static Map<String, String> genPipeMap(ArrayList<String> argExpOutput) throws IOException {

		Map<String, String> map = new HashMap<>();
			for(String line : argExpOutput){
				String[] cols = line.split("\\|", -1);
				if (cols.length != 48) {
//					log.error("Pipe file " + pipeFile.getAbsolutePath() + " is corrupted, number of columns is "
//							+ cols.length + " instead of 48.");
				}
				map.put(cols[3], line);
			}
		return map;
	}

}
