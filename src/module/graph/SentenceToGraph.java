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
package module.graph;
/**
 * This class implements an algorithm to create a semantic graph of the given English sentence.
 * The graph is returned in RDF style has triplets, i.e., <i>has(X,R,Y)</i>, where X is the start node,
 * Y is the end node and R is the semantic relation between them.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kparser.devel.PropbankData;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import module.SentenceToGraphInterface;
import module.graph.dependencymapping.MappingHandlerOutput;
import module.graph.helper.ClassesResource;
import module.graph.helper.GraphPassingNode;
import module.graph.helper.NEObject;
import module.graph.questions.PreprocessQuestions;
import module.graph.questions.ProcessedQuestion;
import module.graph.questions.QuestionType;
import module.graph.resources.CorefResolution;
import module.graph.resources.EventRelationsExtractor;
import module.graph.resources.InputDependencies;
import module.graph.resources.NamedEntityTagger;
import module.graph.resources.PrepositionSenseResolver;
import module.graph.resources.Preprocessor;
import module.graph.pdtbp.resources.PDTBResource;
import module.graph.resources.ResourceHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.stanford.nlp.trees.Tree;

/**
 * changelog: 07/23/2015
 * - line 357: slightly changed the way edge is added based off noticed pattern - works for all 
 *   example sentences + more
 *  
 *	-samrawal
 */

public class SentenceToGraph implements SentenceToGraphInterface{
	public ResourceHandler resourceHandler;

	//	private String questionSym = "q_";

	private Pattern p = Pattern.compile("([^,]*)(,)([^,]*)(,)(.*)");

	private HashMap<String,String> posMap = new HashMap<String,String>();
	private HashMap<String,String> eventOrderMap = new HashMap<String,String>();
	private HashMap<String,HashMap<String,String>> mapOfDependencies = new HashMap<String,HashMap<String,String>>();
	private ArrayList<String> finalList = new ArrayList<String>();
	private Map<String,Boolean> protoTypicalWords = new HashMap<String,Boolean>();
	private HashMap<String,ArrayList<String>> wordSenseMap = new HashMap<String,ArrayList<String>>();
	private ClassesResource conceptualClassResource = null;

	private HashMap<String, String> stringToNamedEntityMap = new HashMap<String, String>(4);
	private HashMap<String, String> pronominalCoreferenceMap = new HashMap<String, String>(4);

	//Map of Multi-Word Expressions
	private HashMap<String, String> mweMap = new HashMap<String, String>(4);

	private EventRelationsExtractor evRelExt = null;
	private PDTBResource pdtbInstance = null;
	private PreprocessQuestions preprocessQues = null;
	private Preprocessor preprocessorInstance = null;
	private NamedEntityTagger neTaggerInstance = null;

	private boolean backGroundFlag;
	private boolean wordSenseFlag;
	private boolean useCoreferenceResolution;

	/**
	 * This is the constructor for this class. It is used to initialize the instances
	 * of:
	 * 1. module.graph.resources.ResourceHandler class, which initialized various other 
	 * resources such as syntactic parser and weka classifier for Preposition Sense Disambiguation.
	 * 
	 * 2. module.graph.questions.PreprocessQuestions class, which initializes resources needed for
	 * parsing different types of questions.
	 * 
	 * 4. module.graph.resources.PDTBResource class, which initializes resources for
	 * Penn Discourse Tree Bank style discourse parsing. 
	 * 
	 * 3. module.graph.resources.EventRelationsExtractor class, which implements the algorithm to 
	 * find relations between event nodes in output graph. This is used if discourse parsing does not
	 * find any event-event relations.
	 */
	public SentenceToGraph(){
		resourceHandler = ResourceHandler.getInstance();
		preprocessorInstance = Preprocessor.getInstance();
		preprocessQues = PreprocessQuestions.getInstance();
		pdtbInstance = PDTBResource.getInstance();
		evRelExt = EventRelationsExtractor.getInstance();
		neTaggerInstance = NamedEntityTagger.getInstance();
	}

