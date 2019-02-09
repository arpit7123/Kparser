/**
 * 
 */
package module.graph.questions;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author arpit
 *
 */

/**
 * changelog: 07/23/2015
 * - added getter/setter nextWordPosition - for use in SentenceToGraph
 * 
 * 	-samrawal
 */

public class ProcessedQuestion implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -958391947190648369L;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String processedText = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private QuestionType type = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String placeholder = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String nextWord = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private int nextWordPosition = 0; 
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String wordSense = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String whWord = null;
	
	public ProcessedQuestion(String processedText,QuestionType type){
		this.processedText = processedText;
		this.type = type;
	}
	
	@Override
	public String toString(){
		String s = "Processed Text = "+processedText+
				"\n" + "Question Type = " + type.toString() +
				"\n" + "Next Word = " + nextWord +
				"\n" + "Next Word Position" + nextWordPosition +
				"\n" + "Word Sense = " + wordSense +
				"\n" + "Wh Word = " + whWord + 
				"\n" + "Place Holder = " + placeholder;
		return s;
	}
}
