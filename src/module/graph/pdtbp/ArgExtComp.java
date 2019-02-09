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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;
import module.graph.pdtbp.resources.ModelsResource;
import module.graph.pdtbp.resources.Node;
import module.graph.pdtbp.utils.ArgPosOutput;
import module.graph.pdtbp.utils.CompOutput;
import module.graph.pdtbp.utils.Corpus;
import module.graph.pdtbp.utils.ArgExtOutput;
import module.graph.pdtbp.utils.ConCompOutput;
import module.graph.pdtbp.utils.MaxEntClassifier;
import module.graph.pdtbp.utils.SynParseData;

/**
 * 
 * @author ilija.ilievski@u.nus.edu
 *
 */
public class ArgExtComp extends Component {

	public final String NAME = "argext";

	private int majorIndex = 0;
	private int doneSoFar = 0;
	private List<String> labels;
	private List<Tree> trees;
	private ConCompOutput conCompResult = null;
	private ArgPosOutput argPosResult = null;
	private Map<String,String> spanHashMap = null;
	private String orgText = null;

	public final String[] PUNC_TAGS = { "#", "$", "``", "''", "-LRB-", "-RRB-", ",", ".", ":" };

	private final String[] Subordinator = { "after", "although", "as", "as if", "as long as", "as soon as",
			"as though", "because", "before", "before and after", "for", "however", "if", "if and when", "insofar as",
			"lest", "much as", "now that", "once", "since", "so", "so that", "though", "till", "unless", "until",
			"when", "when and if", "while" };

	private final String[] Coordinator = { "and", "but", "else", "if..then", "neither..nor", "nor",
			"on the one hand..on the other hand", "or", "plus", "then", "yet" };

	private final String[] Adverbial = { "accordingly", "additionally", "afterward", "also", "alternatively",
			"as a result", "as an alternative", "as well", "besides", "by comparison", "by contrast", "by then",
			"consequently", "conversely", "earlier", "either..or", "except", "finally", "for example", "for instance",
			"further", "furthermore", "hence", "in addition", "in contrast", "in fact", "in other words",
			"in particular", "in short", "in sum", "in the end", "in turn", "indeed", "instead", "later", "likewise",
			"meantime", "meanwhile", "moreover", "nevertheless", "next", "nonetheless", "on the contrary",
			"on the other hand", "otherwise", "overall", "previously", "rather", "regardless", "separately",
			"similarly", "simultaneously", "specifically", "still", "thereafter", "thereby", "therefore", "thus",
			"ultimately", "whereas" };

	/**
	 * 
	 */
	public ArgExtComp(ConCompOutput conCompResult, ArgPosOutput argPosResult) {
		this.conCompResult = conCompResult;
		this.argPosResult = argPosResult;
	}


