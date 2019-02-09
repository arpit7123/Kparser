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
package module.graph.pdtbp.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import module.graph.pdtbp.resources.ModelsResource;
import opennlp.maxent.BasicEventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.AbstractModelWriter;
import opennlp.model.EventStream;

/**
 * Copyright (C) 2014-2015 WING, NUS and NUS NLP Group.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see http://www.gnu.org/licenses/.
 * 
 */

/**
 * 
 * @author ilija.ilievski@u.nus.edu
 *
 */
public class MaxEntClassifier {

	public static File createModel(File trainFile, String modelFilePath) throws IOException {
		File modelFile = new File(modelFilePath);

		FileReader datafr = new FileReader(trainFile);
		EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
		AbstractModel model = GIS.trainModel(es, false);
		AbstractModelWriter writer = new SuffixSensitiveGISModelWriter(model, modelFile);
		writer.persist();

		return modelFile;
	}

	public static CompOutput predict(List<String[]> features, String modelName, ModelsResource modelsResource)
			throws IOException {
		CompOutput result = null;
		GISModel model = modelsResource.getPDTBClassifierModel(modelName);//(GISModel) new SuffixSensitiveGISModelReader(new File(modelsPath+modelName+".model")).getModel();
		if(model!=null){
			if(modelName.equalsIgnoreCase("conn")){	
				result = new ConCompOutput();
				ArrayList<String> outputList = new ArrayList<String>();
				ArrayList<String> spansList = new ArrayList<String>();
				for(String[] featrs : features){
					String[] fs = featrs[0].split("\\s+");
					double[] outcomes = model.eval(fs);
					String spanString = featrs[1] + " " + featrs[2] + " " + featrs[3].replace(' ', '_') + 
							" test.txt " + featrs[0].substring(featrs[0].lastIndexOf(" ")+1);
					outputList.add(model.getAllOutcomes(outcomes) + "  " + model.getBestOutcome(outcomes));
					spansList.add(spanString);
				}
				((ConCompOutput) result).setOutput(outputList);
				((ConCompOutput) result).setConSpan(spansList);
			}else if(modelName.equalsIgnoreCase("argpos")){
				result = new ArgPosOutput();
				ArrayList<String> outputList = new ArrayList<String>();
				for(String[] featrs : features){
					String[] fs = featrs[0].split("\\s+");
					double[] outcomes = model.eval(fs);
					outputList.add(model.getAllOutcomes(outcomes) + "  " + model.getBestOutcome(outcomes));
				}
				((ArgPosOutput) result).setOutput(outputList);
			}else if(modelName.equalsIgnoreCase("argext")){
				result = new ArgExtOutput();
				ArrayList<String> outputList = new ArrayList<String>();
				for(String[] featrs : features){
					String featureLine = featrs[0];
					String[] ftrs = featureLine.substring(0, featureLine.lastIndexOf(' ')).split(" ");
					double[] outcomes = model.eval(ftrs);
					outputList.add(model.getAllOutcomes(outcomes) + "  " + model.getBestOutcome(outcomes));			
				}
				((ArgExtOutput) result).setOutputList(outputList);
			}else if(modelName.equalsIgnoreCase("exp")){
				result = new ExpCompOutput();
				ArrayList<String> outputList = new ArrayList<String>();
				for(String[] featrs : features){
					String[] fs = featrs[0].split("\\s+");
					double[] outcomes = model.eval(fs);
					outputList.add(model.getAllOutcomes(outcomes) + "  " + model.getBestOutcome(outcomes));
				}
				((ExpCompOutput) result).setOutputList(outputList);
			}else if(modelName.equalsIgnoreCase("noexp")){
				result = new NoExpCompOutput();
				ArrayList<String> outputList = new ArrayList<String>();
				for(String[] featrs : features){
					String[] fs = featrs[0].split("\\s+");
					double[] outcomes = model.eval(fs);
					outputList.add(model.getAllOutcomes(outcomes) + "  " + model.getBestOutcome(outcomes));
				}
				((NoExpCompOutput) result).setOutputList(outputList);
			}
		}
		return result;
	}
}
