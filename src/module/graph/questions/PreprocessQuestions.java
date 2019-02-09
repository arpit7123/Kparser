/**
 * 
 */
package module.graph.questions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.trees.Tree;


/**
 * @author arpit
 *
 */


/**
 * changelog: 07/23/2015
 * - added method getTreeInfo - detect where the first NP node ends, to put the first word in case of YES_NO question
 * - changed way nextWord is calculated
 * - added method getWordPosition - gets position of the nextWord in a sentence (for use in SentenceToGraph)
 * 	 Don't know if this is most efficient method but it works.
 *
 * -samrawal
 */
public class PreprocessQuestions {
	private static PreprocessQuestions preprocessQuestions = null;
	private QuestionsResources questionsResources = null;
	private Pattern tagQuesPat = Pattern.compile("(.*)(,)([^,]+)");
	
	static {
		preprocessQuestions = new PreprocessQuestions();
	}
	
	private PreprocessQuestions(){
		questionsResources = QuestionsResources.getInstance();
	}

	public static PreprocessQuestions getInstance() {
		return preprocessQuestions;
	}
	
	public ProcessedQuestion preprocessQuestion(String inputQuestion, Tree t){
		ProcessedQuestion pq = null;
		QuestionType qt = null;
		String placeholder = null;
		String input = null;
		
		String firstWord = inputQuestion.substring(0, inputQuestion.indexOf(" "));
		String temp = inputQuestion.substring(firstWord.length()).trim();
		String first2Words = firstWord + " " + temp.substring(0, temp.indexOf(" "));
		int startIndex = 0;
		int endIndex = inputQuestion.length()-2;
		if(questionsResources.isAnAuxiliaryVerb(first2Words.toLowerCase())){
			qt = QuestionType.YES_NO;		
			startIndex = first2Words.length();
			input = first2Words.toLowerCase();
		}else if(questionsResources.isAnAuxiliaryVerb(firstWord.toLowerCase())){
			qt = QuestionType.YES_NO;		
			startIndex = firstWord.length();
			input = firstWord.toLowerCase();
		}else if(questionsResources.isWhWord(first2Words.toLowerCase())){
			qt = QuestionType.WH;
			startIndex = first2Words.length();
			firstWord = first2Words;
		}else if(questionsResources.isWhWord(firstWord.toLowerCase())){
			qt = QuestionType.WH;
			startIndex = firstWord.length();
		}else{
			qt = QuestionType.TAG;		
		}
		
		String processedQues = null;
		String nextWord = null;
		int nextWordPosition = 0;
		String wordSense = null;
		String whWord = null;
		String endCharacter = null;
		
		switch (qt) {
			case YES_NO:
				String replace = getTreeInfo(t);
				processedQues = inputQuestion.substring(startIndex,endIndex).trim();
				endCharacter = ".";
				
				String temp2 = processedQues;
				processedQues = temp2.substring(0, temp2.indexOf(replace)+replace.length())+" "+input.toLowerCase()+temp2.substring(temp2.indexOf(replace)+replace.length());
				
				nextWord = processedQues.substring(0,processedQues.indexOf(" "));
				nextWord = replace.substring(replace.lastIndexOf(" ")).trim();
				nextWordPosition = getWordPosition(processedQues, nextWord);
				
				break;
			case WH:
				processedQues = questionsResources.getWhWordReplacement(firstWord.toLowerCase()) + " " + inputQuestion.substring(startIndex,endIndex).trim();
				placeholder = questionsResources.getWhWordReplacement(firstWord.toLowerCase());
				wordSense = questionsResources.getWhWordSense(firstWord.toLowerCase());
				endCharacter = questionsResources.getEndCharacter(firstWord.toLowerCase());
				whWord = firstWord;
				break;
			case TAG:
				Matcher m = tagQuesPat.matcher(inputQuestion);
				if(m.matches()){
					String tmpStr = m.group(3);
					int suffixLen = tmpStr.length();
					tmpStr = tmpStr.trim();
					String[] tmpArr = tmpStr.split(" ");
					int len = tmpArr.length;
					String tmpFirstWord = null;
					String tmpFirst2Words = null;
					if(len>0){
						tmpFirstWord = tmpArr[0];
					}
					if(len>1){
						tmpFirst2Words = tmpArr[1];
					}
					
					if(questionsResources.isAnAuxiliaryVerb(tmpFirst2Words)
							|| questionsResources.isAnAuxiliaryVerb(tmpFirstWord)){
						endIndex = endIndex - suffixLen + 1;
					}					
				}
				processedQues = inputQuestion.substring(startIndex,endIndex).trim();
				endCharacter = ".";
				break;
			default:
				break;
		}
		
		pq = new ProcessedQuestion(processedQues+" " + endCharacter,qt);
		pq.setPlaceholder(placeholder);
		pq.setNextWord(nextWord);
		pq.setNextWordPosition(nextWordPosition);
		pq.setWhWord(whWord);
		pq.setWordSense(wordSense);
		
		return pq;
	}
	
	
	public String getTreeInfo(Tree t){
		int nodeNumber=0;
		String words = "";
		
		for (int i=1; i<t.size(); i++){
			if (t.getNodeNumber(i).label().toString().equals("NP")){
				nodeNumber=i;
				break;
			}
		}
		
		for (int j=0; j<t.getNodeNumber(nodeNumber).getLeaves().size(); j++){
			words = " "+t.getNodeNumber(nodeNumber).getLeaves().get(j).toString();
		}
		
		
		return words;
	}
	
	
	public int getWordPosition(String sentence, String word){
		int r=0;
		
		for (int i=0; i<sentence.indexOf(word); i++){
			if (sentence.charAt(i) == ' '){
				r++;
			}
		}
		
		return r+1;
	}
	
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args){
		PreprocessQuestions pq = new PreprocessQuestions();
//		ProcessedQuestion p = pq.preprocessQuestion("Is it good ?");
//		System.out.println(p.toString());
	}
	
}