	@Override
	public CompOutput parseAnyText(String modelName, String inputText,SynParseData synPData, ModelsResource modelsResource)
			throws IOException {
		this.trees = new ArrayList<Tree>();
		this.trees.add(synPData.getPennTree());
		this.orgText = inputText;
		this.labels = getArgPosLabels(this.argPosResult.getOutput());
		List<String> explicitSpans = Corpus.genExplicitSpans(this.conCompResult);
		ArrayList<String> argExtAux = new ArrayList<String>();
		List<String[]> features = generateFeatures(inputText, synPData, explicitSpans);
		for (String[] feature : features) {
			argExtAux.add(feature[2]);
		}

		CompOutput argExtOut = MaxEntClassifier.predict(features,modelName,modelsResource);

		ArrayList<String> pipeOut = new ArrayList<String>();
		pipeOut.addAll(printAnyTxtPsArgs(explicitSpans));
		pipeOut.addAll(printAnyTxtSsArgs(explicitSpans, argExtOut, argExtAux, features));

		((ArgExtOutput) argExtOut).setPipeOutList(pipeOut);

		return argExtOut;
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

	/* (non-Javadoc)
	 * @see module.graph.pdtbp.Component#generateFeatures(java.lang.String, edu.stanford.nlp.trees.Tree, java.util.Map, java.util.HashSet)
	 */
	@Override
	protected List<String[]> generateFeatures(String inputText, SynParseData synPData, List<String> expSpans)
			throws IOException {
		List<String[]> features = new ArrayList<>();

		ArrayList<String> spanArray = null;
		List<String> explicitSpans = new ArrayList<String>(expSpans);

		spanArray = synPData.getSpanList();
		this.spanHashMap = getSpanMap(spanArray);

		int index = 0;
		int contIndex = 0;
		for (String rel : explicitSpans) {

			String[] cols = rel.split("\\|", -1);

			String argPos = null;

			argPos = labels.get(majorIndex);


			if (argPos.equals("FS")) {
				continue;
			}

			argPos = labels.get(majorIndex);

			++majorIndex;

			if (argPos.equals("SS")) {
				Set<Integer> done = new HashSet<>();

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
						// if (span[0].equals(canSpan[0]) || nodes.size() > 0) {
						boolean flag = span[0].equals(canSpan[0]) || (nodes.size() > 0 && spans.length == 1
								&& Integer.parseInt(canSpan[1]) <= Integer.parseInt(span[1]));
						if (flag) {
							if (nodes.size() == 0) {
								contIndex = index;
							}
							root = synPData.getPennTree();//trees.get(Integer.parseInt(spanCols[1]));
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
							if (!done.contains(nodeNum)) {
								done.add(nodeNum);
								nodes.add(new Node(node, nodeNum));
							}
							if (span[1].equals(canSpan[1])) {
								++index;
								break;
							}
						}
					}
				}
				if (!nodes.isEmpty()) {
					String connStr = null;
					String connCat = null;

					connStr = cols[8].trim().replace(' ', '_');
					String c = cols[5].substring(0, 1);
					if (c.toLowerCase().equals(connStr.substring(0, 1))) {
						connStr = c + connStr.substring(1);
					}

					connCat = findCategory(cols[8]);


					Tree connNode = nodes.get(nodes.size() - 1).tree.parent(root);

					Tree[] argNodes = getArgNodes(root, cols, spanArray, connCat, connNode);
					List<Tree> internal = getInternalNodes(root, root);

					String treeNum;
					String line;
					int total = (doneSoFar + features.size());
					treeNum = cols[7];
					line = "test.txt" + ":" + total + "-" + (total + internal.size()) + ":Arg1(" + cols[22]
							+ "):Arg2(" + cols[32] + "):" + cols[3];

					for (Tree node : internal) {
						String label = "";
						if (node.equals(argNodes[0])) {
							label = "arg1_node";
						} else if (node.equals(argNodes[1])) {
							label = "arg2_node";
						} else {
							label = "none";
						}

						label = treeNum + ":" + node.nodeNumber(root);

						String feature = printFeature(root, node, connStr, connCat, connNode, label);
						features.add(new String[] { feature, rel, line });
					}
				}
			}
		}

