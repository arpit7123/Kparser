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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;
import module.graph.pdtbp.resources.Dependency;
import module.graph.pdtbp.resources.ModelsResource;
import module.graph.pdtbp.resources.Stemmer;
import module.graph.pdtbp.resources.TreeNode;
import module.graph.pdtbp.utils.CompOutput;
import module.graph.pdtbp.utils.Corpus;
import module.graph.pdtbp.utils.Corpus.Type;
import module.graph.pdtbp.utils.ExpCompOutput;
import module.graph.pdtbp.utils.MaxEntClassifier;
import module.graph.pdtbp.utils.NoExpCompOutput;
import module.graph.pdtbp.utils.SynParseData;
import module.graph.pdtbp.utils.Util;

/**
 * 
 * @author ilija.ilievski@u.nus.edu
 *
 */
public class NonExplicitComp extends Component {

	public final String NAME = "nonexp";

	private List<Tree> trees;

	private HashMap<String, Dependency> dtreeMap;

	private Map<String, Double> featureProdRules;
	private Map<String, Double> featureDepRules;
	private Map<String, Double> featureWordPairs;

	private final int NUM_PROD_RULES = 100;
	private final int NUM_DEP_RULES = 100;
	private final int NUM_WORD_PAIRS = 500;

	private static final int PHRASE_LENGTH = 3;

	private Type corpus = Type.PDTB;

	private String orgText;

	private Map<String, LinkedList<Integer>> spanToSenId;
	
	private ExpCompOutput expCompResult = null;

	public NonExplicitComp(ExpCompOutput expCompResult) throws IOException {
		this.expCompResult = expCompResult;
		String prodRulesFileName = NonExplicitComp.class.getResource("prod_rules.txt").toString();
		prodRulesFileName = prodRulesFileName.substring(prodRulesFileName.indexOf(":")+1);
		featureProdRules = initRules(prodRulesFileName, this.NUM_PROD_RULES);
		
		String depRulesFileName = NonExplicitComp.class.getResource("dep_rules.txt").toString();
		depRulesFileName = depRulesFileName.substring(depRulesFileName.indexOf(":")+1);
		featureDepRules = initRules(depRulesFileName, this.NUM_DEP_RULES);
		
		String wordPairsFileName = NonExplicitComp.class.getResource("word_pair.txt").toString();
		wordPairsFileName = wordPairsFileName.substring(wordPairsFileName.indexOf(":")+1);
		featureWordPairs = initRules(wordPairsFileName, this.NUM_WORD_PAIRS);
	}

