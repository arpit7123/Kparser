/**
 * 
 */
package module.graph.pdtbp.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import module.graph.helper.ConnectivesClass;
import module.graph.pdtbp.PdtbParser;
import module.graph.pdtbp.utils.ClassifierOutputs;
import module.graph.resources.ResourceHandler;

import com.google.common.collect.Lists;

import edu.stanford.nlp.trees.Tree;

/**
 * @author arpit
 *
 */
public class PDTBResource {		
		private int baseIndex = 0;
		private PdtbParser pdtbParser = null;
		private static PDTBResource pdtbInstance = null;
		@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ResourceHandler resHandler = null;
		@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> posMap = null;
		
		static {
			pdtbInstance = new PDTBResource();
		}

		private PDTBResource(){
			pdtbParser = new PdtbParser();
		}

		public static PDTBResource getInstance() {
			return pdtbInstance;
		}
		
		
		public Object getParsedSent(String inputText, String pennTree) throws Exception{
			Tree pTree = Tree.valueOf(pennTree);
			return pdtbParser.parseSentence(inputText, pTree);
		}
		
		public ArrayList<String> getEventRelations(String inputText, String pennTree, String depTree, int baseIndex) throws Exception{
			
			if(baseIndex!=0){
				this.baseIndex = baseIndex;
			}
			
			ArrayList<String> result = Lists.newArrayList();
			Object parsedText = getParsedSent(inputText,pennTree);
					
			ArrayList<ConnectivesClass> listOfConnArgs = processParsedText(parsedText, inputText);
			
			for(ConnectivesClass connObject : listOfConnArgs){
				String arg1 = connObject.getArg1();
				String arg2 = connObject.getArg2();
				String conn = connObject.getConn();
				
				ArrayList<String> eventList1 = getEventList(arg1,inputText,"first");
				ArrayList<String> eventList2 = getEventList(arg2,inputText,"second");
//				String relation = getRelation(conn);
//				String event2 = null;
//				if(eventList2.size()>0){
//					event2 = eventList2.get(0);
//				}
//				
//				if(event2!=null && relation!=null){
//					for(String s : eventList1){
//						result.add(s+","+relation+","+event2);
//					}
//				}
				
				for(String e1 : eventList1){
					for(String e2 : eventList2){
						result.addAll(getRelation(conn,e1,e2));
					}
				}
				
			}			
			return result;
		}
		
		private ArrayList<String> getEventList(String arg, String inString, String identifier) throws Exception{
			inString = inString.replaceAll("'s", " 's");
			inString = inString.replaceAll(" +", " ");
			arg = arg.replaceAll("'s", " 's");
			arg = arg.replaceAll(" +", " ");
			ArrayList<String> events = Lists.newArrayList();
			Pattern p = Pattern.compile("(.*)("+arg+")(.*)");
			Matcher m = p.matcher(inString);
			if(m.find()){
				String[] preWords = null;
				String[] argWords = null;
				if(m.group(1).trim().equalsIgnoreCase("")){
					preWords = new String[0];
				}else{
					preWords = m.group(1).split(" ");
				}
				
				if(m.group(2).trim().equalsIgnoreCase("")){
					argWords = new String[0];
				}else{
					argWords = m.group(2).split(" ");
				}
						
				int indx = this.baseIndex + preWords.length+1;
				for(String argWord : argWords){
					if(posMap.get(argWord+"-"+indx).startsWith("V")){
						if(identifier.equalsIgnoreCase("sec")){
							events.add(argWord+"-"+indx);
							return events;
						}else{					
							events.add(argWord+"-"+indx);
						}
					}
					indx++;
				}
			}
			return events;
		}
		
		private ArrayList<String> getRelation(String connective){
			return resHandler.getConjunctionLabel(connective);
		}
		
		private ArrayList<String> getRelation(String connective, String arg1, String arg2){
			return resHandler.getConjunctionLabel(connective,arg1,arg2);
		}
		
