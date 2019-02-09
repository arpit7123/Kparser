package tests.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


public class ExtraFileForTryOuts {


	public static void main(String[] args){
		ExtraFileForTryOuts efto = new ExtraFileForTryOuts();
		String outFile = "/home/arpit/Desktop/outputFile.json";
		try{
			BufferedWriter outBr = new BufferedWriter(new FileWriter(outFile));
			String fileName = "/home/arpit/Desktop/Kparser/tests/tests/helper/testsDataFile.txt";
			String content = efto.readFile(fileName, StandardCharsets.UTF_8);
			JSONArray arr = new JSONArray(content);
			JSONArray newArr = new JSONArray();
			for(int i = 0; i < arr.length(); i++){
				JSONObject temp = arr.getJSONObject(i);
				String input = temp.getString("input");
				String output = temp.getString("output");
				//				System.out.println(input);
				String[] outArray = output.split(" ");
				ArrayList<String> a = Lists.newArrayList(outArray);
				JSONObject newObj = new JSONObject();
				newObj.put("input", input);
				newObj.put("output", a);
				newArr.put(newObj);
			}


			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(newArr.toString());
			String prettyJsonString = gson.toJson(je);
			outBr.write(prettyJsonString);
			outBr.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String readFile(String path, Charset encoding) 
			throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
