package tests.mapping;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test {
		@SuppressWarnings("unused")
		private String sentenceMappingFile = "/home/arpit/Desktop/sentMapfile.xml";
		private String resultsFile = "";//"resultsfile.xml";
		private String tabSeparatedFile = "";//"commaSepFile2.csv";

		/**
		 * @param args
		 */
		public static void main(String[] args) {
			Test rxf = new Test();
			rxf.readSentenceXML();
		}

		private void readSentenceXML(){
			try{
				File fXmlFile = new File(sentenceMappingFile);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);

				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("Sentence");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node node = nList.item(temp);
					NodeList nList1 = node.getChildNodes();
					if(nList1.item(0).getNodeValue()!=null)
					System.out.println(nList1.item(0).getNodeValue());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
}