		private ArrayList<ConnectivesClass> processParsedText(Object parsedInput, String inText) throws Exception{
			ArrayList<ConnectivesClass> listOfConnectives = Lists.newArrayList();
//			ArrayList<String> inputWords = Lists.newArrayList(Splitter.on(" ").split(inText));
			ClassifierOutputs out = (ClassifierOutputs) parsedInput;
			for(String text : out.getArgExtOutput().getPipeOutList()){
//				System.out.println("Parsed output is: --- "+text);
//				text = text.replaceAll("[|]+","|");
				
				String[] args = text.split("\\|");
//				Explicit|||15..22||because||0|because|||Contingency.Cause|||||||||||0..14|0|John loves Mia||||||||23..36|0|she loves men|||||||||||||
				String type = args[0];
				String connIndx = args[3];
//				String connIndx = args[1];
//				String conn = args[2];
				String[] connIndxArr = connIndx.split("\\.\\.");
				int connStrtIndx = Integer.parseInt(connIndxArr[0]);
				int connEndIndx = Integer.parseInt(connIndxArr[1]);
				String conn = inText.substring(connStrtIndx, connEndIndx);
				
//				String connClass = args[5];
				
//				String arg1Indx = args[6];
//				String arg1 = args[8];
				String arg1Indx = args[22];
				String[] arg1IndxArr = arg1Indx.split("\\.\\.");
				int arg1StrtIndx = 0;//Integer.parseInt(arg1IndxArr[0]);
				int arg1EndIndx = Integer.parseInt(arg1IndxArr[1]);
				String arg1 = null;
				if(connStrtIndx+1>arg1EndIndx){
					arg1 = inText.substring(arg1StrtIndx, connStrtIndx-1).trim();
				}else{
					arg1 = inText.substring(arg1StrtIndx, arg1EndIndx);
				}
				
//				String arg2Indx = args[9];
//				String arg2 = args[11];
				String arg2Indx = args[32];
				String[] arg2IndxArr = arg2Indx.split("\\.\\.");
				int arg2StrtIndx = Integer.parseInt(arg2IndxArr[0]);
				int arg2EndIndx = Integer.parseInt(arg2IndxArr[1]);
				String arg2 = null;
				if(arg2StrtIndx>connEndIndx+1){
					arg2 = inText.substring(connEndIndx+1, arg2EndIndx);
				}else{
					arg2 = inText.substring(arg2StrtIndx, arg2EndIndx);
				}
							
				listOfConnectives.add(new ConnectivesClass(arg1,arg2,conn));
			}
			
			return listOfConnectives;
		}
		
		public static void main(String[] args) throws Exception{
			PDTBResource p = PDTBResource.getInstance();

			String sentence = "The older students were bullying the younger students so we rescued them.";
			sentence = "John loves Mia because she loves men , but she is a bad girl.";
//			sentence = "John loves Mia because she loves men, but she is a bad girl.";
			String pTreeStr = "(ROOT (S (NP (DT The) (JJR older) (NNS students)) (VP (VBD were) (VP (VBG bullying) (NP (DT the) (JJR younger) (NNS students)) (SBAR (IN so) (S (NP (PRP we)) (VP (VBD rescued) (NP (PRP them))))))) (. .)))";
			pTreeStr = "(ROOT (S (S (NP (NNP John)) (VP (VBZ loves) (NP (NNP Mia)) (SBAR (IN because) (S (NP (PRP she)) (VP (VBZ loves) (NP (NNS men))))))) (, ,) (CC but) (S (NP (PRP she)) (VP (VBZ is) (NP (DT a) (JJ bad) (NN girl)))) (. .)))";
//			
////			Exp 0 Cause|(because )|1|3|4|4|5|7#Exp 1 Contrast|(but )|1|8|9|9|10|15#
			ArrayList<String> output = p.getEventRelations(sentence, pTreeStr, null, 0);
			for(String edge : output){
				System.out.println(edge);
			}
			
//			ClassifierOutputs c = new ClassifierOutputs();
//			ArgExtOutput argExtOut = new ArgExtOutput();
//			ArrayList<String> out = new ArrayList<String>();
//			out.add("Explicit|||15..22||because||0|because|||Contingency.Cause|||||||||||0..14|0|John loves Mia||||||||23..36|0|she loves men|||||||||||||");
//			out.add("Explicit|||39..42||but||0|but|||Comparison.Contrast|||||||||||0..36|0|John loves Mia because she loves men||||||||43..60|0|she is a bad girl|||||||||||||");
//			argExtOut.setPipeOutList(out);
//			c.setArgExtOutput(argExtOut);
//			ArrayList<ConnectivesClass> result = p.processParsedText(c, sentence);
//			System.out.println(result);
		}
		
		
		public void main_old(String[] args) throws Exception{
			PDTBResource p = PDTBResource.getInstance();
			String s = "Exp 1 Cause|(because )|12|16|17|17|18|22#Exp 0 Cause|(so )|1|8|9|9|10|23#";
			String input = "The older students were bullying the younger students so we rescued them and we killed them because he was a bad man .";
			ArrayList<ConnectivesClass> c = processParsedText(s,input);
//			for(ConnectivesClass cc : c){
//				System.out.println(cc.toString());
//			}
			
			for(ConnectivesClass connObject : c){
				String arg1 = connObject.getArg1();
				String arg2 = connObject.getArg2();
//				String conn = connObject.getConn();
				
				ArrayList<String> eventList1 = p.getEventList(arg1,input,"first");
				ArrayList<String> eventList2 = p.getEventList(arg2,input,"second");
				String relation = "hello";//p.getRelation(conn);
				String event2 = null;
				if(eventList2.size()>1){
					event2 = eventList2.get(0);
				}		
				
				if(event2!=null && relation!=null){
					for(String str : eventList1){
						System.out.println(str+","+relation+","+event2);
					}
				}
			}	
		}

}
