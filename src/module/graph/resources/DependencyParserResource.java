/*******************************************************************************
 * Copyright (C) 2017 Arpit Sharma
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package module.graph.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import module.graph.helper.Configurations;
import module.graph.helper.WordToken;
import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class DependencyParserResource implements IResource {

	private int BG_INDEX = 0;
	private LexicalizedParser lexicalizedParser;
	private ArrayList<WordToken> tokens;
	private Tree t;
	private boolean backGroundFlag = false;
	private HashMap<String,HashMap<String,String>> mapOfDependencies = new HashMap<String,HashMap<String,String>>();
	private HashMap<String,ArrayList<String>> mapOfDeps = new HashMap<String,ArrayList<String>>();
	private HashMap<String,String> eventOrderMap = new HashMap<String,String>();
	private HashMap<String,String> posMap = new HashMap<String,String>();
	
	private String depFormat = "dep(%s,%s,%s).";
	private Pattern p = Pattern.compile("(dep\\()(.*)(\\)).");
	
	
	public DependencyParserResource() {
		try{
			String pcfgModelFile = Configurations.getProperty("pcfgModelFile");
			if(pcfgModelFile.startsWith("./")){
				pcfgModelFile = Configurations.getCWD() + pcfgModelFile.substring(2);
			}
			lexicalizedParser = LexicalizedParser.loadModel(pcfgModelFile);
		}catch(Exception e){
			System.err.println("ERROR: Could not load the stanford PCFG model file !!!");
		}
		
	}
	
	public HashMap<String,String> getPOSMap(String sentence){
		this.posMap.clear();
		tokenizeInputText(sentence);
		extractPOSTags();
		return posMap;
	}
	
	public InputDependencies extractDependencies(String sentence, boolean bgFlag, int index){
		if(sentence.equals("")){
			return null;
		}
		this.mapOfDependencies.clear();
		this.mapOfDeps.clear();
		this.posMap.clear();
		this.eventOrderMap.clear();
		
		this.backGroundFlag = bgFlag;
		this.BG_INDEX = index;
		if(bgFlag==true && index==0){
			this.BG_INDEX = 100;
		}
		
		try{
			tokenizeInputText(sentence);
			extractPOSTags();
			extractEventsOrder();
			ArrayList<String> tempbuffer = getFormattedDeps();

			for(String s1 : tempbuffer){
				Matcher m = p.matcher(s1);
				if(m.find()){
					s1 = m.group(2);
				}
				String[] tokenizer=s1.split(",");
				for(int i = 0 ;i<tokenizer.length;++i){
					if(tokenizer[i].charAt(tokenizer[i].length()-1)=='\''){
						tokenizer[i] = tokenizer[i].substring(0,tokenizer[i].length()-1);
					}
				}
				if(this.backGroundFlag){
					String[] temp = new String[2];
					temp[0] = tokenizer[1].substring(0,tokenizer[1].lastIndexOf("-"));
					temp[1] = tokenizer[1].substring(tokenizer[1].lastIndexOf("-")+1);
					int num = BG_INDEX + Integer.parseInt(temp[1]);
					tokenizer[1] = temp[0]+"-"+num;

					temp[0] = tokenizer[2].substring(0,tokenizer[2].lastIndexOf("-"));
					temp[1] = tokenizer[2].substring(tokenizer[2].lastIndexOf("-")+1);
					num = BG_INDEX + Integer.parseInt(temp[1]);
					tokenizer[2] = temp[0]+"-"+num;
				}
				String resolvedString="";
				resolvedString=resolvedString+tokenizer[0]+",";
				if(!resolvedString.contains("root")){
					if(!mapOfDeps.containsKey(tokenizer[0])){
						ArrayList<String> list = new ArrayList<String>();
						list.add(tokenizer[1]+"#"+ tokenizer[2]);
						mapOfDeps.put(tokenizer[0],list);
					}else{
						ArrayList<String> list = mapOfDeps.get(tokenizer[0]);
						list.add(tokenizer[1]+"#"+ tokenizer[2]);
						mapOfDeps.put(tokenizer[0],list);
					}
					if(!mapOfDependencies.containsKey(tokenizer[0])){
						HashMap<String,String> map = new HashMap<String,String>();
						map.put(tokenizer[1], tokenizer[2]);
						mapOfDependencies.put(tokenizer[0],map);
					}else{
						if(!mapOfDependencies.get(tokenizer[0]).containsKey(tokenizer[1])){
							mapOfDependencies.get(tokenizer[0]).put(tokenizer[1],tokenizer[2]);
						}
					}
					resolvedString=resolvedString+tokenizer[1] + ",";
					resolvedString=resolvedString+tokenizer[2];
				}
			}
		}
		catch (Exception e){
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		
		InputDependencies inDeps = new InputDependencies();
		inDeps.setEventOrderMap(eventOrderMap);
		inDeps.setMapOfDependencies(mapOfDependencies);
		inDeps.setMapOfDeps(mapOfDeps);
		inDeps.setPosMap(posMap);
		inDeps.setPennTree(t);
		inDeps.setSentence(sentence);
		
		return inDeps;
	}


	private void tokenizeInputText(String inputText) {
//		get the parse tree
		t = lexicalizedParser.parse(inputText); 

//		Generate tokens
//		Get words: 1st method
		tokens = new ArrayList<WordToken>();
		List<LabeledWord> words = t.labeledYield();
		int index = 0;
		for (LabeledWord w : words) {
			tokens.add(new WordToken(w.beginPosition(), w.endPosition(), index++, w.tag().value(), w.value()));
		}
	}
	
	public Tree getSyntacticTree(String inputText){
		return lexicalizedParser.parse(inputText); 
	}
	
	// Extracts part-of-speech tags from Stanford Parse tree of input text and stores in global structure.
	private void extractPOSTags(){
		List<TaggedWord> taggedWords = t.taggedYield();
		int count;
		if(this.backGroundFlag){
			count=BG_INDEX+1;
		}else{
			count=1;
		}
		for(int i=0;i<taggedWords.size();i++){
			String word = taggedWords.get(i).toString();
			String[] wordPos = word.split("/");
			posMap.put(wordPos[0]+"-"+count, wordPos[1]);
			count++;
		}
	}

	private void extractEventsOrder() {
		int eventOrder = 1;
		int counter;
		if(this.backGroundFlag){
			counter=BG_INDEX+1;
		}else{
			counter=1;
		}
		for (WordToken wordToken : tokens) {
			String word = wordToken.getValue().split("/")[0];
			if(posMap.get(word+"-"+counter).startsWith("V")){
				eventOrderMap.put("e"+eventOrder++,wordToken.getValue()+"-"+counter);
			}
			counter++;
		}
	}

	private ArrayList<String> getFormattedDeps() {
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
		Collection<TypedDependency> typedDependencies = gs.typedDependenciesCCprocessed();
		ArrayList<String> formattedDeps = new ArrayList<String>();
		for (TypedDependency typedDependency : typedDependencies) {

			String[] govArr = new String[2];
			String tmp = typedDependency.gov().label().toString();
			govArr[0] = tmp.substring(0,tmp.lastIndexOf("-"));
			govArr[1] = tmp.substring(tmp.lastIndexOf("-")+1);
			String gov = govArr[0].split("/")[0] + "-" + govArr[govArr.length-1];
			
			tmp = typedDependency.dep().label().toString();
			String[] depArr =new String[2]; 
			depArr[0] = tmp.substring(0,tmp.lastIndexOf("-"));
			depArr[1] = tmp.substring(tmp.lastIndexOf("-")+1);
			String dep = depArr[0].split("/")[0] + "-" + depArr[depArr.length-1];
			String formatedDep = String.format(depFormat,
								 typedDependency.reln(),
								 gov,dep);
			formattedDeps.add(formatedDep);
		}
		return formattedDeps;
	}
	
	
	public static void main(String args[]){
		DependencyParserResource lpr = new DependencyParserResource();
		String sent = "How are you ?";
//		sent = "A couple having a picnic in front of a house by the edge of a lake, where a man is flying a kite over the lake and next to a pier.";
//		sent = "The older students were bullying the younger students so we rescued them.";
//		sent = "John loves Mia because she loves men, but she is a bad girl.";
//		sent = "He killed the king of Jungle.";
//		sent = "John killed more peopple than Tom.";
//		sent = "Who is the president of the USA ?";
//		sent = "The boy is more bright.";
//		sent = "The boys all left.";
//		sent = "Barack Obama is the president of the U.S.A.";
//		sent = "I like that you are awesome.";
//		sent = "It is possible that the boy goes.";
//		sent = "It is possible that I am crazy.";
//		sent = "more than four thousand boys went away.";
		sent = "In the mid-80s, wind turbines had a typical maximum power rating of 150 kW.";
		sent = "john gave #20 to tom .";
		sent = "The Treasury said the U.S. will default on Nov. 9 if Congress doesn’t act by then.";
		sent = "Political and currency gyrations can whipsaw the funds. Another concern: The funds’ share prices tend to swing more than the broader market";
		sent = "There is a tree with leaves in front of the house and to the left and behind the picnickers.";
		sent = "I spread the cloth on the table in order to protect it.";
		sent = "Bo is playing in the sand at the edge of the lake.";
		sent = "We had a large party about fifty people or so , and yet everything was served quickly and we all had a wonderful time .";
		sent = "He must have parked around the front of the motel .";
		sent = "a sail boat is out on the water and a young person is on the water by the edge someone is fishing on the dock";
		sent = "Whose bishop did Tom take?";
		sent = "One stamp costs 34 cents.";
		sent = "Who is nosy?";
		sent = "Yesterday, the demonstrators threw stones at the soldiers in Israel.";
		sent = "What couldn't we use?";
		sent = "Jane knocked on Susan's door, but there was no answer. She was disappointed.";
		sent = "Who was disappointed?";
		sent = "I paid them to deliver it";
		sent = "This seems absurd as I paid them to deliver this package and it wasn't.";
//		sent = " Drivers will be paid $18 to $25 an hour to deliver packages ordered with Amazon Prime Now";
		sent = "I took the water bottle out of the backpack so that it is lighter.";
		sent = "Since the oil has been taken out of it, it is lighter by weight and will have";
		sent = "there was no answer when I knocked on her door, and, assuming she was out, I left a note of my own";
		sent = "Who out?";
		sent = "John was doing research in the library when he heard a man humming and whistling. He was very annoying.";
		sent = "Police have arrested a man they say was running a meth lab at a campsite.";
		sent = "The cat was lying by the mouse hole, the cat was waiting for the mouse, but it was too impatient.";
		sent = "I am lying here, I am waiting for him.";
		sent = "Trump is on the scene to explain to people that don't understand him";
		sent = "No matter how many times I try to explain. They don't understand me and I feel like I am on my own.";
		sent = "Shrubs can be covered with a blanket to protect them from a late spring frost.";
		sent = "Madonna fired her trainer because she slept with her boyfriend.";
		sent = "Whose boyfriend?";
		sent = "It was smashed through careless handling, I grieved, repaired it with heroic measures, and after it was filmed it got smashed again.";
		sent = "In the storm, the tree fell down and crashed through the roof of my house. Now, I have to get it repaired.";
		sent = "John hired Bill to take care of him.";
		sent = "Author is a cruel bastard for toying with my emotions.";
		sent = "On the surface, history's cruel irony seemed to be toying with Central and Eastern Europe again. ";
		sent = "Dan had to stop Bill from toying with the injured bird. He is very cruel.";
		sent = "To the mum who carried their child whose legs dangled beneath them.";
		sent = "One performer I watched was juggling/VBG . He was very impressive and seemed to do things almost impossible by juggling things on fire. ";
		sent = "John was doing research in the library when he heard a man humming and whistling.";
		sent = "But I rushed it because it felt like it was going really really slow .";
		sent = "Anna did a lot worse than her good friend Lucy on the test because she had studied so hard.";
		sent = "John bought, and moved into a home in 2007.";
		sent = "Chinese authorities arrested a pastor to stop him from traveling to Hong Kong to attend a Christian training conference, the legal aid organization China Aid reports.";
		sent = "If someone speaks to you and breaks your concentration, or you realise a harness strap is twisted and have to untwist it, start checks again at the beginning.";
		sent = "I took the water bottle out of the backpack so that it is lighter.";
		sent = "Since the oil has been taken out of it , it is lighter by weight and will have";
		sent = "She could not lift it off the floor because she is a weak girl .";
		sent = "This seems absurd as I paid them to deliver this package and it was not.";
		sent = "Spanish authorities tried to stop the vote by arresting secessionist leaders and seizing ballots.";
//		sent = "Authorities tried to stop the trade by arresting a criminal";
		sent = "If the con artist has succeeded in fooling Sam, he would have gotten a lot of money. ";
		sent = "he fooled them to get their vote then flipped that racist switch on them.";
//		sent = "Steph curry fooled the entire pelicans defense to get draymond green a wide open three";
		sent = "John gave $20 to Tom.";
		sent = "John gave dollars_20 to Tom .";
		lpr.extractDependencies(sent, false, 0);

		for(String s : lpr.posMap.keySet()){
			System.out.println(s + ":" + lpr.posMap.get(s));
		}
		
		System.out.println("*************");
		
		for(String s : lpr.mapOfDependencies.keySet()){
			System.out.println(s);
			for(String s1 : lpr.mapOfDependencies.get(s).keySet()){
				System.out.println(s1 + ":" + lpr.mapOfDependencies.get(s).get(s1));
			}
		}
		
		System.out.println("*************");
		
		for(String s : lpr.eventOrderMap.keySet()){
			System.out.println(s + ":" + lpr.eventOrderMap.get(s));
		}
	}

}
