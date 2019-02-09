/**
 * Copyright (C) 2015 WING, NUS and NUS NLP Group.
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
 */

package module.graph.pdtbp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;
import module.graph.pdtbp.resources.ModelsResource;
import module.graph.pdtbp.resources.Node;
import module.graph.pdtbp.utils.CompOutput;
import module.graph.pdtbp.utils.Corpus;
import module.graph.pdtbp.utils.ConCompOutput;
import module.graph.pdtbp.utils.ExpCompOutput;
import module.graph.pdtbp.utils.MaxEntClassifier;
import module.graph.pdtbp.utils.SynParseData;
import module.graph.pdtbp.utils.Util;

/**
 * 
 * @author ilija.ilievski@u.nus.edu
 *
 */
public class ExplicitComp extends Component {
	public static final String NAME = "exp";
	
	private List<Tree> trees;
	private ConCompOutput conCompResult = null;
	private Map<String,String> spanHashMap = null;

	public ExplicitComp(ConCompOutput conCompResult) {
		this.conCompResult = conCompResult;
	}
	
	@Override
	public CompOutput parseAnyText(String modelName, String inputText, SynParseData synPData, ModelsResource modelsResource)
			throws IOException {
		this.trees = new ArrayList<Tree>();
		this.trees.add(synPData.getPennTree());

		List<String> explicitSpans = Corpus.genExplicitSpans(this.conCompResult);
		
		List<String[]> features = generateFeatures(inputText, synPData, explicitSpans);
		
		CompOutput expOutput = MaxEntClassifier.predict(features, modelName, modelsResource);

		ArrayList<String> classifierOutput = ((ExpCompOutput) expOutput).getOutputList();
		ArrayList<String> pipeOut = new ArrayList<String>();
		int counter = 0;
		for (String[] feature : features) {
				String[] tmp = classifierOutput.get(counter).split("\\s+");
				String[] cols = feature[1].split("\\|", -1);
				String fullSense = Corpus.getFullSense(tmp[tmp.length - 1]);
				pipeOut.add(cols[3] + "|" + fullSense);
				counter++;
		}	
		((ExpCompOutput) expOutput).setPipeOut(pipeOut);

		return expOutput;
	}

	@Override
	protected List<String[]> generateFeatures(String inputText,
			SynParseData synParseData, List<String> explicitSpans)
			throws IOException {
		List<String[]> features = new ArrayList<>();

		ArrayList<String> spanArray = null;
		spanArray = synParseData.getSpanList();
		this.spanHashMap = getSpanMap(spanArray);

		int index = 0;
		int contIndex = 0;

		for (String rel : explicitSpans) {
			String[] cols = rel.split("\\|", -1);
			index = contIndex;
			List<Node> nodes = new ArrayList<>();
			Tree root = null;

			String[] spans = cols[3].split(";");

			for (String spanTmp : spans) {
				String[] span = spanTmp.split("\\.\\.");

				for (; index < spanArray.size(); ++index) {
					// wsj_1371,0,6,9..21,Shareholders
					String line = spanArray.get(index);
					String[] spanCols = line.split(",");
					String[] canSpan = spanCols[3].split("\\.\\.");

					// Start matches
					if (span[0].equals(canSpan[0]) || nodes.size() > 0) {
						if (nodes.size() == 0) {
							contIndex = index;
						}
						root = trees.get(Integer.parseInt(spanCols[1]));
						List<Tree> leaves = root.getLeaves();
						int start = Integer.parseInt(spanCols[2]);
						Tree node = root.getNodeNumber(start);
						int nodeNum = 0;

						for (; nodeNum < leaves.size(); ++nodeNum) {
							Tree potNode = leaves.get(nodeNum);
							if (node.equals(potNode)) {
								int tmp = potNode.nodeNumber(root);
								String tmpSpan = spanHashMap.get(spanCols[1] + ":" + tmp);
								if (tmpSpan.equals(spanCols[3])) {
									break;
								}
							}
						}

						nodes.add(new Node(node, nodeNum));

						if (span[1].equals(canSpan[1])) {
							break;
						}
					}
				}
			}

			if (!nodes.isEmpty()) {
				Set<String> semantics = Util.getUniqueSense(new String[] { cols[11],
						cols[12] });
				String sem = "";
				semantics.add("xxx");
				
					for (String e : semantics) {
						sem += e.replace(' ', '_');
						sem += "£";
					}
					String feature = printFeature(root, nodes, sem);
					features.add(new String[] { feature, rel });
			}
		}

		return features;
	}

	
	private Map<String, String> getSpanMap(ArrayList<String> spanList) throws IOException {
		Map<String, String> result = new HashMap<>();
				for(String line : spanList){
				String[] cols = line.split(",", -1);
				String key = cols[1] + ":" + cols[2];
				String value = cols[3];
				if (result.containsKey(key)) {
					System.err.println("Duplicate span (" + key);
				}
				result.put(key, value);
			}
		return result;
	}

	private String printFeature(Tree root, List<Node> nodes, String label) {

		StringBuilder feature = new StringBuilder();

		StringBuilder tmp = new StringBuilder();
		StringBuilder tmp2 = new StringBuilder();
		for (Node node : nodes) {
			if (node.tree.parent(root) != null) {
				tmp.append(node.tree.parent(root).value() + " ");
				tmp2.append(node.tree.value() + " ");
			}
		}
		String POS = tmp.toString().trim().replace(' ', '_');
		String connStr = tmp2.toString().trim().replace(' ', '_');
		if (connStr.equalsIgnoreCase("if_then") || connStr.equalsIgnoreCase("either_or")
				|| connStr.equalsIgnoreCase("neither..nor")) {
			connStr = connStr.replaceAll("_", "..");
		}
		List<Tree> leaves = root.getLeaves();

		int firstNodeNum = nodes.get(0).index;

		Tree prevNode = firstNodeNum > 0 ? leaves.get(--firstNodeNum) : null;

		feature.append("conn_lc:");
		feature.append(connStr.toLowerCase());
		feature.append(' ');

		feature.append("conn:");
		feature.append(connStr);
		feature.append(' ');

		feature.append("conn_POS:");
		feature.append(POS);
		feature.append(' ');

		if (prevNode != null) {
			while (prevNode.parent(root).value().equals("-NONE-") && firstNodeNum > 0) {
				prevNode = leaves.get(--firstNodeNum);
			}

			if (prevNode != null) {

				feature.append("with_prev_full:");
				feature.append(prevNode.value().replace(' ', '_').toLowerCase());
				feature.append('_');
				feature.append(connStr.toLowerCase());
				feature.append(' ');

			}
		}

		feature.append(label.replace(' ', '_'));

		return feature.toString().replaceAll("/", "\\\\/");
	}

}
