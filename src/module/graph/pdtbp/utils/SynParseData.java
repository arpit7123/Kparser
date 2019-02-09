/**
 * 
 */
package module.graph.pdtbp.utils;

import java.util.ArrayList;

import edu.stanford.nlp.trees.Tree;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */
public class SynParseData {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private Tree pennTree = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> depList = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> spanList = null;
}
