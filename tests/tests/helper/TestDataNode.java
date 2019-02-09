package tests.helper;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class TestDataNode {
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private String input = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private ArrayList<String> output = null;
	
	public TestDataNode(String input, ArrayList<String> output){
		this.input = input;
		this.output = output;
	}
}