		return features;
	}

	private String findCategory(String connStr) {
		if (connStr == null) {
			return null;
		}

		String key = connStr.toLowerCase();
		if (key.equals("either or")) {
			key = "either..or";
		}

		String value = "Subordinator";

		for (String can : Subordinator) {
			if (key.equals(can)) {
				return value;
			}
		}

		value = "Conj-adverb";
		for (String can : Adverbial) {
			if (key.equals(can)) {
				return value;
			}
		}

		value = "Coordinator";
		key = key.replaceAll("\\s+", "..");
		for (String can : Coordinator) {
			if (key.equals(can)) {
				return value;
			}
		}
		return "null";
	}

	private List<String> getArgPosLabels(ArrayList<String> argPosResult) throws IOException {
		List<String> list = new ArrayList<>();

		for(String str : argPosResult){
			String[] tmp = str.split("\\s+");
			list.add(tmp[tmp.length - 1]);
		}

		return list;
	}

	private String printFeature(Tree root, Tree node, String connStr, String connCat, Tree connNode, String label) {
		StringBuilder feature = new StringBuilder();

		feature.append("conn:");
		feature.append(connStr);
		feature.append(' ');

		feature.append("conn_lc:");
		feature.append(connStr.toLowerCase());
		feature.append(' ');

		feature.append("conn_cat:");
		feature.append(connCat);
		feature.append(' ');

		String path = findPath(root, connNode, node);
		feature.append("conn_to_node:");
		feature.append(path);
		feature.append(' ');

		Tree parent = connNode.parent(root);
		Tree[] children = parent.children();

		int lsibs = 0;
		int rsibs = 0;
		boolean countLeft = true;
		for (int i = 0; i < children.length; ++i) {
			if (children[i].equals(connNode)) {
				countLeft = false;
				continue;
			}
			if (countLeft) {
				++lsibs;
			} else {
				++rsibs;
			}
		}

		feature.append("conn_node_lsib_size=");
		feature.append(lsibs);
		feature.append(' ');

		feature.append("conn_node_rsib_size=");
		feature.append(rsibs);
		feature.append(' ');
		if (lsibs > 1) {
			feature.append("conn_to_node:");
			feature.append(path);
			feature.append("^conn_node_lsib_size:>1");
			feature.append(' ');
		}

		int relpos = relativePosition(root, connNode, node);
		feature.append("conn_to_node_relpos:");
		feature.append(relpos);
		feature.append(' ');

		feature.append(label);

		return feature.toString();
	}

	/**
	 * <pre>
	 *     # 0:  node1 and node2 in the same path to root
	 *     # 1:  node2 is at the rhs of node1's path to root
	 *     # -1: node2 is at the lhs of node1's path to root
	 * </pre>
	 * 
	 * @param root
	 * @param connNode
	 * @param node
	 * @return
	 */
	private int relativePosition(Tree root, Tree connNode, Tree node) {
		Tree curr = connNode;
		while (curr != null && !curr.equals(root)) {
			if (curr.equals(node)) {
				return 0;
			}
			Tree parent = curr.parent(root);
			Tree[] children = parent.children();

			for (int i = 0; i < children.length; ++i) {
				if (children[i].contains(node)) {
					int nodeNum = node.nodeNumber(root);
					int connNum = connNode.nodeNumber(root);

					if (nodeNum < connNum) {
						return -1;
					} else {
						return 1;
					}
				}
			}

			curr = parent;
		}

		return 0;
	}

	private String findPath(Tree root, Tree connNode, Tree node) {
		Tree lca = getLCA(root, connNode, node);

		List<String> n1ToLca = findUpwardPath(root, connNode, lca);
		List<String> n2ToLca = findUpwardPath(root, node, lca);

		if (!n2ToLca.isEmpty() && n2ToLca.get(n2ToLca.size() - 1) != null && !n1ToLca.isEmpty()
				&& n1ToLca.get(n1ToLca.size() - 1).equals(n2ToLca.get(n2ToLca.size() - 1))) {
			n2ToLca.remove(n2ToLca.size() - 1);
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n1ToLca.size(); ++i) {
			sb.append(n1ToLca.get(i));
			if (i < n1ToLca.size() - 1) {
				sb.append("->");
			}
		}

		for (int i = n2ToLca.size() - 1; i > -1; --i) {
			sb.append("<-");
			sb.append(n2ToLca.get(i));
		}

		return sb.toString();
	}

	private Tree getLCA(Tree root, Tree connNode, Tree node) {
		List<Tree> nodes = new ArrayList<>();
		nodes.add(connNode);
		nodes.add(node);

		return getLCA(root, nodes);
	}

	private List<String> findUpwardPath(Tree root, Tree connNode, Tree lca) {
		List<String> path = new ArrayList<>();
		if (connNode == null || lca == null) {// || connNode.equals(lca)) {
			return path;
		}

		Tree curr = connNode;
		while (curr != null && !curr.equals(lca)) {
			String val = curr.value();
			if (val != null) {
				int t = val.indexOf("-");
				if (t > 0) {
					val = val.substring(0, t);
				}
				path.add(val);
			}
			curr = curr.parent(root);
		}

		if (curr != null && curr.equals(lca)) {
			String val = curr.value();
			if (val != null) {
				int t = val.indexOf("-");
				if (t > 0) {
					val = val.substring(0, t);
				}
				path.add(val);
			}
		}

		if (curr == null && path.isEmpty()) {
			return new ArrayList<>();
		} else {
			return path;
		}
	}

	private Tree[] getArgNodes(Tree root, String[] cols, ArrayList<String> spanArray, String connCat, Tree connNode) {
		List<Tree> arg1Nodes = getTreeNodesFromSpan(cols[22], spanArray);
		arg1Nodes.addAll(getTreeNodesFromSpan(cols[29], spanArray));

		if (connCat.equals("Coordinator") && connNode.value().equals("CC")) {
			Tree[] children = connNode.parent(root).children();
			for (Tree child : children) {
				int ind = arg1Nodes.indexOf(child);
				if (ind == -1 && isPuncTag(child.value())) {
					arg1Nodes.add(child);
				}
			}
		}

		Tree arg1Node = (arg1Nodes.size() == 1) ? arg1Nodes.get(0) : getLCA(root, arg1Nodes);

		List<Tree> arg2Nodes = getTreeNodesFromSpan(cols[32], spanArray);
		arg2Nodes.addAll(getTreeNodesFromSpan(cols[39], spanArray));

		Tree arg2Node = (arg2Nodes.size() == 1) ? arg2Nodes.get(0) : getLCA(root, arg2Nodes);

		return new Tree[] { arg1Node, arg2Node };
	}

	private List<Tree> getTreeNodesFromSpan(String column, ArrayList<String> spanArray) {
		List<Tree> nodes = new ArrayList<>();
		if (column.isEmpty()) {
			return nodes;
		}
		String[] spans = column.split(";");
		for (String span : spans) {
			if (span.length() > 0) {
				String[] tmp = span.split("\\.\\.");
				int begin = Integer.parseInt(tmp[0]);
				int end = Integer.parseInt(tmp[1]);
				for (String line : spanArray) {
					// wsj_1371,0,6,9..21,Shareholders
					String[] tt = line.split(",");
					tmp = tt[3].split("\\.\\.");
					int b = Integer.parseInt(tmp[0]);
					int e = Integer.parseInt(tmp[1]);
					if (begin <= b && e <= end) {
						int nodeNum = Integer.parseInt(tt[2]);
						int treeNum = Integer.parseInt(tt[1]);
						nodes.add(trees.get(treeNum).getNodeNumber(nodeNum));
					}
				}
			}
		}

		return nodes;
	}

	private boolean isPuncTag(String value) {

		for (String punc : this.PUNC_TAGS) {
			if (punc.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public Tree getLCA(Tree root, List<Tree> nodes) {

		Tree lca = null;
		Queue<Tree> queue = new LinkedList<>();
		queue.add(root);

		while (queue.size() > 0) {
			Tree curr = queue.remove();
			Set<Tree> allNodes = getAllNodes(curr);
			boolean contains = true;

			for (Tree node : nodes) {
				contains &= allNodes.contains(node);
				if (!contains) {
					break;
				}
			}

			if (contains) {
				lca = curr;
				Tree[] children = curr.children();
				for (Tree child : children) {
					queue.add(child);
				}
			}
		}

		return lca;
	}

	private Set<Tree> getAllNodes(Tree curr) {
		Set<Tree> set = new HashSet<>();

		if (curr != null) {
			set.add(curr);
			Tree[] children = curr.children();
			for (Tree child : children) {
				set.addAll(getAllNodes(child));
			}
		}

		return set;
	}

	private List<Tree> getInternalNodes(Tree root, Tree node) {
		// @child_nodes.size == 1 and @child_nodes.first.class != Node

		List<Tree> result = new ArrayList<>();

		if (node != null && !(node.children().length == 1 && node.firstChild() != null && node.firstChild().isLeaf())) {
			Tree parent = node.parent(root);
			if (parent != null && !node.value().equals("-NONE-")) {
				result.add(node);
			}
			Tree[] children = node.children();
			for (Tree child : children) {
				result.addAll(getInternalNodes(root, child));
			}
		}

		return result;
	}


	class Span implements Comparable<Span> {
		int start;
		int end;

		@Override
		public int compareTo(Span o) {
			return start - o.start;
		}

		Span(String span) {
			String[] tmp = span.split("\\.\\.");
			start = Integer.parseInt(tmp[0]);
			end = Integer.parseInt(tmp[1]);
		}

		@Override
		public String toString() {
			return start + ".." + end;
		}
	}

	private ArrayList<String> printAnyTxtPsArgs(List<String> explicitSpans) throws IOException {
		ArrayList<String> result = new ArrayList<String>();

		majorIndex = 0;
		for (String rel : explicitSpans) {
			String label = labels.get(majorIndex);
			majorIndex++;
			if (label.equals("PS")) {
				String[] cols = rel.split("\\|", -1);
				String[] args = getAnyPSArgSpans(rel, this.spanHashMap);

				StringBuilder resultLine = new StringBuilder();
				int sentNumber = -1;
				for (int i = 0; i < cols.length; i++) {
					String col = cols[i];
					if (i == 7) {
						sentNumber = Integer.parseInt(col);
					}

					if (i == 22) {
						resultLine.append(args[0] + "|");
					} else if (i == 23) {
						resultLine.append((sentNumber - 1) + "|");
					} else if (i == 24) {
						resultLine.append(args[2] + "|");

					} else if (i == 32) {
						resultLine.append(args[1] + "|");
					} else if (i == 33) {
						resultLine.append(sentNumber + "|");
					} else if (i == 34) {
						resultLine.append(args[3] + "|");
					} else {
						resultLine.append(col + "|");
					}
				}
				resultLine.deleteCharAt(resultLine.length() - 1);

				result.add(resultLine.toString());
			}
		}

		return result;
	}

	private String[] getAnyPSArgSpans(String pipe, Map<String, String> spanHashMap) {
		String[] cols = pipe.split("\\|", -1);
		String connSpan = cols[3];
		int arg2TreeNum = Integer.parseInt(cols[7]);

		Tree arg2Root = trees.get(arg2TreeNum);
		String arg2 = (arg2TreeNum) + ":" + arg2Root.firstChild().nodeNumber(arg2Root);
		ArrayList<String> arg2Nodes = new ArrayList<String>();
		arg2Nodes.add(arg2);

		String arg2Span = calcNodesSpan(arg2Nodes, spanHashMap, connSpan, null);
		String arg1Span = "1..2";

		if (arg2TreeNum > 0) {
			int arg1TreeNum = arg2TreeNum - 1;
			Tree arg1Root = trees.get(arg1TreeNum);
			String arg1 = (arg1TreeNum) + ":" + arg1Root.firstChild().nodeNumber(arg1Root);
			ArrayList<String> arg1Nodes = new ArrayList<String>();
			arg1Nodes.add(arg1);
			arg1Span = calcNodesSpan(arg1Nodes, spanHashMap, connSpan, arg2Nodes);
		}

		return new String[] { arg1Span, arg2Span, Corpus.spanToText(arg1Span, orgText).replaceAll("\\|", "<PIPE>"),
				Corpus.spanToText(arg2Span, orgText).replaceAll("\\|", "<PIPE>") };
	}

	private String calcNodesSpan(List<String> nodes, Map<String, String> spanHashMap, String connSpan,
			List<String> otherArg) {
		Set<String> conn = new HashSet<>();
		String[] c = connSpan.split(";");
		for (String e : c) {
			conn.add(e);
		}
		List<Span> spans = new ArrayList<>();
		String[] d = otherArg != null ? otherArg.get(0).split(":") : null;
		int skipTreeNum = -1;
		int skipNodeNumber = -1;
		if (d != null) {
			skipTreeNum = Integer.parseInt(d[0]);
			skipNodeNumber = Integer.parseInt(d[1]);
		}
		// for debugging purposes
		@SuppressWarnings("unused")
		Tree skipNode = d != null ? trees.get(skipTreeNum).getNodeNumber(skipNodeNumber) : null;
		for (String txt : nodes) {
			String[] tmp = txt.split(":");
			int treeNum = Integer.parseInt(tmp[0]);
			Tree root = trees.get(treeNum);
			Tree node = root.getNodeNumber(Integer.parseInt(tmp[1]));
			if (node == null) {
				continue;
			}
			Queue<Tree> children = new LinkedList<>();
			children.add(node);
			while (children.size() > 0) {
				Tree child = children.poll();
				// the same tree and the same node number as the other argument
				if (skipTreeNum == treeNum && child.nodeNumber(root) == skipNodeNumber) {
					continue;
				} else if (!child.isLeaf()) {
					children.addAll(child.getChildrenAsList());
				} else {
					int nodeNum = child.nodeNumber(root);
					String span = spanHashMap.get(treeNum + ":" + nodeNum);
					if (span != null && !hasIntersection(span, conn)) {
						spans.add(new Span(span));
					}
				}
			}
		}

		Collections.sort(spans);
		StringBuilder sb = new StringBuilder();
		for (Span span : spans) {
			if (sb.length() > 0) {
				String end = sb.substring(sb.lastIndexOf(".") + 1);
				String start = Integer.toString(span.start);
				if (Integer.parseInt(start) - Integer.parseInt(end) > 2) {
					sb.append(";");
					sb.append(span);
				} else {
					sb.delete(sb.lastIndexOf(".") + 1, sb.length());
					try {
						end = Integer.toString(span.end);
					} catch (StringIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
					sb.append(end);
				}
			} else {
				sb.append(span);
			}
		}

		String result = sb.toString();
		String out = removePunctuation(result);
		return out;
	}

	private String removePunctuation(String sb) {
		String[] result = sb.split(";");
		String out = "";
		if (sb.length() > 0) {
			for (String span : result) {
				if (out.length() > 0 && out.charAt(out.length() - 1) != ';') {
					out += ";";
				}
				int[] tmp = Corpus.spanToInt(span);
				char[] text = orgText.substring(tmp[0], tmp[1]).toCharArray();
				int i = 0;
				for (; i < text.length; ++i) {
					if (Character.isAlphabetic(text[i]) || Character.isDigit(text[i]) || text[i] == '%'
							|| text[i] == '(') {
						break;
					}
				}
				tmp[0] += i;
				for (i = text.length - 1; i > 0; --i) {
					if (Character.isAlphabetic(text[i]) || Character.isDigit(text[i]) || text[i] == '%'
							|| text[i] == ')') {
						break;
					}
				}
				tmp[1] -= text.length - 1 - i;
				if (tmp[1] - tmp[0] > 0) {
					out += tmp[0] + ".." + tmp[1];
				}
			}
		}
		if (out.endsWith(";")) {
			out = out.substring(0, out.length() - 1);
		}

		return out;
	}

	private boolean hasIntersection(String span, Set<String> conn) {
		for (String c : conn) {
			if (span.equals(c)) {
				return true;
			}
			if (spansIntersect(span, c)) {
				return true;
			}
		}

		return false;
	}

	private boolean spansIntersect(String span, String c) {

		if (span == null || c == null) {
			return false;
		}

		String[] sp = span.split("\\.\\.");
		String[] cc = c.split("\\.\\.");

		int[] spInt = { Integer.parseInt(sp[0]), Integer.parseInt(sp[1]) };
		int[] ccInt = { Integer.parseInt(cc[0]), Integer.parseInt(cc[1]) };
		if (spInt[0] <= ccInt[0] && ccInt[1] <= spInt[1]) {
			return true;
		}
		if (spInt[0] <= ccInt[0] && ccInt[0] <= spInt[1]) {
			return true;
		}
		if (spInt[0] <= ccInt[1] && ccInt[1] <= spInt[1]) {
			return true;
		}
		if (ccInt[0] <= spInt[0] && spInt[1] <= ccInt[1]) {
			return true;
		}
		return false;
	}

	private ArrayList<String> printAnyTxtSsArgs(List<String> explicitSpans, CompOutput argExtOut, ArrayList<String> argExtAux, List<String[]> features) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		Map<String, String> pipeHash = new HashMap<>();
		for (String pipe : explicitSpans) {
			String[] cols = pipe.split("\\|", -1);
			pipeHash.put(cols[3], pipe);
		}

		ArrayList<String> maxEntOut = ((ArgExtOutput) argExtOut).getOutputList();

		int auxCounter = 0;
		int counter = 0;
		for(; auxCounter<argExtAux.size();++auxCounter){
			String tmp = argExtAux.get(auxCounter);
			String[] line = tmp.split(":");
			String[] index = line[1].split("\\-");
			int stIndex = Integer.parseInt(index[0]);
			int endIndex = Integer.parseInt(index[1]);
			List<String> arg1Nodes = new ArrayList<>();
			List<String> arg2Nodes = new ArrayList<>();
			double arg1Max = 0, arg2Max = 0;
			int arg1Ind = 0;

			String[] nodes = new String[endIndex - stIndex];
			String[][] vals = new String[endIndex - stIndex][];

			for (int i = stIndex; i < endIndex; ++i) {
				tmp = maxEntOut.get(counter);

				vals[i - stIndex] = tmp.split("\\s+");

				tmp = features.get(counter)[0];
				nodes[i - stIndex] = tmp.substring(tmp.lastIndexOf(' ')).trim();

				if (i + 1 < endIndex) {
					auxCounter++;					
				}
				counter++;
			}

			for (int i = 0; i < nodes.length; ++i) {
				double val = Double
						.parseDouble(vals[i][1].substring(vals[i][1].indexOf('[') + 1, vals[i][1].indexOf(']')));
				if (val > arg1Max) {
					arg1Ind = i;
					arg1Nodes.clear();
					arg1Nodes.add(nodes[i]);
					arg1Max = val;
				}
			}

			for (int i = 0; i < nodes.length; ++i) {
				double val = Double
						.parseDouble(vals[i][2].substring(vals[i][2].indexOf('[') + 1, vals[i][2].indexOf(']')));
				if (val > arg2Max && arg1Ind != i) {
					arg2Nodes.clear();
					arg2Nodes.add(nodes[i]);
					arg2Max = val;
				}
			}

			String arg2Span = calcNodesSpan(arg2Nodes, this.spanHashMap, line[4], null);
			String arg1Span = calcNodesSpan(arg1Nodes, this.spanHashMap, line[4], arg2Nodes);
			String arg2Txt = Corpus.spanToText(arg2Span, orgText).replaceAll("\\|", "<PIPE>");
			String arg1Txt = Corpus.spanToText(arg1Span, orgText).replaceAll("\\|", "<PIPE>");

			String[] cols = pipeHash.get(line[4]).split("\\|", -1);

			StringBuilder resultLine = new StringBuilder();

			int sentNumber = -1;
			for (int i = 0; i < cols.length; i++) {
				String col = cols[i];
				if (i == 7) {
					sentNumber = Integer.parseInt(col);
				}
				if (i == 22) {
					resultLine.append(arg1Span + "|");
				} else if (i == 23) {
					resultLine.append(sentNumber + "|");
				} else if (i == 24) {
					resultLine.append(arg1Txt + "|");

				} else if (i == 32) {
					resultLine.append(arg2Span + "|");
				} else if (i == 33) {
					resultLine.append(sentNumber + "|");
				} else if (i == 34) {
					resultLine.append(arg2Txt + "|");
				} else {
					resultLine.append(col + "|");
				}
			}
			resultLine.deleteCharAt(resultLine.length() - 1);

			result.add(resultLine.toString());
		}

		return result;
	}


}
