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
 * This class is used in semantic mapping of dependency relations to semantic relations.
 * This class is used to extract the semantic relation given an input syntactic dependency.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.stanford.nlp.trees.Tree;
import module.graph.resources.InputDependencies;

public class SemanticMappingHandler {

	// all mapping of extStanfordDepRelations.

	private MappingsReader mappingsReader = null;
	private HashMap<String,String> newPosMap = Maps.newHashMap();
	private HashSet<String> mappings = Sets.newHashSet();

	/**
	 * This is a constructor of this class. The global instance of module.graph.semanticmapping.MappingsReader 
	 * is initialized in this constructor.
	 */
	public SemanticMappingHandler(){
		mappingsReader = new MappingsReader();
	}

	/**
	 * This method is used to extract the semantic relation between words given an input syntactic dependencies.
	 * @param inDeps it is an instance of module.graph.resources.InputDependencies class.
	 * @return output it is an instance of module.graph.semanticmapping.MappingHandlerOutput class.
	 */
	@SuppressWarnings("unused")
	public MappingHandlerOutput getSemanticMappings(InputDependencies inDeps){
		newPosMap.clear();
		mappings.clear();

		HashMap<String,HashMap<String,String>> mapOfDependencies = inDeps.getMapOfDependencies();
		HashMap<String,String> posMap = inDeps.getPosMap();
		HashMap<String,String> mapOfClasses = inDeps.getMapOfSuperClasses();
		HashMap<String,ArrayList<String>> mapOfDeps = inDeps.getMapOfDeps();
		HashSet<String> inputFlaggedeps = inDeps.getFlaggedDeps();

		HashSet<SyntacticDependency> flaggedDeps = Sets.newHashSet();

		for(String dep : mapOfDeps.keySet()){
			ArrayList<String> argsList = mapOfDeps.get(dep);
			for(String arguments : argsList){
				String[] argsTuple = arguments.split("#");
				String arg1 = argsTuple[0];
				String arg2 = argsTuple[1];
				if(!inputFlaggedeps.contains(dep+";"+arg1+";"+arg2)){
					//		for(String dep : mapOfDependencies.keySet()){
					//			for(String arg1 : mapOfDependencies.get(dep).keySet()){
					//				String arg2 = mapOfDependencies.get(dep).get(arg1);
					SyntacticDependency dependency = new SyntacticDependency();
					dependency.setDep(dep);
					NodeProperties startNode = new NodeProperties(arg1 + "," + posMap.get(arg1));
					if(mapOfClasses.containsKey(arg1)){
						startNode.setNodeSuperclass(mapOfClasses.get(arg1));
					}
					dependency.setStartNode(startNode);
					NodeProperties endNode = new NodeProperties(arg2 + "," + posMap.get(arg2));
					if(mapOfClasses.containsKey(arg2)){
						endNode.setNodeSuperclass(mapOfClasses.get(arg2));
					}
					dependency.setEndNode(endNode);

					HashMap<MappingRuleBody,LinkedList<MappingLabel>> labelMap = mappingsReader.getDepLabelMap();

					boolean badDepFlag = true;
					LinkedList<SyntacticDependency> badDepsList = null;

					for(MappingRuleBody body : labelMap.keySet()){
						HashMap<String,NodeProperties> mapOfNodes = Maps.newHashMap();
						if (body.getDependencyRule().size()==0){
							continue;
						}
						LinkedList<SyntacticDependency> depRuleList = Lists.newLinkedList(body.getDependencyRule());
						badDepsList = Lists.newLinkedList();
						badDepsList.add(dependency);

						SyntacticDependency sd = depRuleList.remove(0);

						if(sd.equals(dependency)){
							if(depRuleList.size()==0){
								badDepFlag = false;
								badDepsList.remove(0);
							}
							if(!mapOfNodes.containsKey(sd.getStartNode().getNodeId())){
								mapOfNodes.put(sd.getStartNode().getNodeId(),new NodeProperties(dependency.getStartNode()));
							}
							if(!mapOfNodes.containsKey(sd.getEndNode().getNodeId())){
								mapOfNodes.put(sd.getEndNode().getNodeId(),new NodeProperties(dependency.getEndNode()));
							}

							boolean satFlag = true;
							while(depRuleList.size()>0){
								boolean continueFlag = false;

								SyntacticDependency s = depRuleList.remove(0);
								if(mapOfDeps.containsKey(s.getDep())){
									ArrayList<String> depList = mapOfDeps.get(s.getDep());
									for(String args : depList){
										String[] argsArr = args.split("#");
										SyntacticDependency synDep = new SyntacticDependency();
										synDep.setDep(s.getDep());
										NodeProperties startNode1 = new NodeProperties();
										startNode1.setNodeId(s.getStartNode().getNodeValue());
										startNode1.setNodePOS(posMap.get(argsArr[0]));
										startNode1.setNodeValue(argsArr[0]);
										synDep.setStartNode(startNode1);

										NodeProperties endNode1 = new NodeProperties();
										endNode1.setNodeId(s.getEndNode().getNodeValue());
										endNode1.setNodePOS(posMap.get(argsArr[1]));
										endNode1.setNodeValue(argsArr[1]);
										synDep.setEndNode(endNode1);

										badDepsList.add(synDep);
										if(s.equals(synDep)){
											if(mapOfNodes.containsKey(s.getStartNode().getNodeId())){
												NodeProperties nodeStrt = mapOfNodes.get(s.getStartNode().getNodeId());
												if(nodeStrt.getNodeValue().equalsIgnoreCase(synDep.getStartNode().getNodeValue())){
													if(mapOfNodes.containsKey(s.getEndNode().getNodeId())){
														NodeProperties nodeEnd = mapOfNodes.get(s.getEndNode().getNodeId());
														if(nodeEnd.getNodeValue().equalsIgnoreCase(synDep.getEndNode().getNodeValue())){
															if(depRuleList.size()==0){
																badDepFlag = false;
																badDepsList.clear();
															}
															if(!mapOfNodes.containsKey(s.getStartNode().getNodeId())){
																mapOfNodes.put(s.getStartNode().getNodeId(),startNode1);
															}
															if(!mapOfNodes.containsKey(s.getEndNode().getNodeId())){
																mapOfNodes.put(s.getEndNode().getNodeId(),endNode1);
															}
															continueFlag = true;
														}
													}else{
														if(depRuleList.size()==0){
															badDepFlag = false;
															badDepsList.clear();
														}
														if(!mapOfNodes.containsKey(s.getStartNode().getNodeId())){
															mapOfNodes.put(s.getStartNode().getNodeId(),startNode1);
														}
														if(!mapOfNodes.containsKey(s.getEndNode().getNodeId())){
															mapOfNodes.put(s.getEndNode().getNodeId(),endNode1);
														}
														continueFlag = true;
													}
												}
											}else{
												if(mapOfNodes.containsKey(s.getEndNode().getNodeId())){
													NodeProperties nodeEnd = mapOfNodes.get(s.getEndNode().getNodeId());
													if(nodeEnd.getNodeValue().equalsIgnoreCase(synDep.getEndNode().getNodeValue())){
														if(depRuleList.size()==0){
															badDepFlag = false;
															badDepsList.clear();
														}
														if(!mapOfNodes.containsKey(s.getStartNode().getNodeId())){
															mapOfNodes.put(s.getStartNode().getNodeId(),startNode1);
														}
														if(!mapOfNodes.containsKey(s.getEndNode().getNodeId())){
															mapOfNodes.put(s.getEndNode().getNodeId(),endNode1);
														}
														continueFlag = true;
													}
												}else{
													if(depRuleList.size()==0){
														badDepFlag = false;
														badDepsList.clear();
													}
													if(!mapOfNodes.containsKey(s.getStartNode().getNodeId())){
														mapOfNodes.put(s.getStartNode().getNodeId(),startNode1);
													}
													if(!mapOfNodes.containsKey(s.getEndNode().getNodeId())){
														mapOfNodes.put(s.getEndNode().getNodeId(),endNode1);
													}
													continueFlag = true;
												}
											}
										}
									}
								}

								if(!continueFlag){
									satFlag = false;
									break;
								}
							}

							if(satFlag){
								LinkedList<MappingLabel> labels = Lists.newLinkedList(labelMap.get(body));
								for(MappingLabel label : labels){
									if(label!=null){
										MappingLabel newLabel = new MappingLabel();
										String startNodeId = label.getStartNode().getNodeValue();
										String startNodeSuffix = label.getStartNode().getNodeSuffix();
										String endNodeId = label.getEndNode().getNodeValue();
										String endNodeSuffix = label.getEndNode().getNodeSuffix();
										if(mapOfNodes.containsKey(startNodeId)){
											NodeProperties start = mapOfNodes.get(startNodeId);
											start.setNodeId(startNodeId);
											start.setNodeSuffix(startNodeSuffix);
											newLabel.setStartNode(start);
										}
										if(mapOfNodes.containsKey(endNodeId)){
											NodeProperties end = mapOfNodes.get(endNodeId);
											end.setNodeId(endNodeId);
											end.setNodeSuffix(endNodeSuffix);
											newLabel.setEndNode(end);
										}
										if(!newLabel.getStartNode().getNodeSuffix().equalsIgnoreCase("X")
												&& !newLabel.getStartNode().getNodeSuffix().equalsIgnoreCase("X")){
											String startNodeValue = newLabel.getStartNode().getNodeValue();
											String endNodeValue = newLabel.getEndNode().getNodeValue();
											mappings.add(startNodeValue+"_"+startNodeSuffix+","+label.getLabel()+","+endNodeValue+"_"+endNodeSuffix);
											newPosMap.put(startNodeValue+"_"+startNodeSuffix, "NN");
											newPosMap.put(endNodeValue+"_"+endNodeSuffix, "NN");
										}else if(!newLabel.getStartNode().getNodeSuffix().equalsIgnoreCase("X")){
											String startNodeValue = newLabel.getStartNode().getNodeValue();
											String endNodeValue = newLabel.getEndNode().getNodeValue();
											mappings.add(startNodeValue+"_"+startNodeSuffix+","+label.getLabel()+","+endNodeValue);
											newPosMap.put(startNodeValue+"_"+startNodeSuffix, "NN");
										}else if(!newLabel.getStartNode().getNodeSuffix().equalsIgnoreCase("X")){
											String startNodeValue = newLabel.getStartNode().getNodeValue();
											String endNodeValue = newLabel.getEndNode().getNodeValue();
											mappings.add(startNodeValue+","+label.getLabel()+","+endNodeValue+"_"+endNodeSuffix);
											newPosMap.put(endNodeValue+"_"+endNodeSuffix, "NN");
										}else{
											mappings.add(newLabel.getStartNode().getNodeValue()+","+label.getLabel()+","+newLabel.getEndNode().getNodeValue());
										}
									}
								}
								break;
							}
						}
						//					else{
						//						flaggedDeps.add(dependency);
						//					}
					}
					if(badDepFlag){
						for(SyntacticDependency flDep : badDepsList){
							flaggedDeps.add(flDep);
						}
					}
				}
			}
		}
		MappingHandlerOutput output = new MappingHandlerOutput();
		output.setMappings(mappings);
		output.setPosMap(newPosMap);
		output.setFlaggedDeps(flaggedDeps);
		return output;
	}
	
