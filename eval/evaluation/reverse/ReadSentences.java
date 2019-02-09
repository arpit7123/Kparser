/**
 * 
 */
package evaluation.reverse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author arpit
 *
 */
public class ReadSentences {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadSentences rs = new ReadSentences();
		String directory = "/home/arpit/Desktop/corpus/nltk_data/corpora/treebank/raw";
//		ArrayList<String> listOfSents = 
		rs.readFrom(directory);
		
	}
	
	
	private ArrayList<String> readFrom(String dir){
		ArrayList<String> list = Lists.newArrayList();
		File folder = new File(dir);
		HashSet<String> listOfFiles = listFilesForFolder(folder);
		int indx = 1;
		for(String file : listOfFiles){
			try(BufferedReader br = new BufferedReader(new FileReader(dir+"/"+file))){
				String line = null;
				while((line=br.readLine())!=null){
					if(!line.trim().equalsIgnoreCase("")){
						if(!line.trim().equalsIgnoreCase(".START")){
							int len = line.split(" ").length;
							if(len>5 && len<=15){
//								System.out.print("Sent "+indx+":- ");
								System.out.print(indx+": ");
								System.out.println(line);
								list.add(line);
								indx++;
								if(indx>=251){
									return list;
								}
							}
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return list;
	}
	
	private HashSet<String> listFilesForFolder(File folder) {
		HashSet<String> listOfFiles = Sets.newHashSet();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
//				listFilesForFolder(fileEntry);
			} else {
				listOfFiles.add(fileEntry.getName());
			}
		}
		return listOfFiles;
	}
	
	
	public static void main_extra(String[] args){
		String fileName = "/home/arpit/Dropbox/EMNLP2015/Long/eventsEvaluation/reverseEvaluation/sentences";
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
			String line = null;
			int indx = 1;
			while((line=br.readLine())!=null){
				String[] temp = line.split(":\t");
				System.out.println(indx + ":\t" + temp[1]);
				indx++;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
