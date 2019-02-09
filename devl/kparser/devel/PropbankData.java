/**
 * 
 */
package kparser.devel;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Arpit Sharma
 * @date Jun 13, 2017
 *
 */
public class PropbankData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8605201743990441252L;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> verbs = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String driverVerb = null;
}
