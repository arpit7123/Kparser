package module.graph.qasrl;

import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class VerbQA {
	
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String verb = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> qaMap = null;
	
	
	@Override
	public String toString(){
		String result = null;
		
		if(verb!=null){
			result = verb;
			for(String key : qaMap.keySet()){
				result += "\n Q: " + key + "\t A: " + qaMap.get(key);
			}
		}
		
		return result;
	}
}
