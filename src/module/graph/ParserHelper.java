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
 * This class implements an algorithm to extract the semantic graph of an input sentence in JSON format.
 * @author Arpit Sharma
 * @since 01/15/2015
 */

import java.util.ArrayList;
import java.util.List;

import module.graph.helper.Node;
import module.graph.helper.NodePassedToViewer;
import module.graph.qasrl.VerbQA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ParserHelper {
	private MakeGraph make = null;
	
	/**
	 * It is the constructor that initializes all the global variables of this class.
	 */
	public ParserHelper(){
		make = new MakeGraph();
	}

	/**
	 * This is the main method used to test the basic functionality of this class.
	 * @param args empty array of strings
	 */
	public static void main(String[] args){
		String sentence = "john gave #20 to tom .";
		sentence = "John loves his wife.";
		ParserHelper ph = new ParserHelper();
		System.out.println(ph.getJsonString(sentence, false));
	}
	
	/**
	 * This method is used to get the semantic graph of an input sentence in pretty JSON format.
	 * @param sent input sentence
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done.
	 * @return result it is the output semantic graph in pretty JSON format.
	 */
	public String getPrettyJsonString(String sent, boolean useCoreference) throws Exception{
		String result = null;
		if(sent.equalsIgnoreCase("")){
			return null;
		}
		String sentence = sent;
		JSONObject ja = null;
		try{
			ja = parse(sentence, useCoreference);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(ja.toString());
			String prettyJsonString = gson.toJson(je);
			result = prettyJsonString;
		}catch(JSONException e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * This method is used to get the semantic graph of an input sentence in pretty JSON format.
	 * @param sent input sentence
	 * @param bgFlag it is a flag used to specify if a number should be added to the index of each word.
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done.
	 * @param index it is the number that will be added to the index of each word in the input sentence. It will be used if bgFlag is true.
	 * @return result it is the output semantic graph in pretty JSON format.
	 */
	public String getPrettyJsonString(String sent, boolean bgFlag, boolean useCoreference, int index) throws Exception{
		String result = null;
		if(sent.equalsIgnoreCase("")){
			return null;
		}
		String sentence = sent;
		JSONArray ja = null;
		try{
			ja = parse(sentence,bgFlag,useCoreference,index);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(ja.toString());
			String prettyJsonString = gson.toJson(je);
			result = prettyJsonString;
		}catch(JSONException e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method is used to get the semantic graph of an input sentence as JSON string.
	 * @param sent input sentence.
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done.
	 * @return outputJSONString it is the output JSON semantic graph of the input sentence.
	 */
	public String getJsonString(String sent, boolean useCoreference){
		String outputJSONString = null;
		if(sent.equalsIgnoreCase("")){
			return null;
		}
		String sentence = sent;
		JSONObject ja = null;
		try{
			ja = parse(sentence, useCoreference);
		}catch(Exception e){
			e.printStackTrace();
		}
		outputJSONString = ja.toString();
		return outputJSONString;
	}
	
	/**
	 * This method is used to get the semantic graph of an input sentence as JSON string.
	 * @param sent input sentence.
	 * @param bgFlag it is a flag used to specify if a number should be added to the index of each word.
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done.
	 * @param index it is the number that will be added to the index of each word in the input sentence. It will be used if bgFlag is true.
	 * @return outputJSONString it is the output JSON semantic graph of the input sentence.
	 */
	public String getJsonString(String sent, boolean bgFlag, boolean useCoreference, int index){
		String outputJSONString = null;
		if(sent.equalsIgnoreCase("")){
			return null;
		}
		String sentence = sent;
		JSONArray ja = null;
		try{
			ja = parse(sentence,bgFlag,useCoreference,index);
		}catch(Exception e){
			e.printStackTrace();
		}
		outputJSONString = ja.toString();
		return outputJSONString;
	}
	
	/**
	 * This method takes a sentence and a co-reference resolution flag as input and returns an array of JSON objects as output.
	 * @param sentence it is the input sentence.
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done. 
	 * @return jsonArray it is an array of JSON objects (semantic graph(s))
	 * @throws JSONException
	 */
	public JSONObject parse(String sentence, boolean useCoreference) throws Exception {
		JSONObject result = new JSONObject();
		ArrayList<NodePassedToViewer> list = make.createGraphUsingSentence(sentence,false,true, useCoreference);
		
		ArrayList<VerbQA> vqa = make.graphToQasrl(list);
		JSONArray vqaObj = getJSONObj(vqa);
		
		JSONArray jsonArray = new JSONArray();
		for (NodePassedToViewer upperNode : list) {
			Node node = upperNode.getGraphNode();
			jsonArray.put(toJSON(node,null));
		}
		result.put("parser_output", jsonArray);
		result.put("vqa_data", vqaObj);
		
		return result;
	}
	
	/**
	 * This method returns an array of JSON objects as output.
	 * @param sentence it is the input sentence.
	 * @param bgFlag it is a flag used to specify if a number should be added to the index of each word.
	 * @param useCoreference it is a flag used to specify if co-reference resolution should be done. 
	 * @param index it is the number that will be added to the index of each word in the input sentence. It will be used if bgFlag is true.
	 * @return jsonArray it is an array of JSON objects (semantic graph(s))
	 * @throws JSONException
	 */
	public JSONArray parse(String sentence, boolean bgFlag, boolean useCoreference, int index) throws Exception {
		JSONArray jsonArray = new JSONArray();
		ArrayList<NodePassedToViewer> list = make.createGraphUsingSentence(sentence,bgFlag,true,useCoreference,index);
		for (NodePassedToViewer upperNode : list) {
			Node node = upperNode.getGraphNode();
			jsonArray.put(toJSON(node,null));
		}
		return jsonArray;
	}

	/**
	 * This method is used to recursively convert the semantic graph into a JSON object.
	 * @param node it is a node in the semantic graph.
	 * @param edge it is the edge label ending in node.
	 * @return outputJSONObj it is the output JSON object for the semantic graph of the input sentence.
	 * @throws JSONException
	 */
	private JSONObject toJSON(Node node, String edge) throws JSONException {
		JSONObject outputJSONObj = null;
		List<JSONObject> children = Lists.newArrayList();
		ArrayList<Node> nodeList = node.getChildren();
		for (int i=0;i<nodeList.size();++i){
			Node child = nodeList.get(i);
			children.add(toJSON(child,node.getEdgeList().get(i)));
		}

		JSONObject obj = new JSONObject();
		obj.put("word", node.getValue());
		obj.put("pos", node.getPOS());
		obj.put("isClass", node.isAClass());
		obj.put("isEvent", node.isAnEvent());
		obj.put("isEntity", node.isAnEntity());
		obj.put("wordSense", node.getWordSense());
		obj.put("Edge", edge);
		obj.put("isASemanticRole", node.isASemanticRole());
		obj.put("isACoreferent", node.isACoreferent());
		
		outputJSONObj = new JSONObject().put("data", obj).put("children", new JSONArray(children));
		return outputJSONObj;
	}
	
	
	private JSONArray getJSONObj(ArrayList<VerbQA> vqaList) throws JSONException{
		JSONArray result = new JSONArray();
		for(VerbQA vqa : vqaList){
			JSONObject jObj = new JSONObject();
			jObj.put("verb", vqa.getVerb());
			jObj.put("qas", vqa.getQaMap());
			result.put(jObj);
		}
		return result;
	}
}
