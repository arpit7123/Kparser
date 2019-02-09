/**
 * 
 */
package module.graph.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arpit Sharma
 *
 */
public class Substring {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String value;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private int startIndx;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private int endIndx;
	
	public Substring(){
	}
	
	public Substring(String value, int startIndx, int endIndx){
		this.value = value;
		this.startIndx = startIndx;
		this.endIndx = endIndx;
	}
}
