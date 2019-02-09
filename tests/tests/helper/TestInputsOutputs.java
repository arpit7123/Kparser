package tests.helper;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;

public class TestInputsOutputs {
	private static TestInputsOutputs testIO = null;
	private HashMap<String,TestDataNode> testData = null;
	private static String testsDataFile = TestInputsOutputs.class.getResource("testsDataFile.json").toString();

	static {
		testIO = new TestInputsOutputs();
	}

	private TestInputsOutputs(){
		readTests();
	}

	public static TestInputsOutputs getInstance() {
		return testIO;
	}

	private void readTests(){
		HashMap<String,TestDataNode> result = Maps.newHashMap();
		try{
			URL in = null;
			in = new URL(testsDataFile);
			String content = CharStreams.toString(new InputStreamReader(in.openStream(), Charsets.UTF_8));
			JSONArray arr = new JSONArray(content);
			for(int i = 0; i < arr.length(); i++){
				JSONObject temp = arr.getJSONObject(i);
				String type = temp.getString("type");
				String input = temp.getString("input");
				JSONArray jsonArray = (JSONArray) temp.get("output");
				ArrayList<String> a = Lists.newArrayList();
				int len = jsonArray.length();
				for (int j=0;j<len;j++){ 
					a.add(jsonArray.get(j).toString());
				}
				TestDataNode node = new TestDataNode(input,a);
				result.put(type, node);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		testData = result;
	}

	public HashMap<String,TestDataNode> getTestData(){
		return testData;
	}
}
