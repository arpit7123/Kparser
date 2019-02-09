package evaluation.timeml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ReadData{


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadData rxf = new ReadData();
		String inFolder = "/home/arpit/Dropbox/EMNLP2015/Long/eventsEvaluation/TIME_ML/aquaint_timeml_1.0/data/N45";
		String outFolder = "/home/arpit/Dropbox/EMNLP2015/Long/eventsEvaluation/TIME_ML/corpus";
		ArrayList<String> sentences = rxf.readXML(inFolder,outFolder);
		for(String sentence : sentences){
			System.out.println(sentence);
		}
	}

	public ArrayList<String> readXML(String inFolder, String outFolder) {
		HashSet<String> fileNames = listFilesForFolder(new File(inFolder));
		ArrayList<String> listOfSentences = Lists.newArrayList();
		try {
			BufferedWriter eventFile = new BufferedWriter(new FileWriter(outFolder + "/allEvents.txt"));

			for (String fileName : fileNames){

//				BufferedWriter contentFile = new BufferedWriter(new FileWriter(outFolder + "/"+fileName+".txt"));
				File fXmlFile = new File(inFolder + "/"+fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);

				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();
				//				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

				NodeList nList = doc.getElementsByTagName("TimeML");
//				for (int temp = 0; temp < nList.getLength(); temp++) {
//					Node nNode = nList.item(temp);
//					//					System.out.println(nNode.getTextContent());
//					contentFile.append(nNode.getTextContent());
//				}

				nList = doc.getElementsByTagName("EVENT");
				//				System.out.println("Total Sentences = " + nList.getLength() + "\n");

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
//					System.out.println(nNode.getTextContent());
					eventFile.append(nNode.getTextContent());
					eventFile.newLine();
					//				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					//					Element eElement = (Element) nNode;
					//					NodeList answerNodeList = eElement.getElementsByTagName("answer");
					//					NodeList contextNodeList = eElement.getElementsByTagName("context");
					//					for(int i = 0;i<answerNodeList.getLength();++i){
					//						Node answerNode = answerNodeList.item(i);
					//						Element ansNode = (Element) answerNode;
					//						String senseId = ansNode.getAttribute("senseid");
					//						if(inputSenseId.equalsIgnoreCase(senseId)){
					//							for(int j=0;j<contextNodeList.getLength();++j){
					//								Node contextNode = contextNodeList.item(j);
					//								Element contxtNode = (Element) contextNode;
					//								NodeList headNodeList = contxtNode.getElementsByTagName("head");
					//								String sentencePart1 = contextNode.getFirstChild().getTextContent();
					//								sentencePart1 = sentencePart1.replaceAll("\n", "");
					//								sentencePart1 = sentencePart1.trim();
					//								String sentencePart2 = contextNode.getLastChild().getTextContent();
					//								sentencePart2 = sentencePart2.replaceAll("\n", "");
					//								sentencePart2 = sentencePart2.trim();
					//								for(int k=0;k<headNodeList.getLength();++k){
					//									listOfSentences.add(sentencePart1 + " <head>" 
					//											+ headNodeList.item(j).getTextContent() + "</head> " + sentencePart2);
					//								}
					//							}
					//						}
					//					}
					//				}
				}

//				contentFile.close();

			}
			eventFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listOfSentences;
	}

	private HashSet<String> listFilesForFolder(File folder) {
		HashSet<String> listOfFiles = Sets.newHashSet();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listOfFiles.addAll(listFilesForFolder(fileEntry));
			} else {
				listOfFiles.add(fileEntry.getName());
			}
		}
		return listOfFiles;
	}
}
