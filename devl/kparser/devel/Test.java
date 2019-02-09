/**
 * 
 */
package kparser.devel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * @author Arpit Sharma
 * @date Jul 23, 2017
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String outDir = "/home/arpit/workspace/AlzheimersProject/supervised_parser_related/AllNAACLData/";
		String arffFile = "/home/arpit/workspace/AlzheimersProject/supervised_parser_related/allNAACL2016Data.arff";
		ArffLoader l = new ArffLoader();
		l.setSource(new File(arffFile));
		Instances data = l.getDataSet();

		HashSet<String> allDeclineSents = new HashSet<String>();

		for(int i=0; i<data.numInstances(); i++){
			Instance ins = data.instance(i);
			String docId = ins.stringValue(0);
			String insClass = ins.stringValue(2);
			boolean decFlag = false;
			if(insClass.equals("Decline")){
				decFlag = true;
			}
			docId = insClass+"/"+docId;
			try(BufferedWriter out = new BufferedWriter(new FileWriter(outDir+docId))){
				String text = ins.stringValue(1);
				String[] textArr = text.split("\\.");
				for(String sent : textArr){
					sent = sent.trim();
//					System.out.println(":::"+sent+":::");
					if(!sent.equals("")){
						if(!sent.substring(sent.length()-1).equals(".")){
							sent = sent + ".";
						}
						if(decFlag){
							allDeclineSents.add(sent);
						}
						out.append(sent);
						out.newLine();
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		String allDecSentsFile = "/home/arpit/workspace/AlzheimersProject/supervised_parser_related/allDeclineSents.txt";
		try(BufferedWriter out = new BufferedWriter(new FileWriter(allDecSentsFile))){
			for(String decSent : allDeclineSents){
				out.append(decSent);
				out.newLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}


	}

}