	/**
	 * This is the main class used to test the basic functionality of this class.
	 * @param args empty array of strings
	 */
	public static void main(String[] args) throws Exception{
		long startTime = System.currentTimeMillis();
		SentenceToGraph stg = new SentenceToGraph();
		//		String sentence1 = "Tom killed the boy.";
		//		String sentence = "Every boxer walks";
		//		String sentence2 = "John loves his wife.";
		//		sentence2 = "John asked Jim but Timmy replied.";
		//		sentence2 = "John and Tom went away.";
		//		sentence2 = "I'll walk to town unless it rains.";
		//		sentence2 = "Fish ate the worm because it was tasty.";
		//		sentence2 = "The older students were bullying the younger students so we rescued them and we killed them.";
		//		sentence2 = "The fish ate the worm because it was tasty.";
		//		sentence2 = "The boy wants to visit New York City.";
		//		sentence2 = "Do you like your new teacher?";
		//		sentence2 = "Who did you kill ?";
		//		sentence2 = "Is it true that he killed himself ?";
		//		sentence2 = "It is true that he killed himself .";
		//		sentence2 = "I loved that red dress .";
		//		sentence2 = "Didn't you eat all the apples ?";
		//		sentence2 = "You love her, don't you ?";
		//		sentence2 = "Who killed the dog ?";
		//		sentence2 = "Is my homework good ?";
		//		sentence2 = "Children were playing in the water."; 
		//		sentence2 = "John loves Mia.";
		//		sentence2 = "Bill had lots of time to read , while Jane just enjoyed being outside in the cooler weather";
		//		sentence2 = "John ran to his dog Rover.";
		//		sentence2 = "John loves his dog.";
		//		sentence2 = "John killed for money.";
		//		sentence2 = "The box was great in size.";
		//		sentence2 = "John came in car.";
		//		sentence2 = "The older students were bullying the younger students so we rescued them.";
		//		sentence2 = "From this point of view , Rusche and Kirchheimer_Punishment_and_Social_Structures , 1939 , ed .";
		//		sentence2 = "Who are you ?";
		//		sentence2 = "Did you eat all the apples ?";
		//		sentence2 = "The atmosphere was warm and friendly.";
		//		sentence2 = "John loved two dogs.";
		//		sentence2 = "ENTITY is your name .";
		//		sentence2 = "The older students were bullying the younger students so we rescued them and we killed them because he was a bad man .";
		//		sentence2 = "John loves Mia.";
		//		sentence2 = "John loves Mia because he loves girls and he hates boys .";
		//		sentence2 = "John came from Africa.";
		//		sentence2 = "We ' re having a picnic at the lakeshore .";
		//		sentence2 = "There is a tree with leaves in front of the house.";
		//		sentence2 = "Gavin ran away after he killed Tom.";
		//		sentence2 = "Add another blue stack of the same height.";
		//		sentence2 = "The older students were bullying the younger students so we rescued them.";
		//		sentence2 = "John loves his wife.";
		//		sentence2 = "Add another stack of the same height.";

		ArrayList<String> slist = new ArrayList<String>();
		//				slist.add("Leonard Vole is accused of murdering a rich woman after the World War II.");
		//				slist.add("John loves his wife because he likes wives.");
		//		slist.add("she'll go to school on Wednesday February 29 2016 16:30 PST");
		//				slist.add("I got 25 percent marks in the exam.");
		//				slist.add("John loves his wife but he hates her son.");
		//				slist.add("John came by car.");
		//		slist.add("I like that you love her.");
		//		slist.add("who founded nivea?");
		//		slist.add("John gave #20 to Tom.");
		//		slist.add("Whose bishop did Tom take?");
		//		slist.add("There is a tree with leaves in front of the house and to the left and behind the picnickers.");
		//		slist.add("In the mid-80s, wind turbines had a typical maximum power rating of 150 kW.");
		//		slist.add(" She went off the rails.");
		//		slist.add("The delivery truck zoomed by the school bus because it was going so fast.");
		//		slist.add("I think they are so tightly intertwined that they are virtually one and the same .");
		//		slist.add("Harry ate an apple.");
		//		slist.add("John loves his wife.");
		//		slist.add("Steve follows Fred's example in everything . He admires him hugely .");
//		slist.add("Jim yelled at Kevin becuse he was so upset.");
//		slist.add("Susan knows all about Ann's personal problems because she is nosy.");
//		slist.add("I poured water from the bottle into the cup until it was full.");
//		slist.add("Paul tried to call George on the phone, but he wasn't available.");
//		slist.add("Spanish authorities tried to stop the vote by arresting secessionist leaders and seizing ballots.");
//		slist.add("Steph curry fooled the entire pelicans defense to get draymond green a wide open three");
//		slist.add("Williams was reluctant to repeat what she had said to the official");
//		slist.add("John went to New York City.");
//		slist.add("John visited on Friday.");
//		slist.add("John gave 20$ to Tom.");
		slist.add("John loves his wife.");
		int count = 1;
		for(String sent : slist){
			if(count==1){
				startTime = System.currentTimeMillis();
			}
			GraphPassingNode gpn2 = stg.extractGraph(sent,false,true,false);
			ArrayList<String> list = gpn2.getAspGraph();
			for(String s : list){
				System.out.println(s);
			}
			System.out.println("*********************************");
			System.out.println(gpn2.getposMap());
			System.out.println(gpn2.getExtraStuff());
			count++;
		}

		//		String inputFile = "/home/arpit/workspace/WinogradPhd/dataForCompre/kparserEvalData.txt";
		//		try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
		//			String line = null;
		//			while((line=br.readLine())!=null){
		//				long sentStartTime = System.currentTimeMillis();
		//				GraphPassingNode gpn2 = stg.extractGraph(line,false,true,false);
		//				long sentEndTime = System.currentTimeMillis();
		//				System.out.println("Processing Time = "+(sentEndTime-sentStartTime));
		//				ArrayList<String> list = gpn2.getAspGraph();
		//				for(String s : list){
		//					System.out.println(s);
		//				}
		//			}
		//		}catch(IOException e){
		//			e.printStackTrace();
		//		}

		//		String ANSI_BLUE = "\u001B[34m";
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime));
		System.exit(0);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag) throws Exception{
		return this.extractGraph(sentence, bgFlag, wsFlag, false);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag, boolean useCoreference) throws Exception{
		return extractGraph(sentence,bgFlag,wsFlag,useCoreference,0);
	}

	/**
	 * This method is used to extract the graph of an input sentence.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 * @param index it is the number that will be added to the index of each word in the input sentence in case the background flag (bgflag) is true.
	 * @return GraphPassingNode it is an instance of module.graph.helper.GraphPassingNode
	 */
	public GraphPassingNode extractGraph(String sentence, boolean bgFlag, boolean wsFlag, boolean useCoreference, int index) throws Exception{
		wordSenseFlag = wsFlag;
		backGroundFlag = bgFlag;
		useCoreferenceResolution = useCoreference;

		finalList.clear();
		wordSenseMap.clear();
		pronominalCoreferenceMap.clear();

		if(sentence.equalsIgnoreCase("")){
			return null;
		}

		mweMap.clear();
		sentence = tagNamedEntities(sentence);
		//		System.out.println("NE Tagged Sentence:" + sentence);

		sentence = preprocessorInstance.preprocessText(sentence,resourceHandler,mweMap,stringToNamedEntityMap);
		//		System.out.println("Preprocessed Sentence:" + sentence);

		if((sentence.substring(sentence.length()-1).equalsIgnoreCase("?"))){
			extractFromQuestion(sentence, bgFlag, wsFlag, useCoreference,index);
		}else{
			extractFromSentence(sentence, bgFlag, wsFlag, useCoreference,index);
			HashSet<String> markedSet = new HashSet<String>();
			ArrayList<String> list = Lists.newArrayList();
			HashSet<String> usedAsAdj = new HashSet<String>();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);

					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")
							&& !markedSet.contains(s)){
						if(!rel.equalsIgnoreCase("instance_of")
								&&!rel.equalsIgnoreCase("is_subclass_of")
								&&!rel.equalsIgnoreCase("semantic_role")){
//							if(mweMap.containsKey(arg1)){ 
//								arg1 = mweMap.get(arg1)+"-"+arg1.substring(arg1.indexOf("-")+1);
//							}
//							if(mweMap.containsKey(arg2)){ 
//								arg2 = mweMap.get(arg2)+"-"+arg2.substring(arg2.indexOf("-")+1);
//							}

							if(arg1.contains("-") && arg2.contains("-")){
								String word1 = arg1.substring(0, arg1.lastIndexOf("-"));
								String indx1 = arg1.substring(arg1.lastIndexOf("-")+1);

//								System.out.println(arg2);
								String word2 = arg2.substring(0, arg2.lastIndexOf("-"));
								String indx2 = arg2.substring(arg2.lastIndexOf("-")+1);

								if(posMap.get(arg1).startsWith("V") && resourceHandler.verbUsedAsAdj(word1)){
									if(rel.equalsIgnoreCase("recipient") 
											|| (rel.equalsIgnoreCase("agent") 
													&& (word1.equalsIgnoreCase("gripped")
															|| word1.equalsIgnoreCase("ached")))){
										list.add("has("+arg1+"00"+indx1+",is_trait_of,"+arg2+").");
										posMap.put(arg1+"00"+indx1, "JJ");
										list.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
										if(!usedAsAdj.contains(arg1)){
											list.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
//											posMap.put(arg1+"00"+indx1, "JJ");
											usedAsAdj.add(arg1);
										}
										
									}else if(rel.equalsIgnoreCase("causes")){
										list.add("has("+arg1+"00"+indx1+","+rel+","+arg2+").");
										list.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
										posMap.put(arg1+"00"+indx1, "JJ");
										if(!usedAsAdj.contains(arg1)){
//											list.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
//											posMap.put(arg1+"00"+indx1, "JJ");
											usedAsAdj.add(arg1);
										}
									}
								}

								if(posMap.get(arg2).startsWith("V") && resourceHandler.verbUsedAsAdj(word2)){
									if(usedAsAdj.add(arg2) && rel.equalsIgnoreCase("causes")){
										list.add("has("+arg1+","+rel+","+arg2+"00"+indx2+").");
										posMap.put(arg2+"00"+indx2, "JJ");
									}
								}

							}

						}else{

						}
						list.add("has("+arg1+","+rel+","+arg2+").");//.replaceAll("-", "_")+").");//System.out.println("has("+s+").");
					}else{
						markedSet.add(arg2+",instance_of,"+arg2.substring(0, arg2.indexOf("-")));
					}
				}
			}
			finalList.clear();
			finalList.addAll(list);
		}

		if(finalList.size()==0){
			extractEntities();
		}
		
		HashMap<String,ArrayList<String>> mapOfList = new HashMap<String, ArrayList<String>>();
		Pattern pat = Pattern.compile("([\\S]+)( to )([\\S]+)");
		Matcher m = pat.matcher(sentence);
		while(m.find()){
			String arg1 = m.group(1);
			String arg2 = m.group(3);
			ArrayList<String> list = null;
			if(mapOfList.containsKey(arg1)){
				list = mapOfList.get(arg1);
			}else{
				list = new ArrayList<String>();
			}
			list.add(arg2);
			mapOfList.put(arg1, list);
		}
		
		GraphPassingNode result = new GraphPassingNode(finalList, posMap, sentence, wordSenseMap);
		result.setConClassRes(this.conceptualClassResource);
		result.setExtraStuff(mapOfList);
		
		return result;
	}

	/**
	 * This method is used to extract the graph of an input question after initial processing in other methods.
	 * The final graph in RDF style <i>has(X,R,Y)</i> triplets is saved in global array list. 
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 */
	private void extractFromQuestion(String question, boolean bgFlag, boolean wsFlag, boolean useCoreference, int index) throws Exception{
		Tree t = (Tree) resourceHandler.getSyntacticTree(question);

		ProcessedQuestion proQues = preprocessQues.preprocessQuestion(question, t);
		String sent = proQues.getProcessedText();
		
		extractFromSentence(sent, bgFlag, wsFlag, useCoreference, index);
		
		
		if(proQues.getType().equals(QuestionType.YES_NO)){
			ArrayList<String> newList = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					//					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						newList.add("has("+s+").");
						if(arg1.equalsIgnoreCase(proQues.getNextWord() + "-1")){
							if(posMap.containsKey(arg2)){
								if(posMap.get(arg2).startsWith("V")){
									newList.add("has("+arg2+",question,true-false).");
									posMap.put("true-false", "NN");
								}
							}
						}else if(arg2.equalsIgnoreCase(proQues.getNextWord() + "-" + String.valueOf(proQues.getNextWordPosition()))){
							if(posMap.containsKey(arg2)){
								if(posMap.get(arg1).startsWith("V")){
									newList.add("has("+arg1+",question,true-false).");
									posMap.put("true-false", "NN");
								}
							}
						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}else if(proQues.getType().equals(QuestionType.WH)){
			ArrayList<String> newList = Lists.newArrayList();
			HashSet<String> usedAsAdj = new HashSet<String>();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						if(!s.startsWith(proQues.getPlaceholder()+"-1,instance_of") 
								&& !s.startsWith(proQues.getPlaceholder()+",is_subclass_of,")
								&& !s.startsWith(proQues.getPlaceholder()+"-1,semantic_role")){
							if(arg1.equalsIgnoreCase(proQues.getPlaceholder()+"-1")){
								newList.add("has(?-1,"+rel+","+arg2+").");
								newList.add("has(?-1,instance_of,?).");
								String wordSenses = proQues.getWordSense();
								String[] sensArr = wordSenses.split(",");
								if(sensArr.length>1){
									for(String eachSens : sensArr){
										newList.add("has(?,is_subclass_of,"+eachSens+").");
									}
								}else{
									newList.add("has(?,is_subclass_of,"+wordSenses+").");
								}
								posMap.put("?-1", "NN");
								posMap.put("?", "NN");
							}else if(arg2.equalsIgnoreCase(proQues.getPlaceholder()+"-1")){
								newList.add("has("+arg1+","+rel+",?-1).");
								newList.add("has(?-1,instance_of,?).");
								String wordSenses = proQues.getWordSense();
								String[] sensArr = wordSenses.split(",");
								if(sensArr.length>1){
									for(String eachSens : sensArr){
										newList.add("has(?,is_subclass_of,"+eachSens+").");
									}
								}else{
									newList.add("has(?,is_subclass_of,"+wordSenses+").");
								}			
								posMap.put("?-1", "NN");
								posMap.put("?", "NN");
							}else{
								newList.add("has("+s+").");
							}
						}else if(s.startsWith(proQues.getPlaceholder()+"-1,semantic_role")){
							newList.add("has(?-1,"+rel+","+arg2+").");
						}
						
						if(arg1.contains("-") && arg2.contains("-")){
							String word1 = arg1.substring(0, arg1.lastIndexOf("-"));
							String indx1 = arg1.substring(arg1.lastIndexOf("-")+1);

//							System.out.println(arg2);
							String word2 = arg2.substring(0, arg2.lastIndexOf("-"));
							String indx2 = arg2.substring(arg2.lastIndexOf("-")+1);

							if(posMap.get(arg1).startsWith("V") && resourceHandler.verbUsedAsAdj(word1)){
								if(rel.equalsIgnoreCase("agent")
										|| (rel.equalsIgnoreCase("recipient") && (word1.equalsIgnoreCase("disappointed")||word1.equalsIgnoreCase("concerned")))){
									if(arg2.equalsIgnoreCase(proQues.getPlaceholder()+"-1")){
										newList.add("has("+arg1+"00"+indx1+",is_trait_of,?-1).");
										newList.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
										posMap.put(arg1+"00"+indx1, "JJ");
									}
									if(!usedAsAdj.contains(arg1)){
//										newList.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
//										posMap.put(arg1+"00"+indx1, "JJ");
										usedAsAdj.add(arg1);
									}
									
								}else if(rel.equalsIgnoreCase("causes")){
									newList.add("has("+arg1+"00"+indx1+","+rel+","+arg2+").");
									newList.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
									posMap.put(arg1+"00"+indx1, "JJ");
									if(!usedAsAdj.contains(arg1)){
//										newList.add("has("+arg1+"00"+indx1+",instance_of,"+word1+").");
//										posMap.put(arg1+"00"+indx1, "JJ");
										usedAsAdj.add(arg1);
									}
								}
							}

							if(posMap.get(arg2).startsWith("V") && resourceHandler.verbUsedAsAdj(word2)){
								if(usedAsAdj.add(arg2) && rel.equalsIgnoreCase("causes")){
									newList.add("has("+arg1+","+rel+","+arg2+"00"+indx2+").");
								}
							}

						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}else if(proQues.getType().equals(QuestionType.TAG)){
			String firstEvent = "";
			if(eventOrderMap.containsKey("e1")){
				eventOrderMap.get("e1");
			}
			ArrayList<String> newList = Lists.newArrayList();
			for(String s : finalList){
				Matcher m = p.matcher(s);
				if(m.find()){
					String arg1 = m.group(1);
					String rel = m.group(3);
					String arg2 = m.group(5);
					if(!arg2.startsWith("the-") && !arg2.startsWith("The-")
							&& !arg2.startsWith("an-") && !arg2.startsWith("An-")
							&& !arg2.startsWith("a-") && !arg2.startsWith("A-")){
						newList.add("has("+s+").");
						if(!rel.equalsIgnoreCase("instance_of") && !rel.equalsIgnoreCase("is_subclass_of")){
							if(arg1.equalsIgnoreCase(firstEvent)){
								newList.add("has("+arg1+",question,true-false).");
								posMap.put("true-false", "NN");
							}else if(arg2.equalsIgnoreCase(firstEvent)){
								newList.add("has("+arg2+",question,true-false).");
								posMap.put("true-false", "NN");
							}
						}
					}
				}
			}
			finalList.clear();
			finalList.addAll(newList);
		}
	}

	/**
	 * This method is used to extract the graph of an input sentence after initial processing in other methods.
	 * The final graph in RDF style <i>has(X,R,Y)</i> triplets is saved in global array list. 
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is the boolean variable used specify if you would like to add 100 to the index of each word in the input sentence.
	 * @param wsFlag it is the boolean variable used specify if you would like to perform word sense disambiguation on the words in the input sentence.
	 * @param useCoreference it is the boolean variable used specify if you would like to perform co-reference resolution on the entities in the input sentence.
	 */
	private void extractFromSentence(String inputText, boolean bgFlag, boolean wsFlag, boolean useCoreference,int index) throws Exception{	
		///Plugging in the NamedEntityTagger
		//		String inputText = tagNamedEntities(sentence);

		InputDependencies inDep = resourceHandler.getSyntacticDeps(inputText, this.backGroundFlag, index);
		if(inDep==null){
			return;
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		///////////////////////////// JUNE 13th 2017 (Addition of Phrasal Verbs) ///////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		HashMap<String,String> oldPosMap = inDep.getPosMap();

		HashMap<String,String> oldEventOrderMap = inDep.getEventOrderMap();
		HashMap<String,String> newEventsMap = inDep.getEventOrderMap();
		HashSet<String> origDrivrVrbs = new HashSet<String>();

		HashMap<String,String> mapOfOld2NewEvents = new HashMap<String, String>();

		HashMap<String,ArrayList<Integer>> mapOfIndices = new HashMap<String, ArrayList<Integer>>(); 

		for(String key : oldEventOrderMap.keySet()){
			ArrayList<Integer> listOfIndices = new ArrayList<Integer>();
			int lstWrdPrt1Indx = -999;
			int fstWrdPrt2Indx = -999;
			String event = oldEventOrderMap.get(key);
			String oldEvent = event;
			event = event.substring(0, event.lastIndexOf("-"));
			String baseForm = resourceHandler.getBaseFormOfWord(event,"v");
			PropbankData pbData = resourceHandler.getPrpbnkVrbGrp(baseForm);
			if(pbData!=null){
				ArrayList<String> verbs = pbData.getVerbs();
				if(verbs!=null && verbs.size()>0){
					for(String verb : verbs){
						verb = getVerbInCorrectForm(event,baseForm,verb);
						Pattern p = Pattern.compile("(.*)("+verb.replaceAll("_", " ")+")(.*)");
						Matcher m = p.matcher(inputText);
						if(m.matches()){
							String part1 = m.group(1);
							int part1Len = part1.length();
							if(part1.trim().length()==0 || part1.trim().length()<part1Len){
								lstWrdPrt1Indx = part1.split(" ").length-1;
								listOfIndices.add(lstWrdPrt1Indx);
							}
							String part2 = m.group(3);
							int part2Len = part2.length();
							if(part2Len==0 || part2.startsWith(" ")){
								fstWrdPrt2Indx = lstWrdPrt1Indx + verb.split(" ").length+1;
								listOfIndices.add(fstWrdPrt2Indx);
							}
							origDrivrVrbs.add(oldEvent);
							event = verb.replaceAll(" ", "_")+"-"+(lstWrdPrt1Indx+2);
							newEventsMap.put(key, event);
							mapOfOld2NewEvents.put(oldEvent, event);
							break;
						}

					}
				}			
			}
			if(lstWrdPrt1Indx!=-999 && fstWrdPrt2Indx!=-999){
				mapOfIndices.put(oldEvent, listOfIndices);
			}

		}

		HashMap<String,ArrayList<String>> oldMapOfDeps = inDep.getMapOfDeps();
		HashMap<String,ArrayList<String>> newMapOfDeps = new HashMap<String, ArrayList<String>>();
		HashMap<String,HashMap<String,String>> newMapOfDependencies = new HashMap<String, HashMap<String,String>>();

		for(String dep : oldMapOfDeps.keySet()){
			ArrayList<String> argsList = oldMapOfDeps.get(dep);
			ArrayList<String> newArgsList = new ArrayList<String>();
			HashMap<String,String> newMapOfArgs = new HashMap<String, String>();
			for(String argStr : argsList){

				boolean arg1Flag = false;
				String arg1 = argStr.split("#")[0];
				String newArg1 = null;
				int tmpIndx = Integer.parseInt(arg1.substring(arg1.lastIndexOf("-")+1));
				if(indxInRange(tmpIndx,mapOfIndices)){// > (lstWrdPrt1Indx+2) && tmpIndx < (fstWrdPrt2Indx+2)){
					if(origDrivrVrbs.contains(arg1)){//.substring(0, arg1.lastIndexOf("-"))
						newArg1 = arg1;
						arg1Flag = true;
					}
				}else{
					newArg1 = arg1;
				}

				boolean arg2Flag = false;
				String arg2 = argStr.split("#")[1];
				String newArg2 = null;
				tmpIndx = Integer.parseInt(arg2.substring(arg2.lastIndexOf("-")+1));
				if(indxInRange(tmpIndx,mapOfIndices)){// > (lstWrdPrt1Indx+2) && tmpIndx < (fstWrdPrt2Indx+2)){
					if(origDrivrVrbs.contains(arg2)){//.substring(0, arg2.lastIndexOf("-"))
						newArg2 = arg2;
						arg2Flag = true;
					}
				}else{
					newArg2 = arg2;
				}

				if(newArg1!=null && newArg2!=null){
					if(arg1Flag && !arg2Flag){
						newArgsList.add(mapOfOld2NewEvents.get(newArg1)+"#"+newArg2);
						newMapOfArgs.put(mapOfOld2NewEvents.get(newArg1), newArg2);
						oldPosMap.put(mapOfOld2NewEvents.get(newArg1), oldPosMap.get(newArg1));
					}else if(!arg1Flag && arg2Flag){
						newArgsList.add(newArg1+"#"+mapOfOld2NewEvents.get(newArg2));
						newMapOfArgs.put(newArg1, mapOfOld2NewEvents.get(newArg2));
						oldPosMap.put(mapOfOld2NewEvents.get(newArg2), oldPosMap.get(newArg2));
					}else if(arg1Flag && arg2Flag){
						newArgsList.add(mapOfOld2NewEvents.get(newArg1)+"#"+mapOfOld2NewEvents.get(newArg2));
						newMapOfArgs.put(mapOfOld2NewEvents.get(newArg1), mapOfOld2NewEvents.get(newArg2));
						oldPosMap.put(mapOfOld2NewEvents.get(newArg1), oldPosMap.get(newArg1));
						oldPosMap.put(mapOfOld2NewEvents.get(newArg2), oldPosMap.get(newArg2));
					}else{
						newArgsList.add(newArg1+"#"+newArg2);
						newMapOfArgs.put(newArg1, newArg2);
					}

				}
			}
			newMapOfDeps.put(dep, newArgsList);
			newMapOfDependencies.put(dep, newMapOfArgs);
		}

		//		String oldSent = inDep.getSentence();
		//		String[] words = oldSent.split(" ");
		//		String[] part1 = Arrays.copyOfRange(words, 0, lstWrdPrt1Indx);
		//		String[] verb
		//		String[] part2 = Arrays.copyOfRange(words, fstWrdPrt2Indx, words.length-1);
		//		System.out.println(lstWrdPrt1Indx);
		//		System.out.println(fstWrdPrt2Indx);

		inDep.setEventOrderMap(newEventsMap);
		inDep.setPosMap(oldPosMap);
		inDep.setMapOfDependencies(newMapOfDependencies);
		inDep.setMapOfDeps(newMapOfDeps);

		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////


		mapOfDependencies = newMapOfDependencies;//inDep.getMapOfDependencies();
		posMap = oldPosMap;//inDep.getPosMap();
		eventOrderMap = newEventsMap;//inDep.getEventOrderMap();


		if(this.wordSenseFlag){
			if(inputText.charAt(inputText.length()-1)=='.'){
				wordSenseMap = resourceHandler.getWordSenses(inputText.substring(0,inputText.length()-1),index);
			}else{
				wordSenseMap = resourceHandler.getWordSenses(inputText,index);
			}
		}else{
			wordSenseMap = new HashMap<String,ArrayList<String>>();
		}

		protoTypicalWords = extractPrototypicalWords();


		ClassesResource cr = extractClasses();
		HashMap<String,String> mapOfSuperClasses = cr.getSuperclassesMap();
		//		mapOfSuperClasses.put("Wednesday", "date");
		//		mapOfSuperClasses.put("6am", "time");
		//		posMap.put("Wednesday", "NN");
		//		posMap.put("6am", "NN");
		inDep.setMapOfSuperClasses(mapOfSuperClasses);

		this.conceptualClassResource = cr;

		/**		Code to use the Preposition Sense Disambiguation module to find correct
		 * 		semantic relations due to prepositions in the input text */
		//				HashSet<String> flaggedDeps = mappingOutput.getFlaggedDeps();
		//		HashSet<String> flaggedDeps = 
		classifyPrepositions(mapOfSuperClasses);
		//		inDep.setFlaggedDeps(flaggedDeps);
		//				divide(inputText);
		//////////////////////////////////////////////////////////////////////////////////

		MappingHandlerOutput mappingOutput = resourceHandler.getSemanticMapping(inDep);

		ArrayList<String> tempList =Lists.newArrayList(mappingOutput.getMappings()); 
		finalList.addAll(tempList);
		posMap.putAll(mappingOutput.getPosMap());

		
		
//		######### new on Feb 07 2019 ########
		HashMap<String,String> mapOfClasses = cr.getClassesMap();
		HashSet<String> instList = new HashSet<String>();
		HashSet<String> coveredWords = new HashSet<String>();
		for(String item : finalList){
			String[] itemArr = item.split(",");
			if(itemArr.length==3){
				String arg1 = itemArr[0];
				String rel = itemArr[1];
				String arg2 = itemArr[2];
				
				if(mweMap.containsKey(arg1)){ 
					arg1 = mweMap.get(arg1)+"-"+arg1.substring(arg1.indexOf("-")+1);
				}
				if(mweMap.containsKey(arg2)){ 
					arg2 = mweMap.get(arg2)+"-"+arg2.substring(arg2.indexOf("-")+1);
				}
				
				instList.add(arg1+","+rel+","+arg2);
				
				if(!coveredWords.contains(arg1)){
					String arg1Class = null;
					if(mapOfClasses.containsKey(arg1)){
						if(mapOfClasses.get(arg1)!=null){
							arg1Class = mapOfClasses.get(arg1);
							if(protoTypicalWords.containsKey(arg1)){
								if(protoTypicalWords.get(arg1)){
									instList.add(arg1+",prototype_of,"+arg1Class);
								}else{
									instList.add(arg1+",instance_of,"+arg1Class);
								}
							}else{
								instList.add(arg1+",instance_of,"+arg1Class);
							}
						}

						if(mapOfSuperClasses.containsKey(arg1)){
							if(arg1Class!=null){
								instList.add(arg1Class+",is_subclass_of,"+mapOfSuperClasses.get(arg1));
							}
						}
					}
					coveredWords.add(arg1);
				}
				
				if(!coveredWords.contains(arg2)){
					String arg2Class = null;
					if(mapOfClasses.containsKey(arg2)){
						if(mapOfClasses.get(arg2)!=null){
							arg2Class = mapOfClasses.get(arg2);
							if(protoTypicalWords.containsKey(arg2)){
								if(protoTypicalWords.get(arg2)){
									instList.add(arg2+",prototype_of,"+arg2Class);
								}else{
									instList.add(arg2+",instance_of,"+arg2Class);
								}
							}else{
								instList.add(arg2+",instance_of,"+arg2Class);
							}
						}

						if(mapOfSuperClasses.containsKey(arg2)){
							if(arg2Class!=null){
								instList.add(arg2Class+",is_subclass_of,"+mapOfSuperClasses.get(arg2));
							}
						}
					}
					coveredWords.add(arg2);
				}
			}
			
		}
		
		finalList.clear();		
		finalList.addAll(instList);
		
//		############################
		
//		////////////////////////////////
//		for(String s : cr.getClassesMap().keySet()){
//			if(s.length()>0){
//				int endIndex = s.length()-1;
//				if(s.contains("-")){
//					endIndex = s.indexOf("-");
//				}
//				if(!s.substring(0, endIndex).equalsIgnoreCase("a")
//						&& !s.substring(0, endIndex).equalsIgnoreCase("an")
//						&& !s.substring(0, endIndex).equalsIgnoreCase("the")){
//					if(mapOfSuperClasses.containsKey(s)){
//						if(protoTypicalWords.containsKey(s)){
//							if(protoTypicalWords.get(s)){
//								finalList.add(s+",prototype_of,"+cr.getClassesMap().get(s));
//							}else{
//								finalList.add(s+",instance_of,"+cr.getClassesMap().get(s));
//							}
//						}else{
//							finalList.add(s+",instance_of,"+cr.getClassesMap().get(s));
//						}
//						if(mapOfSuperClasses.get(s)!=null){
//							finalList.add(cr.getClassesMap().get(s)+",is_subclass_of,"+mapOfSuperClasses.get(s));
//						}
//					}else{
//						finalList.add(s+",instance_of,"+cr.getClassesMap().get(s));
//					}
//				}
//			}
//		}
//		/////////////////////////////////

		if(this.useCoreferenceResolution){
			pronominalCoreferenceMap.clear();
			CorefResolution corefInstance = CorefResolution.getInstance();
			pronominalCoreferenceMap = corefInstance.extractCoreferenceChain(inputText);
		}


		/**
		 * Code to call Penn Discourse Treebank style parser to find discourse connectives
		 * and map them to appropriate semantic relations
		 */
		//		************************************************************
		String depTree = "";
		for(String s : mapOfDependencies.keySet()){
			HashMap<String,String> temp = mapOfDependencies.get(s);
			for(String s1 : temp.keySet()){
				depTree += s + "(" + s1 +"," + temp.get(s1) + ")\n";
			}
		}

		//		depTree = depTree.substring(0,depTree.length()-1);
		depTree = "";

		Tree t = inDep.getPennTree();

		String pennTree = t.firstChild().toString();
		pennTree = pennTree.substring(1,pennTree.length()-1);
		pennTree = t.toString();

		pdtbInstance.setResHandler(resourceHandler);
		pdtbInstance.setPosMap(posMap);
		ArrayList<String> eventRelations = new ArrayList<String>();
		try{
			eventRelations = pdtbInstance.getEventRelations(inputText,pennTree,depTree,index);
		}catch(Exception e){
			System.err.println("Error in discourse parsing");
		}
		finalList.addAll(eventRelations);
		//		************************************************************

		if(eventRelations.size()==0){
			evRelExt.setResHandler(resourceHandler);
			finalList.addAll(evRelExt.divide(inputText, inDep));
		}

		//		************************************************************

		extractSemanticRoles();
	}

	/**
	 * Added 06/13/2017. It is a helper method in phrasal verbs detection
	 * @param original
	 * @param baseForm
	 * @param phrasal
	 * @return
	 */
	private String getVerbInCorrectForm(String original,String baseForm, String phrasal){
		String result = "";
		String[] tmp = phrasal.split("_");
		for(String part : tmp){
			if(part.equals(baseForm)){
				result += " "+original;
			}else{
				result += " " + part;
			}
		}
		return result.trim();
	}

	/**
	 * Added 06/13/2017. It is a helper method in phrasal verbs detection
	 * @param index
	 * @param mapOfIndices
	 * @return
	 */
	private boolean indxInRange(int index, HashMap<String,ArrayList<Integer>> mapOfIndices){
		for(String key : mapOfIndices.keySet()){
			ArrayList<Integer> listOfIndices = mapOfIndices.get(key);
			if(index > (listOfIndices.get(0)+1) && index < (listOfIndices.get(1)+1)){
				return true;
			}
		}
		return false;
	}

	/**
	 * This private method is used to tag the named entities in the input sentence/question.
	 * @param sentence it is the input sentence/question.
	 * @return java.lang.String it is the modified sentence in which the named entities with multiple words
	 * are combined into one word by using underscore (_). Also, a global map of named entities is updated.
	 */
	private String tagNamedEntities(String sentence) {
		stringToNamedEntityMap.clear();
		NEObject namedEntityTagger = neTaggerInstance.tagNamedEntities(sentence);

		// sentence will now have New York City as New_York_City
		sentence = namedEntityTagger.getModifiedText();
		// map will contain (New_York_City, LOCATION) as its class
		stringToNamedEntityMap = namedEntityTagger.getNamedEntityMap();

		return sentence;
	}

	/**
	 * This private method is used to find the semantic relations among words in the input sentence caused
	 * because of prepositions in the sentence. A classification based approach is used for this. 
	 * A separate trained classifier model is used for each preposition.
	 * @param mapOfSuperclasses it is a map of words in the input sentence and their conceptual classes.
	 * @return This method updates the global list of <i>has(X,R,Y)</i> triplets.
	 */
	private HashSet<String> classifyPrepositions(HashMap<String,String> mapOfSuperclasses){
		HashSet<String> flaggedDeps = Sets.newHashSet();
		Instances classifierInstStr =  PrepositionSenseResolver.getArffStructure();

		//		System.out.println(classifierInstStr.toString());
		for(String dep : mapOfDependencies.keySet()){
			//			String dependency = null;
			//			if(dep.startsWith("prep")){
			//				dependency = dep;
			//			}else if(dep.equalsIgnoreCase("agent")){
			//				dependency = "prep_by";
			//			}
			if(dep.startsWith("prep_")){
				String classes = resourceHandler.getPrepClasses(dep);
				if(classes!=null){
					FastVector attributes = new FastVector();
					if(classes.contains(",")){
						String[] temp = classes.split(",");
						for(String cls : temp){
							attributes.addElement(cls);
						}
					}else{
						attributes.addElement(classes);
					}

					// Create nominal attribute "position" 
					Attribute classAttr = new Attribute("class", attributes);

					classifierInstStr.insertAttributeAt(classAttr, 8);

					Classifier classifier = resourceHandler.getWekaClassifier(dep);
					if(classifier!=null){
						//				Classifier classifier = resourceHandler.getWekaClassifier(dependency);
						HashMap<String,String> tempMap = mapOfDependencies.get(dep);
						for(String word1 : tempMap.keySet()){
							String word2 = tempMap.get(word1);
							Instances unlabeled = new Instances(classifierInstStr);
							double[] vals;
							// 3. fill with data
							// first instance
							vals = new double[unlabeled.numAttributes()];
							// - nominal
							vals[0] = unlabeled.attribute(0).indexOfValue(getGeneralPOS(posMap.get(word1)));
							// - nominal
							vals[1] = unlabeled.attribute(1).indexOfValue(posMap.get(word1));
							// - nominal
							vals[2] = unlabeled.attribute(2).indexOfValue(getGeneralPOS(posMap.get(word2)));
							// - nominal
							vals[3] = unlabeled.attribute(3).indexOfValue(posMap.get(word2));
							// - nominal
							String superClass1 = mapOfSuperclasses.get(word1);
							if(superClass1==null){
								vals[4] = unlabeled.attribute(4).indexOfValue("all");
							}else{
								vals[4] = unlabeled.attribute(4).indexOfValue(superClass1);
							}
							// - nominal
							String superClass2 = mapOfSuperclasses.get(word2);
							if(superClass2==null){
								vals[5] = unlabeled.attribute(5).indexOfValue("all");
							}else{
								vals[5] = unlabeled.attribute(5).indexOfValue(superClass2);
							}

							unlabeled.add(new Instance(1.0, vals));

							unlabeled.instance(0).setValue(6, resourceHandler.getFeature7Value("*"+word1.substring(0,word1.lastIndexOf("-"))+"*"+word2.substring(0,word2.lastIndexOf("-"))+"*"));
							unlabeled.instance(0).setValue(7, resourceHandler.getFeature8Value(mapOfSuperclasses.get(word2)+":"+dep.substring(dep.indexOf("_")+1)));

							// set class attribute
							int classIndex = unlabeled.numAttributes()-1;
							unlabeled.setClassIndex(classIndex);
							try{
								double clsLabel = classifier.classifyInstance(unlabeled.instance(0));
								String label = unlabeled.attribute(classIndex).value((int)clsLabel);
								finalList.add(word1 +","+ label +","+ word2);
								flaggedDeps.add(dep+";"+word1+";"+word2);
							}catch(Exception e){
								System.err.println("Error in classifying the preposition sense.");
							}
						}
					}
					classifierInstStr.deleteAttributeAt(8);
				}
			}
		}
		return flaggedDeps;
	}

	/**
	 * This private method is used to get the general POS tags from specific ones.
	 * @param pos it is a specific POS tag. For example, <i>VBD</i>.
	 * @return java.lang.String it is the general POS tag. For example, <i>verb</i>
	 */
	private String getGeneralPOS(String pos){
		if(pos==null){
			return "useless";
		}

		if(pos.startsWith("V")){
			return "verb";
		}else if(pos.startsWith("N")){
			return "noun";
		}else if(pos.startsWith("R")){
			return "adverb";
		}else if(pos.startsWith("J")){
			return "adjective";
		}else{
			return "useless";
		}
	}

	/**
	 * This is a private method to mark the words in the sentence which are prototypes of their classes.
	 * @return HashMap<String,Boolean>
	 */
	private HashMap<String,Boolean> extractPrototypicalWords(){
		HashMap<String,Boolean> result = Maps.newHashMap();
		if(mapOfDependencies.containsKey("det")){
			HashMap<String,String> tempMap = mapOfDependencies.get("det");
			for(String key : tempMap.keySet()){
				String quant = tempMap.get(key).substring(0, tempMap.get(key).lastIndexOf('-')).trim();
				if("all".equalsIgnoreCase(quant)||
						"every".equalsIgnoreCase(quant)||
						"any".equalsIgnoreCase(quant)||
						"each".equalsIgnoreCase(quant)){
					result.put(key, true);
				}
			}
		}
		if(mapOfDependencies.containsKey("predet")){
			HashMap<String,String> tempMap = mapOfDependencies.get("predet");
			for(String key : tempMap.keySet()){
				String quant = tempMap.get(key).substring(0, tempMap.get(key).lastIndexOf('-')).trim();
				if("all".equalsIgnoreCase(quant)||
						"every".equalsIgnoreCase(quant)||
						"any".equalsIgnoreCase(quant)||
						"each".equalsIgnoreCase(quant)){
					result.put(key, true);
				}
			}
		}
		return result;
	}

	/**
	 * This method extracts the semantic roles of entities if there are any.
	 */
	private void extractSemanticRoles(){
		ArrayList<String> srlList = Lists.newArrayList();

		for(String str : finalList){
			Matcher m = p.matcher(str);
			if(m.find()){
				String tempVerb = m.group(1);
				if(tempVerb.contains("-")){
					String tmp1 = tempVerb.substring(0, tempVerb.lastIndexOf("-"));
					String tmp2 = tempVerb.substring(tempVerb.lastIndexOf("-")+1);
					if(pronominalCoreferenceMap.containsKey(tmp1+"_"+tmp2)){
						srlList.add(tempVerb+",has_coreferent,"+pronominalCoreferenceMap.get(tmp1+"_"+tmp2));
					}
					if(!m.group(3).equalsIgnoreCase("instance_of")){
						tempVerb = tempVerb.substring(0,tempVerb.lastIndexOf("-"));
						String role = null;
						if((role=resourceHandler.getSemanticRoleLabel(resourceHandler.getBaseFormOfWord(tempVerb, "v"), m.group(3)))!=null){
							srlList.add(m.group(5)+",semantic_role,"+":"+role);
						}
					}
				}
			}
		}
		finalList.addAll(srlList);
	}

	/**
	 * This method extracts entities from the input sentence in case 
	 * semantic graph is empty at the end. 
	 */
	private void extractEntities(){
		ArrayList<String> tempList = Lists.newArrayList();
		HashMap<String,String> tempPosMap = new HashMap<String, String>(posMap);
		for(String s : tempPosMap.keySet()){
			if(posMap.get(s).startsWith("N")||
					posMap.get(s).startsWith("P")||
					posMap.get(s).startsWith("CD")||
					posMap.get(s).startsWith("JJ")){
				String baseWord = resourceHandler.getBaseFormOfWord(s.split("-")[0],posMap.get(s.replaceAll("_", "-")).substring(0, 1));
				tempList.add("has("+s+",instance_of,"+baseWord+").");
				posMap.put(baseWord, posMap.get(s));
				if(stringToNamedEntityMap.containsKey(s.split("-")[0])){
					tempList.add("has("+baseWord+",is_subclass_of,"+stringToNamedEntityMap.get(s.split("-")[0]).toLowerCase()+").");
				}else{
					String supClass = getSuperclass(s, baseWord);
					if(supClass!=null){
						tempList.add("has("+baseWord+",is_subclass_of,"+supClass+").");
						posMap.put(supClass, posMap.get(s));
					}
				}
			}
		}

		finalList.addAll(tempList);
	}

	/**
	 * This method is used to extract conceptual classes of words in the input sentence.
	 * It used lemmatization, saved word senses from WordNet, and manually curated adjective classes.
	 * @return model.graph.helper.ClassesResource it is used to store maps of conceptual classes.
	 */
	private ClassesResource extractClasses(){
		Pattern pat = Pattern.compile("(.*)(-)([0-9]{1,5})");
		HashMap<String,String> mapOfClasses = Maps.newHashMap();
		HashMap<String,String> mapOfSuperClasses = Maps.newHashMap();
		for(String dep : mapOfDependencies.keySet()){
			HashMap<String,String> depTuple = mapOfDependencies.get(dep);
			for(String word1 : depTuple.keySet()){
				String word2 = depTuple.get(word1);

				if(mweMap.containsKey(word1)){
					String oldWord1 = word1;
					word1 = mweMap.get(word1) + oldWord1.substring(oldWord1.lastIndexOf("-"));
					posMap.put(word1, posMap.get(oldWord1));
				}

				if(mweMap.containsKey(word2)){
					String oldWord2 = word2;
					word2 = mweMap.get(word2) + oldWord2.substring(oldWord2.lastIndexOf("-"));
					posMap.put(word2, posMap.get(oldWord2));
				}

				String word1POS = posMap.get(word1);
				if(word1POS!=null){
					if(!word1POS.startsWith("CC")
							&& !word1POS.startsWith(",")
							&& !word1POS.startsWith(".")
							&& !word1POS.startsWith("IN")
							&& !word1POS.startsWith("TO")){

						/*
						 * Word1 Class extraction
						 */
						String wrd1 = word1;
						Matcher m = pat.matcher(word1);
						if(m.matches()){
							wrd1 = m.group(1);
						}
						
						String baseWord = wrd1;
						if(!word1POS.startsWith("JJ")){
							String[] tmpWord1Parts = wrd1.split("_");
							if(tmpWord1Parts.length==1){
								baseWord = resourceHandler.getBaseFormOfWord(wrd1,word1POS.substring(0, 1));
							}else{
								for(int i=0;i<tmpWord1Parts.length;i++){
									if(i==0){
										baseWord = resourceHandler.getBaseFormOfWord(tmpWord1Parts[i],word1POS.substring(0, 1));
									}else{
										baseWord+="_"+tmpWord1Parts[i];
									}
								}
							}
						}
						
						
						if(!mapOfClasses.containsKey(word1)){
							mapOfClasses.put(word1, baseWord);
							posMap.put(baseWord, word1POS);
						}
						
						

						/*
						 * Word1 Superclass extraction
						 */
						String superclass = getSuperclass(word1,baseWord);

						if(word1POS.equalsIgnoreCase("JJ")){
							String sup = resourceHandler.getAdjectiveClass(baseWord);
							if(sup!=null){
								superclass = sup;
							}else{
								sup = resourceHandler.getAdjectiveClass(word1.substring(0, word1.lastIndexOf("-")));
								if(sup!=null){
									superclass = sup;
								}
							}
						}else if(word1POS.equalsIgnoreCase("JJS")){
							superclass = "superlative";
						}else if(word1POS.equalsIgnoreCase("JJR")){
							superclass = "comparative";
						}

						if(!mapOfClasses.containsKey(baseWord)){
							mapOfClasses.put(baseWord,superclass);
						}
						if(!mapOfSuperClasses.containsKey(word2)){
							mapOfSuperClasses.put(word1, superclass);
						}
					}
				}

				/*
				 * Word2 Class extraction
				 */
				String word2POS = posMap.get(word2);
				if(word2POS!=null){
					if(!word2POS.startsWith("CC")
							&& !word2POS.startsWith(",")
							&& !word2POS.startsWith(".")
							&& !word2POS.startsWith("IN")
							&& !word2POS.startsWith("TO")){
						
						/*
						 * Word1 Class extraction
						 */
						String wrd2 = word2;
						Matcher m = pat.matcher(word2);
						if(m.matches()){
							wrd2 = m.group(1);
						}
						
						String baseWord = wrd2;
						if(!word2POS.startsWith("JJ")){
							String[] tmpWord2Parts = wrd2.split("_");
							if(tmpWord2Parts.length==1){
								baseWord = resourceHandler.getBaseFormOfWord(wrd2,word2POS.substring(0, 1));
							}else{
								for(int i=0;i<tmpWord2Parts.length;i++){
									if(i==0){
										baseWord = resourceHandler.getBaseFormOfWord(tmpWord2Parts[i],word2POS.substring(0, 1));
									}else{
										baseWord+="_"+tmpWord2Parts[i];
									}
								}
							}
						}
						
						if(!mapOfClasses.containsKey(word2)){
							mapOfClasses.put(word2, baseWord);
							posMap.put(baseWord, word2POS);
						}

						/*
						 * Word2 Superclass extraction
						 */
						String superclass = getSuperclass(word2,baseWord);

						if(word2POS.equalsIgnoreCase("JJ")){
							String sup = resourceHandler.getAdjectiveClass(baseWord);
							if(sup!=null){
								superclass = sup;
							}else{
								sup = resourceHandler.getAdjectiveClass(word2);
								if(sup!=null){
									superclass = sup;
								}
							}
						}else if(word2POS.equalsIgnoreCase("JJS")){
							superclass = "superlative";
						}else if(word2POS.equalsIgnoreCase("JJR")){
							superclass = "comparative";
						}

						if(!mapOfClasses.containsKey(baseWord)){
							mapOfClasses.put(baseWord,superclass);
						}
						if(!mapOfSuperClasses.containsKey(word2)){
							mapOfSuperClasses.put(word2, superclass);
						}
					}
				}
			}
		}

		ClassesResource classResource = new ClassesResource(mapOfClasses,mapOfSuperClasses);
		return classResource;

	}

	/**
	 * This method is used to extract super-classes of a word in the input sentence given the word and its base form.
	 * @param word it is the word in the input sentence. For example, <i>understood</i>.
	 * @param baseWord it is the base form of a word in the input sentence. For example, <i>understand</i>.
	 * @return superclass it is the conceptual superclass of the input word. For example, <i>cognition</i>.
	 */
	private String getSuperclass(String word, String baseWord){
		String superclass = null;
		String lexicalFileName =  wordSenseMap.get(word.toLowerCase().replaceAll("-", "_")) == null ? null : wordSenseMap.get(word.toLowerCase().replaceAll("-", "_")).get(1);
		String  entityType= stringToNamedEntityMap.get(baseWord);
		if(entityType != null) {
			entityType = entityType.toLowerCase();
			superclass = entityType.replace(neTaggerInstance.NAMED_MULTI_WORD_SEPARATOR," ");
			posMap.put(entityType.toLowerCase(), posMap.get(baseWord));
		}else if(lexicalFileName != null){
			if(lexicalFileName.contains(".")){
				lexicalFileName = lexicalFileName.substring(lexicalFileName.indexOf(".")+1,lexicalFileName.length());
			}
			if(resourceHandler.isAPronounRole(baseWord.toLowerCase())){
				String parent = resourceHandler.getPronounRole(baseWord.toLowerCase());
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord.replaceAll("_", "-")));
			}else{
				String parent = lexicalFileName;
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord));
			}
		}else{
			if(resourceHandler.isAPronounRole(baseWord.toLowerCase())){
				String parent = resourceHandler.getPronounRole(baseWord.toLowerCase());
				superclass = parent;
				posMap.put(parent, posMap.get(baseWord.replaceAll("_", "-")));
			}else{
			}
		}
		return superclass;
	}

}