	public static void main(String[] args){
//		SemanticMappingHandler smh = new SemanticMappingHandler();
		InputDependencies indeps = new InputDependencies();
		
		String sent = "The light went_out .";
		indeps.setSentence(sent);
		
		HashMap<String,String> eventsOrderMap = new HashMap<String, String>();
		eventsOrderMap.put("e1", "went_out-3");
		indeps.setEventOrderMap(eventsOrderMap);
		
		HashMap<String,ArrayList<String>> mapOfDeps = new HashMap<String, ArrayList<String>>();
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.add("light-2#The-1");
		mapOfDeps.put("det", tmp);
		ArrayList<String> tmp1 = new ArrayList<String>();
		tmp1.add("went_out-3#light-2");
		mapOfDeps.put("nsubj", tmp1);
		indeps.setMapOfDeps(mapOfDeps);
		
		HashMap<String,String> mapOfSuperclases = new HashMap<String, String>();
		mapOfSuperclases.put("light-2", "phenomenon");
		mapOfSuperclases.put("The-1", null);
//		mapOfSuperclases.put("out-4", null);
		mapOfSuperclases.put("went_out-3", "motion");
		indeps.setMapOfSuperClasses(mapOfSuperclases);
		
		Tree t = Tree.valueOf("(ROOT (S (NP (DT The) (NN light)) (VP (VBD went) (PRT (RP out))) (. .)))");
		indeps.setPennTree(t);
		
		HashMap<String,String> posMap = new HashMap<String, String>();
		posMap.put("light-2", "NN");
		posMap.put("The-1", "DT");
		posMap.put("motion", "VBD");
		posMap.put("went_out-3", "VBD");
		posMap.put("light", "NN");
		posMap.put("The", "DT");
		posMap.put("go_out", "VBD");
		posMap.put(".-5", ".");
		posMap.put("phenomenon", "NN");
		indeps.setPosMap(posMap);
		
		
//		MappingHandlerOutput mho = smh.getSemanticMappings(indeps);
		
		System.exit(0);
		
	}
}