	private Map<String, Double> initRules(String fileName, int maxSize) throws IOException {
		Map<String, Double> result = new HashMap<String, Double>();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(fileName), Util.ENCODING))) {
			String line;
			while ((line = reader.readLine()) != null && result.size() < maxSize) {
				String[] tokens = line.split("\\s+");
				int n = tokens.length;

				String rule = tokens[0];
				if (tokens[n - 1] != null) {
					Double value = Double.parseDouble(tokens[n - 1]);
					if (!result.containsKey(rule)) {
						result.put(rule, value);
					}
				}
			}

		}

		return result;
	}

	@Override
	public CompOutput parseAnyText(String modelName, String inputText, SynParseData synPData, ModelsResource modelsResource)
			throws IOException {
		this.trees = new ArrayList<Tree>();
		this.trees.add(synPData.getPennTree());
		
		List<String[]> features = generateFeatures(inputText, synPData, null);
		
		CompOutput noExpOutput = MaxEntClassifier.predict(features, modelName, modelsResource);
		ArrayList<String> outputList = ((NoExpCompOutput) noExpOutput).getOutputList(); 
		
		ArrayList<String> finalOutPut = new ArrayList<String>();

		int counter = 0;
		for (String[] feature : features) {
				String[] tmp = outputList.get(counter).split("\\s+");
				String[] cols = feature[1].split("\\|", -1);
				String fullSense = Corpus.getFullSense(tmp[tmp.length - 1]);
				if (fullSense.equals("EntRel") || fullSense.equals("AltLex")) {
					feature[1] = feature[1].replaceAll("Implicit\\|", fullSense + "|");
					finalOutPut.add(feature[1]);
				} else {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 48; ++i) {
						sb.append((i == 11) ? fullSense : cols[i]);
						sb.append("|");
					}
					sb.deleteCharAt(sb.length() - 1);
					finalOutPut.add(sb.toString());
				}
				counter++;
			}

		((NoExpCompOutput) noExpOutput).setFinalList(finalOutPut);
		
		return noExpOutput;
	}

	@Override
	protected List<String[]> generateFeatures(String inputText,
			SynParseData synParseData, List<String> explicitSpans)
					throws IOException {
		List<String[]> features = new ArrayList<>();
		int[] paragraphs = null;
		List<String> relations = null;
		
		this.orgText = inputText;
			
			if (dtreeMap == null) {
				buildDependencyTrees(synParseData);
			}
			paragraphs = new int[1];//Corpus.getParagraphs(article, Corpus.getSpanMapAsList(article, featureType), featureType);
			relations = getEpRelations();

		for (int i = 0; i < paragraphs.length; ++i) {
			int index = paragraphs[i];
			int limit = ((i + 1) < paragraphs.length ? paragraphs[i + 1] : trees.size()) - 1;
			for (; index < limit; ++index) {
				// log.trace("Parsing paragraph " + index + " out of " +
				// paragraphs[paragraphs.length - 1]);
				int senIdx1 = index;
				int senIdx2 = index + 1;
				String expRel = findRelation(relations, "Exp", senIdx1, senIdx2);

				if (expRel == null) {
					String nonExpRel = null;

					StringBuilder feature = new StringBuilder();
					Tree arg1Tree = trees.get(senIdx1);
					Tree arg2Tree = trees.get(senIdx2);

					List<TreeNode> arg1 = new ArrayList<>();
					arg1.add(new TreeNode(arg1Tree.firstChild(), arg1Tree.firstChild(), senIdx1));

					List<TreeNode> arg2 = new ArrayList<>();
					arg2.add(new TreeNode(arg2Tree.firstChild(), arg2Tree.firstChild(), senIdx2));

					String productionRules = printProductionRules(arg1, arg2);
					feature.append(productionRules);

					String dependencyRules = printDependencyRules(arg1, arg2);
					feature.append(dependencyRules);

					String wordPairs = printWordPairs(arg1, arg2);
					feature.append(wordPairs);
					String arg2Word = printArg2Word(arg1, arg2);
					feature.append(arg2Word);

					feature.append("xxx");
					
					nonExpRel = genNonExpRel(arg1Tree, arg2Tree, senIdx1, senIdx2);
					
					features.add(new String[] { feature.toString(), nonExpRel });
				}
			}
		}

		return features;
	}

	private String genNonExpRel(Tree arg1Tree, Tree arg2Tree, int senIdx1, int senIdx2) {
		List<Tree> arg1Pos = new ArrayList<>();
		getAllPosNodes(arg1Tree, arg1Pos);
		List<Tree> arg2Pos = new ArrayList<>();
		getAllPosNodes(arg2Tree, arg2Pos);

		String arg1Text = Corpus.nodesToString(arg1Pos);
		String arg2Text = Corpus.nodesToString(arg2Pos);

		String arg1Span = Corpus.continuousTextToSpan(arg1Text, orgText);
		String arg2Span = Corpus.continuousTextToSpan(arg2Text, orgText);

		StringBuilder sb = new StringBuilder();

		if (corpus.equals(Type.PDTB)) {
			for (int i = 0; i < 48; ++i) {
				if (i == 0) {
					sb.append("Implicit");
				}
				if (i == 22) {
					sb.append(arg1Span);
				}
				if (i == 23) {
					sb.append(senIdx1);
				}
				if (i == 24) {
					sb.append(arg1Text);
				}
				if (i == 32) {
					sb.append(arg2Span);
				}
				if (i == 33) {
					sb.append(senIdx2);
				}
				if (i == 34) {
					sb.append(arg2Text);
				}
				sb.append("|");
			}
		} else {
			for (int i = 0; i < 27; ++i) {
				if (i == 0) {
					sb.append("Implicit");
				}
				if (i == 14) {
					sb.append(arg1Span);
				}
				if (i == 20) {
					sb.append(arg2Span);
				}
				sb.append("|");
			}
		}
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	private List<String> getEpRelations(){
		List<String> result = new ArrayList<>();
		ArrayList<String> pipes = this.expCompResult.getPipeOut();
		for (String pipe : pipes) {
			if (pipe.length() > 0) {
				String[] rel = pipe.split("\\|", -1);
				int pipeLength = 48;
				if (rel.length != pipeLength) {
					System.err.println("Invalid EP arg_ext relations. Column size should be " + pipeLength + " but it was "
							+ rel.length + " in article");
				}
				result.add(pipe);
			}
		}

		return result;
	}

	private String printWordPairs(List<TreeNode> arg1TreeNodes, List<TreeNode> arg2TreeNodes) {
		String arg1 = treeNodeToText(arg1TreeNodes);
		String arg2 = treeNodeToText(arg2TreeNodes);

		return printWordPairs(arg1, arg2);
	}

	private String findRelation(List<String> relations, String type, int senId1, int senId2) throws IOException {

		for (String rel : relations) {
			if ((type.equals("Exp") && rel.startsWith("Explicit"))
					|| (type.equals("NonExp") && !rel.startsWith("Explicit"))) {

				String[] cols = rel.split("\\|", -1);
				String arg1Gorn = "";
				String arg2Gorn = "";

				if (corpus.equals(Type.PDTB)) {
					String[] tmp = cols[23].split(";");
					arg1Gorn = tmp[tmp.length - 1].split(",")[0].trim();
					arg2Gorn = cols[33].split(";")[0].split(",")[0].trim();
				} else {
					LinkedList<Integer> arg1s = spanToSenId.get(cols[14]);
					if (arg1s != null && arg1s.size() > 0) {
						arg1Gorn = arg1s.getLast().toString();
					}
					LinkedList<Integer> arg2s = spanToSenId.get(cols[20]);
					if (arg2s != null && arg2s.size() > 0) {
						arg2Gorn = arg2s.getFirst().toString();
					}
				}

				if (arg1Gorn.length() > 0 && arg2Gorn.length() > 0) {
					int sen1 = Integer.parseInt(arg1Gorn);
					int sen2 = Integer.parseInt(arg2Gorn);
					if (sen1 == senId1 && sen2 == senId2) {
						return rel;
					}
				}
			}
		}
		return null;
	}

	private String printProductionRules(List<TreeNode> arg1, List<TreeNode> arg2) {
		StringBuilder line = new StringBuilder();
		HashMap<String, Integer> pRules1 = new HashMap<String, Integer>();
		for (TreeNode tree : arg1) {
			getProductionRules(tree.getNode(), pRules1);
		}

		HashMap<String, Integer> pRules2 = new HashMap<String, Integer>();
		for (TreeNode tree : arg2) {
			getProductionRules(tree.getNode(), pRules2);
		}

		HashSet<String> pRules = new HashSet<String>();
		pRules.addAll(pRules1.keySet());
		pRules.addAll(pRules2.keySet());

		LinkedList<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(featureProdRules.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return Double.compare(o2.getValue(), o1.getValue());
			}
		});

		for (Entry<String, Double> item : list) {
			String key = item.getKey();

			if (pRules.contains(key)) {
				boolean a1 = pRules1.containsKey(key);
				boolean a2 = pRules2.containsKey(key);

				if (a1) {
					line.append(key + ":1 ");
				}

				if (a2) {
					line.append(key + ":2 ");
				}

				if (a1 && a2) {
					line.append(key + ":12 ");
				}
			}
		}

		return line.toString();
	}

	public static void getProductionRules(Tree tree, HashMap<String, Integer> prodRules) {
		String[] tr = tree.value().replaceAll("=[0-9]+", "").split("-");
		String value = tr.length > 0 ? tr[0] : tree.value();
		if (tree.isLeaf() || tree.value().startsWith("-")) {
			value = tree.value();
		}
		StringBuilder rule = new StringBuilder(value);
		rule.append(" ->");
		for (Tree child : tree.children()) {
			if (!child.value().equalsIgnoreCase("-NONE-")) {
				String[] tmp = child.value().replaceAll("=[0-9]+", "").split("-");
				String cVal = tmp.length > 0 ? tmp[0] : child.value();
				if (child.isLeaf() || child.value().startsWith("-")) {
					cVal = child.value();
				}
				rule.append(" ");
				rule.append(cVal);
			}
		}
		String key = rule.toString().replaceAll("\\s+", "_");

		// has some child values
		if (!key.endsWith("->")) {
			Integer count = prodRules.get(key);
			if (count == null) {
				count = 0;
			}
			++count;
			prodRules.put(key, count);
		}

		for (Tree child : tree.children()) {
			if (!child.value().equalsIgnoreCase("-NONE-")) {
				getProductionRules(child, prodRules);
			}
		}

	}

	private String printWordPairs(String arg1, String arg2) {
		StringBuilder line = new StringBuilder();
		arg1 = arg1.toLowerCase();
		arg2 = arg2.toLowerCase();

		arg1 = tokenize(arg1);
		arg2 = tokenize(arg2);

		String[] text1 = arg1.split("\\s+");
		String[] text2 = arg2.split("\\s+");

		for (int i = 0; i < text1.length; ++i) {
			text1[i] = stem(text1[i]);
		}

		for (int i = 0; i < text2.length; ++i) {
			text2[i] = stem(text2[i]);
		}
		Set<String> pairs = new HashSet<String>();
		for (String w1 : text1) {
			for (String w2 : text2) {
				String pair = w1 + "_" + w2;
				pairs.add(pair);
			}
		}

		List<String> list = new LinkedList<String>(pairs);
		Collections.sort(list);

		for (String pair : list) {
			if (featureWordPairs.containsKey(pair)) {
				line.append(pair + " ");
			}
		}
		return line.toString();
	}

	private String printDependencyRules(List<TreeNode> arg1, List<TreeNode> arg2) {
		StringBuilder line = new StringBuilder();

		HashMap<String, Integer> depRules1 = new HashMap<String, Integer>();
		for (TreeNode pair : arg1) {
			getDependencyRules(pair, dtreeMap, depRules1);
		}

		HashMap<String, Integer> depRules2 = new HashMap<String, Integer>();
		for (TreeNode pair : arg2) {
			getDependencyRules(pair, dtreeMap, depRules2);
		}

		HashSet<String> depRules = new HashSet<String>();
		depRules.addAll(depRules1.keySet());
		depRules.addAll(depRules2.keySet());

		LinkedList<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(featureDepRules.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				int r = Double.compare(o2.getValue(), o1.getValue());
				if (r == 0) {
					r = o1.getKey().compareTo(o2.getKey());
				}
				return r;
			}
		});

		for (Entry<String, Double> item : list) {
			String key = item.getKey();

			if (depRules.contains(key)) {
				boolean a1 = depRules1.containsKey(key);
				boolean a2 = depRules2.containsKey(key);

				if (a1) {
					line.append(key + ":1 ");
				}

				if (a2) {
					line.append(key + ":2 ");
				}

				if (a1 && a2) {
					line.append(key + ":12 ");
				}
			}
		}
		return line.toString();
	}

	public static void getDependencyRules(TreeNode treeNode, HashMap<String, Dependency> dtreeMap,
			HashMap<String, Integer> depRules) {

		Tree root = treeNode.getRoot();
		Tree node = treeNode.getNode();
		int treeNumber = treeNode.getTreeNumber();

		List<Tree> allPosNodes = new ArrayList<Tree>();
		getAllPosNodes(node, allPosNodes);

		List<Integer> leafNodes = new ArrayList<Integer>();
		for (Tree pn : allPosNodes) {
			leafNodes.add(pn.firstChild().nodeNumber(root));
		}

		for (int i = 0; i < leafNodes.size(); ++i) {
			Tree n = root.getNodeNumber(leafNodes.get(i));
			int nInd = n.nodeNumber(root);
			Dependency nDepRel = dtreeMap.get(treeNumber + " : " + nInd);
			if (nDepRel != null) {
				List<Tree> nDependents = new ArrayList<Tree>();
				for (Integer ind : nDepRel.getDependents()) {
					if (leafNodes.contains(ind)) {
						Tree d = root.getNodeNumber(ind);
						nDependents.add(d);
					}
				}

				if (nDependents.size() > 0) { // has nDependents

					String rule = n.value() + " <- ";

					for (Tree n1 : nDependents) {
						int n1Ind = n1.nodeNumber(root);
						Dependency n1DepRel = dtreeMap.get(treeNumber + " : " + n1Ind);
						String dependency = n1DepRel.getLabel();
						rule += "<" + dependency + "> ";
					}
					rule = rule.trim();

					if (!rule.endsWith("<-")) { // rule has some child values
						rule = rule.replace(' ', '_');
						Integer value = depRules.containsKey(rule) ? depRules.get(rule) : 0;
						value += 1;
						depRules.put(rule, value);
					}
				}
			}
		}
	}

	private String printArg2Word(List<TreeNode> arg1, List<TreeNode> arg2TreeNodes) {
		String arg2 = treeNodeToText(arg2TreeNodes);
		return printArg2Word(arg2);
	}

	private String printArg2Word(String arg2) {
		StringBuilder line = new StringBuilder();
		String[] ary = arg2.toLowerCase().split("\\s+");
		for (int i = 0; i < PHRASE_LENGTH && i < ary.length; ++i) {
			line.append("arg2_start_uni_" + ary[i] + " ");
		}

		return line.toString();
	}
	protected void buildDependencyTrees(SynParseData synPData) throws IOException {
		dtreeMap = new HashMap<String, Dependency>();
		ArrayList<String> dtreeTexts = synPData.getDepList();

		if (dtreeTexts.size() != trees.size()) {
//			logDiffTreeCountErr(dtreeTexts.size(), article, featureType);
			System.err.println("Number of dependency trees and syntactic trees do not match !!!");
		}

		for (int i = 0; i < dtreeTexts.size(); i++) {
			String dtreeText = dtreeTexts.get(i);

			if (dtreeText.isEmpty() || trees.get(i).children().length == 0) {
				continue;
			}

			Tree tree = trees.get(i).getChild(0);
			List<Tree> allPosNodes = new ArrayList<Tree>();
			getAllPosNodes(tree, allPosNodes);
			String[] rels = dtreeText.split("\n");

			// if there is no dependency tree for the parse tree
			if (rels.length == 0 || (rels.length == 1 && rels[0].isEmpty())) {
				continue;
			}

			for (String tmp : rels) {
				if (tmp.equals("_nil_") || tmp.startsWith("root") || tmp.isEmpty()) {
					continue;
				}
				int ind1 = tmp.indexOf('(');
				int ind2 = tmp.lastIndexOf(')');
				int split = tmp.indexOf(", ");

				String label = tmp.substring(0, ind1);

				String tmp1 = tmp.substring(ind1 + 1, split);
				String w1 = tmp1.substring(tmp1.lastIndexOf('-') + 1);

				String tmp2 = tmp.substring(split + 2, ind2);
				String w2 = tmp2.substring(tmp2.lastIndexOf('-') + 1);

				w1 = w1.replaceAll("[^\\d]", "");
				w2 = w2.replaceAll("[^\\d]", "");

				int pInd = Integer.parseInt(w1) - 1;
				int cInd = Integer.parseInt(w2) - 1;

				Tree p = allPosNodes.get(pInd).firstChild();
				Tree c = allPosNodes.get(cInd).firstChild();

				pInd = p.nodeNumber(tree);
				cInd = c.nodeNumber(tree);

				Dependency pDep = dtreeMap.get(i + " : " + pInd);
				if (pDep == null) {
					pDep = new Dependency(p);
				}

				Dependency cDep = dtreeMap.get(i + " : " + cInd);
				if (cDep == null) {
					cDep = new Dependency(c);
				}

				pDep.addDependents(cInd);
				cDep.setDependsOn(p);
				cDep.setLabel(label);

				dtreeMap.put(i + " : " + cInd, cDep);
				dtreeMap.put(i + " : " + pInd, pDep);
			}
		}
	}

	public static void getAllPosNodes(Tree node, List<Tree> allPosNodes) {
		boolean isPos = node.numChildren() == 1 && node.firstChild().isLeaf();
		if (!isPos) {
			for (Tree child : node.children()) {
				getAllPosNodes(child, allPosNodes);
			}
		} else {
			if (!node.value().equalsIgnoreCase("-NONE-")) {
				allPosNodes.add(node);
			}
		}
	}

	/**
	 * Prepare text to be split into tokens according the Penn Treebank
	 * tokenization rules. After calling this method call
	 * {@code text.split("\\s+")} to get the actual tokens.
	 * 
	 * <p>
	 * Based upon the sed script written by Robert MacIntyre at
	 * http://www.cis.upenn.edu/~treebank/tokenizer.sed .
	 * </p>
	 * 
	 * @param text,
	 *            string to be prepared
	 * @return string ready to be split into tokens.
	 */
	public static String tokenize(String text) {
		text = text.toLowerCase();

		text = text.replaceAll("^\"", "`` ");
		text = text.replaceAll("``", " `` ");
		text = text.replaceAll("([ \\(\\[{<])\"", "$1 `` ");
		text = text.replaceAll("\\.\\.\\.", " ... ");
		text = text.replaceAll("[,;:@#\\$%&]", " $0 ");
		text = text.replaceAll("([^.])([.])([\\])}>\"']*)[ \t]*$", "$1 $2$3 ");
		text = text.replaceAll("[?!]", " $0 ");
		text = text.replaceAll("[\\]\\[\\(\\){}\\<\\>]", " $0 ");
		text = text.replaceAll("--", " -- ");
		text = text.replaceAll("$", " ");
		text = text.replaceAll("^", " ");
		text = text.replaceAll("\"", " '' ");
		text = text.replaceAll("''", " '' ");
		text = text.replaceAll("([^'])' ", "$1 ' ");
		text = text.replaceAll("'([sSmMdD]) ", " '$1 ");
		text = text.replaceAll("'ll ", " 'll ");
		text = text.replaceAll("'re ", " 're ");
		text = text.replaceAll("'ve ", " 've ");
		text = text.replaceAll("n't ", " n't ");
		text = text.replaceAll("'LL ", " 'LL ");
		text = text.replaceAll("'RE ", " 'RE ");
		text = text.replaceAll("'VE ", " 'VE ");
		text = text.replaceAll("N'T ", " N'T ");
		text = text.replaceAll(" ([Cc])annot ", " $1an not ");
		text = text.replaceAll(" ([Dd])'ye ", " $1' ye ");
		text = text.replaceAll(" ([Gg])imme ", " $1im me ");
		text = text.replaceAll(" ([Gg])onna ", " $1on na ");
		text = text.replaceAll(" ([Gg])otta ", " $1ot ta ");
		text = text.replaceAll(" ([Ll])emme ", " $1em me ");
		text = text.replaceAll(" ([Mm])ore'n ", " $1ore 'n ");
		text = text.replaceAll(" '([Tt])is ", " '$1 is ");
		text = text.replaceAll(" '([Tt])was ", " '$1 was ");
		text = text.replaceAll(" ([Ww])anna ", " $1an na ");
		text = text.replaceAll("  *", " ");
		text = text.replaceAll("^ *", "");

		return text;
	}

	public static String stem(String text) {
		Stemmer st = new Stemmer();
		char[] ar = text.toLowerCase().toCharArray();
		st.add(ar, ar.length);
		st.stem();
		String result = st.toString();
		return result;
	}

	private String treeNodeToText(List<TreeNode> arg1TreeNodes) {
		StringBuilder sb = new StringBuilder();
		for (TreeNode node : arg1TreeNodes) {
			List<Tree> posNodes = new ArrayList<>();
			getAllPosNodes(node.getRoot(), posNodes);
			String sent = Corpus.nodesToString(posNodes);
			sb.append(sent);
		}

		return sb.toString();
	}

	protected HashMap<String, Dependency> getDtreeMap() {
		return this.dtreeMap;
	}

}
