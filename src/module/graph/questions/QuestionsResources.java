/**
 * 
 */
package module.graph.questions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import module.graph.helper.Configurations;

/**
 * @author arpit
 *
 */
public class QuestionsResources {
	
	private static QuestionsResources questionsResources = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashSet<String> auxiliaryVerbs = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> whWordSenseMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> whWordReplacementMap = null;
	@Setter (AccessLevel.PUBLIC) @Getter (AccessLevel.PUBLIC) private HashMap<String,String> endCharacterMap = null;
	
	private QuestionsResources(){
		try{
			String fileName = Configurations.getProperty("auxVerbsFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			populateAuxiliaryVerbsSet(fileName);
			
			fileName = Configurations.getProperty("whWordsFile");
			if(fileName.startsWith("./")){
				fileName = Configurations.getCWD() + fileName.substring(2);
			}
			populateWhWordsMap(fileName);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the Questions parsing resources !!!");
		}
	}
	
	static {
		questionsResources = new QuestionsResources();
	}

	public static QuestionsResources getInstance() {
		return questionsResources;
	}
	
	private void populateAuxiliaryVerbsSet(String auxiliaryVerbsFile){
		auxiliaryVerbs = new HashSet<String>();
		BufferedReader br = null;
		InputStream in = null;
		try{
			in = new FileInputStream(auxiliaryVerbsFile);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=br.readLine())!=null){
				auxiliaryVerbs.add(line);
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void populateWhWordsMap(String whWordsFile){
		whWordSenseMap = new HashMap<String,String>();
		whWordReplacementMap = new HashMap<String,String>();
		endCharacterMap = new HashMap<String,String>();
		BufferedReader br = null;
		InputStream in = null;
		try{
			in = new FileInputStream(whWordsFile);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=br.readLine())!=null){
				if(!line.startsWith("\\")){
					String[] temp = line.split("\t");
					whWordSenseMap.put(temp[0], temp[1].replaceAll(" ", "_"));
					whWordReplacementMap.put(temp[0], temp[2]);
					endCharacterMap.put(temp[0], temp[3]);
				}
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public boolean isAnAuxiliaryVerb(String word){
		if(auxiliaryVerbs.contains(word)){
			return true;
		}
		return false;
	}

	public boolean isWhWord(String word){
		if(whWordSenseMap.containsKey(word)){
			return true;
		}
		return false;
	}
	
	public String getWhWordSense(String word){
		if(whWordSenseMap.containsKey(word)){
			return whWordSenseMap.get(word);
		}
		return null;
	}
	
	public String getWhWordReplacement(String word){
		if(whWordReplacementMap.containsKey(word)){
			return whWordReplacementMap.get(word);
		}
		return null;
	}
	
	public String getEndCharacter(String word){
		if(endCharacterMap.containsKey(word)){
			return endCharacterMap.get(word);
		}
		return null;
	}
}
