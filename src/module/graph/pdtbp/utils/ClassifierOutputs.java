/**
 * 
 */
package module.graph.pdtbp.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */
public class ClassifierOutputs {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ConCompOutput conOutput = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArgPosOutput argPosOutput = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArgExtOutput argExtOutput = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ExpCompOutput expOutput = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private NoExpCompOutput noExpOutput = null;
}
