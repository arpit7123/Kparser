package tests.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;

import com.google.common.collect.Sets;

import tests.helper.TestDataNode;
import tests.helper.TestInputsOutputs;
import module.graph.SentenceToGraph;
import module.graph.helper.GraphPassingNode;

public class SemanticMappingTests {
	
	SentenceToGraph stg = null;
	TestInputsOutputs testsIO = null;
	HashMap<String,TestDataNode> testData = null;

	@Before
	public void initialize(){
		stg= new SentenceToGraph();
		testsIO = TestInputsOutputs.getInstance();
		testData = testsIO.getTestData();
	}
	
//	@Test
//	public void testSubject(){
//		GraphPassingNode actualResult = stg.extractGraph(testData.get("nsubj-agent1").getInput(), false, true, false);
//		ArrayList<String> expectedResult = testData.get("nsubj-agent1").getOutput();
//		test(actualResult.getAspGraph(),expectedResult);
//	}
	
	@org.junit.Test
	public void testAll() throws Exception{
		for(String dep : testData.keySet()){
			System.out.println(dep);
			TestDataNode ipOp = testData.get(dep);
			GraphPassingNode actualResult = stg.extractGraph(ipOp.getInput(), false, true, false);
			ArrayList<String> expectedResult = ipOp.getOutput();
			test(actualResult.getAspGraph(),expectedResult);
		}
	}
	
	
	private void test(ArrayList<String> list1, ArrayList<String> list2){
		org.junit.Assert.assertEquals(list1.size(), list2.size());
		HashSet<String> set1 = Sets.newHashSet(list1);
		HashSet<String> set2 = Sets.newHashSet(list2);
		if(set1.size()==set2.size()){
			for(String s : set1){
				org.junit.Assert.assertEquals(set2.contains(s),true);
			}
		}
	}

}
