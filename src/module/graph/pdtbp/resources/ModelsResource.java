/**
 * 
 */
package module.graph.pdtbp.resources;

import java.io.File;
import java.util.HashMap;

import module.graph.helper.Configurations;
import opennlp.maxent.GISModel;
import opennlp.maxent.io.SuffixSensitiveGISModelReader;

/**
 * @author arpit
 *
 */
public class ModelsResource {

	private HashMap<String,GISModel> mapOfpdtbModels = null;
	private static ModelsResource pdtbModelResource = null;
	
	static{
		pdtbModelResource = new ModelsResource();
	}
	
	private ModelsResource(){
		mapOfpdtbModels = new HashMap<String, GISModel>();
		try{
			String pdtbResourcesDir = Configurations.getProperty("pdtbDir");
			if(pdtbResourcesDir.startsWith("./")){
				pdtbResourcesDir = Configurations.getCWD() + pdtbResourcesDir.substring(2);
			}
			String connModelFileName = pdtbResourcesDir + "/conn.model";
			GISModel model = (GISModel) new SuffixSensitiveGISModelReader(new File(connModelFileName)).getModel();
			mapOfpdtbModels.put("conn", model);

			String argPosModelFileName = pdtbResourcesDir + "/argpos.model";
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(argPosModelFileName)).getModel();
			mapOfpdtbModels.put("argpos", model);

			String argExtModelFileName = pdtbResourcesDir + "/argext.model";
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(argExtModelFileName)).getModel();
			mapOfpdtbModels.put("argext", model);

			String expModelFileName = pdtbResourcesDir + "/exp.model";
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(expModelFileName)).getModel();
			mapOfpdtbModels.put("exp", model);

			String noExpModelFileName = pdtbResourcesDir + "/nonexp.model";
			model = (GISModel) new SuffixSensitiveGISModelReader(new File(noExpModelFileName)).getModel();
			mapOfpdtbModels.put("noexp", model);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("An error occurred in creating map of PDTB classification models !");
		}
	}
	
	public static ModelsResource getInstance() {
		return pdtbModelResource;
	}
	
	public GISModel getPDTBClassifierModel(String modelName){
		GISModel result = null;
		if(mapOfpdtbModels.containsKey(modelName)){
			result = mapOfpdtbModels.get(modelName);
		}
		return result;
	}
	
}
