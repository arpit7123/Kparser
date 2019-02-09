/**
 * 
 */
package module.graph.pdtbp.utils;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */
public class ConCompOutput implements CompOutput{
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> output = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> conSpan = null;
}
