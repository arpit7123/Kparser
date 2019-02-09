/**
 * 
 */
package kparser.devel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Arpit Sharma
 * @date Jun 13, 2017
 *
 */
public class ReadPropbankXML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadPropbankXML rpx = new ReadPropbankXML();
		
		String propbankMapFile = "/home/arpit/workspace/Kparser/propbank_data/propbankMap.ser";
//		String xmlFilePath = "/home/arpit/workspace/Test/file.xml";
//		Utilities.saveObject(rpx.readPropbankXML(xmlFilePath),propbankMapFile);
		
		HashMap<String,PropbankData> result = (HashMap<String, PropbankData>) Utilities.load(propbankMapFile);
		
		System.exit(0);
		
	}

	private HashMap<String,PropbankData> readPropbankXML(String xmlFilePath){
		HashMap<String,PropbankData> mapOfPropData = new HashMap<String,PropbankData>();

		try {
			File fXmlFile = new File(xmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("verbGroup");
			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					PropbankData pbData = new PropbankData();
					ArrayList<String> listOfVerbs = new ArrayList<String>();
					
					NodeList textsList = eElement.getElementsByTagName("Predicate");
					for (int temp1 = 0; temp1 < textsList.getLength(); temp1++) {
						Node nNode1 = textsList.item(temp1);
						if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
							Element textElement = (Element) nNode1;
							String predName = textElement.getElementsByTagName("Value").item(0).getTextContent();
							predName = predName.substring(predName.indexOf(":")+1).trim();
							if(temp1==0){
								pbData.setDriverVerb(predName);
							}else{
								listOfVerbs.add(predName);
							}						
						}
					}
					
					pbData.setVerbs(listOfVerbs);
					
					mapOfPropData.put(pbData.getDriverVerb(), pbData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapOfPropData;
	}

}
