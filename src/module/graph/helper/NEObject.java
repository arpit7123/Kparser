/**
 * 
 */
package module.graph.helper;

import java.util.HashMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arpit Sharma
 *
 */
public class NEObject {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> namedEntityMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String modifiedText = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<Substring,String> entityIndxMap = null;
	
	public NEObject(HashMap<String,String> namedEntityMap, String modifiedText){
		this.namedEntityMap = namedEntityMap;
		this.modifiedText = modifiedText;
	}
	
	public NEObject(){}
	
	@Override
	public String toString(){
		return this.modifiedText + "\n" + this.namedEntityMap.toString();
	}
	
}
