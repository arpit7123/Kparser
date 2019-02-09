package module.graph.qasrl;

import java.util.ArrayList;
import java.util.HashMap;

import module.graph.helper.Node;
import module.graph.helper.NodePassedToViewer;

public class Graph2Qasrl {
	
	
	public ArrayList<VerbQA> getQASRLOutput(ArrayList<NodePassedToViewer> listOfGraphs){
		ArrayList<VerbQA> result = new ArrayList<VerbQA>();
		
		for(NodePassedToViewer npv : listOfGraphs){
			Node root = npv.getGraphNode();
			traverseGraph(root, result);
		}
		
		
		return result;
	}

	private void traverseGraph(Node graphNode, ArrayList<VerbQA> listOfVerbQAs){
		ArrayList<Node> children = graphNode.getChildren();
		ArrayList<String> edges = graphNode.getEdgeList();
		if(graphNode.isAnEvent()){
			String verb = graphNode.getValue();
			verb = verb.substring(0, verb.lastIndexOf("-"));
			VerbQA vqa = new VerbQA();
			vqa.setVerb(verb);
			HashMap<String,String> qaMap = new HashMap<String,String>();
			
			for(int i=0;i<edges.size();++i){
				String childVal = children.get(i).getValue();
				if(childVal.matches("(.*)(-)([0-9]{1,7})")){
					childVal = childVal.substring(0, childVal.lastIndexOf("-"));
				}
				if(edges.get(i).equalsIgnoreCase("agent")){
					String otherPart = getOtherPart(i,children);
					String pref = null;
					if(children.get(i).getSuperClass().equalsIgnoreCase("person")){
						pref = "Who";
					}else{
						pref = "What";
					}
					String ques = pref + " " + verb + " " +otherPart +"?";
					String ans = childVal;
					qaMap.put(ques, ans);
				}
				
				if(edges.get(i).equalsIgnoreCase("recipient")
						|| edges.get(i).equalsIgnoreCase("beneficiary")
						|| edges.get(i).equalsIgnoreCase("attachment")){
					
					String otherPart = getOtherPart(i,children);
					String pref = null;
					if(children.get(i).getSuperClass().equalsIgnoreCase("person")){
						pref = "Who";
					}else{
						pref = "What";
					}
					
					if(graphNode.getPOS().equalsIgnoreCase("vbd") || graphNode.getPOS().equalsIgnoreCase("vbn")){
						pref += " did";
					}else{
						pref += " does";
					}
					
					String ques = pref + " " + otherPart + " " + verb +"?";
					String ans = childVal;
					qaMap.put(ques, ans);
				}
				
				if(edges.get(i).equalsIgnoreCase("time")
						|| edges.get(i).equalsIgnoreCase("time_at")){
					
					String otherPart = getOtherPart(i,children);
					String pref = "When";
					
					if(graphNode.getPOS().equalsIgnoreCase("vbd") || graphNode.getPOS().equalsIgnoreCase("vbn")){
						pref += " did";
					}else{
						pref += " does";
					}
					
					String ques = pref + " " + otherPart + " " + verb +"?";
					String ans = childVal;
					qaMap.put(ques, ans);
				}
				
				if(edges.get(i).equalsIgnoreCase("object")
						|| edges.get(i).equalsIgnoreCase("raw_material")){
					
					String otherPart = getOtherPart(i,children);
					String pref = "What";
					
					if(graphNode.getPOS().equalsIgnoreCase("vbd") || graphNode.getPOS().equalsIgnoreCase("vbn")){
						pref += " did";
					}else{
						pref += " does";
					}
					
					String ques = pref + " " + otherPart + " " + verb +"?";
					String ans = childVal;
					qaMap.put(ques, ans);
				}
				
				if(edges.get(i).equalsIgnoreCase("destination") 
						|| edges.get(i).equalsIgnoreCase("location") 
						|| edges.get(i).equalsIgnoreCase("site")
						|| edges.get(i).equalsIgnoreCase("origin")
						|| edges.get(i).equalsIgnoreCase("target")){
					
					String otherPart = getOtherPart(i,children);
					String pref = "Where";
					
					if(graphNode.getPOS().equalsIgnoreCase("vbd") || graphNode.getPOS().equalsIgnoreCase("vbn")){
						pref += " did";
					}else{
						pref += " does";
					}
					
					String ques = pref + " " + otherPart + " " + verb +"?";
					String ans = childVal;
					qaMap.put(ques, ans);
				}
				
			}
			vqa.setQaMap(qaMap);
			listOfVerbQAs.add(vqa);
		}
		for(Node child : children){
			traverseGraph(child, listOfVerbQAs);
		}
	}
	
	private String getOtherPart(int i, ArrayList<Node> children){
		String result = "";
		
		for(int j=0;j<children.size();++j){
			if(j!=i){
				Node child = children.get(j);
				if(child.isAnEntity()){
					if(child.getSuperClass().equalsIgnoreCase("person")){
						result = "someone";
					}else if(child.getSuperClass().equalsIgnoreCase("location")){
						result = "somewhere";
					}else{
						result = "something";
					}
					break;
				}
			}
		}
		
		return result;
	}

	public static void main(String[] args) {
		String s = "hello";
		System.out.println(s.substring(0,s.lastIndexOf("-")));
	}

}
