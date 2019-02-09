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
 */

package module.graph.pdtbp.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import module.graph.pdtbp.resources.FeatureType;
import edu.stanford.nlp.trees.Tree;

public class Corpus {

	public static enum Type {
		PDTB, BIO_DRB;
	}

	public static FilenameFilter PIPE_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".pipe");
		}
	};

	public static FilenameFilter TXT_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".txt");
		}
	};
	
	public static List<String> genExplicitSpans(ConCompOutput conOutput) throws IOException {
		List<String> result = new ArrayList<>();

		int counter = 0;
		for(String line : conOutput.getConSpan()){
			//		String line = conOutput.getConSpan();

			String[] out = conOutput.getOutput().get(counter).split("\\s+");
			if (out[2].equals("1")) {
				String[] cols = line.split("\\s+");
				StringBuilder rel = new StringBuilder();
				rel.append("Explicit|");
				rel.append("||");
				rel.append(cols[0] + "|");// col 3 connective span
				rel.append("|");
				rel.append(cols[2].replace('_', ' ') + "|");// col 5
				// connective
				// raw text
				rel.append("|");
				rel.append(cols[1] + "|");// col 7 sentence number
				rel.append(cols[2].toLowerCase().replace('_', ' ') + "|"); // col
				// 8
				// connective
				// head
				for (int i = 0; i < 38; ++i) {
					rel.append("|");
				}
				result.add(rel.toString());
			}
			counter++;
		}
		return result;
	}

	public static String calculateHeadSpan(String pipe) {
		String[] cols = pipe.split("\\|", -1);
		String head = cols[8];
		String rawText = cols[5].toLowerCase();
		String span = cols[3];
		if (!((cols[1] + cols[2]).equals("2369") && head.equals("if then"))) {

			int index = rawText.indexOf(head);
			int start = Integer.parseInt(span.split("\\.\\.")[0]) + index;
			int end = start + head.length();
			return start + ".." + end;
		} else {
			return span;
		}
	}
	
	public static String genEpP2ipePath(String articleFilename, FeatureType featureType) {
		StringBuilder path = new StringBuilder();
		path.append(articleFilename + ".pipe");

		return path.toString();
	}

	public static String getLabel(String arg1Gorn, String arg2Gorn) {

		LinkedList<Integer> arg1 = gornToSenIds(arg1Gorn);
		LinkedList<Integer> arg2 = gornToSenIds(arg2Gorn);
		String label = "";

		if (arg1.getLast() + 1 == arg2.getFirst()) {
			label = "IPS";
		} else if (arg1.getLast() + 1 < arg2.getFirst()) {
			label = "NAPS";
		} else if (arg2.getFirst() + 1 <= arg1.getFirst()) {
			label = "FS";
		} else {
			label = "SS";
		}

		return label;
	}

	public static String getBioLabel(String arg1Span, String arg2Span, ArrayList<String> spanMap) throws IOException {
		LinkedList<Integer> arg1 = spanToSenIds(arg1Span, spanMap);
		LinkedList<Integer> arg2 = spanToSenIds(arg2Span, spanMap);

		String label = "";

		if (arg1.getLast() + 1 == arg2.getFirst()) {
			label = "IPS";
		} else if (arg1.getLast() + 1 < arg2.getFirst()) {
			label = "NAPS";
		} else if (arg2.getFirst() + 1 <= arg1.getFirst()) {
			label = "FS";
		} else {
			label = "SS";
		}

		return label;
	}

	private static LinkedList<Integer> gornToSenIds(String gorn) {
		String[] tmp = gorn.split(";");
		LinkedList<Integer> res = new LinkedList<>();
		Set<Integer> done = new HashSet<>();
		for (String t : tmp) {
			String[] e = t.split(",");
			int i = Integer.parseInt(e[0]);
			if (!done.contains(i)) {
				res.add(i);
				done.add(i);
			}
		}

		return res;
	}

	public static LinkedList<Integer> spanToSenIds(String span, ArrayList<String> spanMap) throws IOException {

		LinkedList<Integer> result = new LinkedList<>();
		String[] spans = span.split(";");
		for (String line : spanMap) {
			// 1064873.txt.ptree,0,6,0..10,Resistance
			String[] cols = line.split(",", -1);

			int start = Integer.parseInt(cols[3].split("\\.\\.")[0]);
			int end = Integer.parseInt(cols[3].split("\\.\\.")[1]);

			for (String tmp : spans) {
				int outStart = Integer.parseInt(tmp.split("\\.\\.")[0]);
				int outEnd = Integer.parseInt(tmp.split("\\.\\.")[1]);
				if (outStart <= start && end <= outEnd) {
					result.add(Integer.parseInt(cols[1]));
				}
			}
		}

		Collections.sort(result);

		return result;
	}

	public static String getFullSense(String predictedSense) {
		String level1Sense = "";
		String level2Sense = predictedSense;
		switch (predictedSense) {
		case "Asynchronous":
		case "Synchrony":
			level1Sense = "Temporal.";
			break;
		case "Contrast":
		case "Pragmatic_contrast":
		case "Concession":
		case "Pragmatic_concession":
			level1Sense = "Comparison.";
			break;
		case "Conjunction":
		case "Instantiation":
		case "Restatement":
		case "Alternative":
		case "Exception":
		case "List":
			level1Sense = "Expansion.";
			break;
		case "Cause":
		case "Pragmatic_cause":
		case "Condition":
		case "Pragmatic_condition":
			level1Sense = "Contingency.";
			break;
		case "EntRel":
		case "AltLex":
			level1Sense = predictedSense;
			level2Sense = "";
			break;
		}
		return (level1Sense + level2Sense).replace('_', ' ');
	}

	public static String spanToText(String span, String orgText) {
		if (span.isEmpty()) {
			return "";
		} else {
			int[] sp = spanToInt(span);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sp.length; i += 2) {
				String text = orgText.substring(sp[i], sp[i + 1]).replaceAll("\\r", "").replaceAll(Util.NEW_LINE, "");
				sb.append(text.trim() + " ");
			}
			return sb.toString().trim();
		}
	}

	public static String continuousTextToSpan(String contText, String sourceText) {

		sourceText = sourceText.replace(' ', ' ');
		sourceText = sourceText.replaceAll("[^\\p{ASCII}]", "&");
		contText = contText.replaceAll("[^\\p{ASCII}]", "&");
		int newSourceLength = sourceText.replaceAll("\\s+", "").length();
		StringBuilder sb = new StringBuilder(newSourceLength);
		int[] mapNewToOld = new int[newSourceLength];

		for (int i = 0; i < sourceText.length(); ++i) {
			char sourceChar = sourceText.charAt(i);
			if (!Character.isWhitespace(sourceChar)) {
				mapNewToOld[sb.length()] = i;
				sb.append(sourceChar);
			}
		}

		String newSource = sb.toString();
		if (newSource.length() != newSourceLength) {
			//			log.error("Missed some whitespace");
		}

		String newContText = contText.replaceAll("\\s+", "");
		int chop = Math.min(newContText.length(), 19);
		int start = newSource.indexOf(newContText.substring(0, chop));
		int end = Math.min(start + newContText.length() - 1, mapNewToOld.length - 1);
		if (start == -1) {
			return "";
		} else {
			int realStart = mapNewToOld[start];
			int realEnd = mapNewToOld[end];

			return realStart + ".." + realEnd;
		}
	}

	public static int[] spanToInt(String span) {
		if (span.trim().length() > 0) {
			String[] sp = span.split(";");
			int[] result = new int[sp.length * 2];
			for (int i = 0; i < sp.length; ++i) {
				Integer start = Integer.parseInt(sp[i].substring(0, sp[i].indexOf('.')));
				Integer end = Integer.parseInt(sp[i].substring(sp[i].lastIndexOf('.') + 1));
				result[i * 2] = start;
				result[i * 2 + 1] = end;
			}
			return result;
		} else {
			return null;
		}
	}

	public static String nodeToString(Tree leaf) {
		String leafStr = leaf.toString();
		leafStr = leafStr.replaceAll("-LRB-", "(");
		leafStr = leafStr.replaceAll("-LCB-", "{");
		leafStr = leafStr.replaceAll("-LSB-", "[");
		leafStr = leafStr.replaceAll("-RRB-", ")");
		leafStr = leafStr.replaceAll("-RCB-", "}");
		leafStr = leafStr.replaceAll("-RSB-", "]");
		leafStr = leafStr.replaceAll("``", "\"");
		leafStr = leafStr.replaceAll("''", "\"");
		leafStr = leafStr.replaceAll("--", "–");
		leafStr = leafStr.replaceAll("`", "'");

		return leafStr;
	}

	public static String nodesToString(List<Tree> posNodes) {
		StringBuilder sba = new StringBuilder();
		for (Tree leaf : posNodes) {
			sba.append(Corpus.nodeToString(leaf.firstChild()));
			sba.append(' ');
		}
		return sba.toString().trim();
	}

	public static Map<String, LinkedList<Integer>> getBioSpanToSenId(ArrayList<String> spanMap,
			List<String> relations) {
		Map<String, LinkedList<Integer>> spanToSenIdMap = new HashMap<>();

		for (String rel : relations) {
			String[] cols = rel.split("\\|", -1);
			String[] argSpans = { cols[14], cols[20] };

			for (String line : spanMap) {
				// 1064873.txt.ptree,0,6,0..10,Resistance
				String[] lineCols = line.split(",", -1);

				int start = Integer.parseInt(lineCols[3].split("\\.\\.")[0]);
				int end = Integer.parseInt(lineCols[3].split("\\.\\.")[1]);

				for (String span : argSpans) {
					if (span.length() > 0) {
						LinkedList<Integer> result = new LinkedList<>();
						String[] spans = span.split(";");
						for (String tmp : spans) {
							int outStart = Integer.parseInt(tmp.split("\\.\\.")[0]);
							int outEnd = Integer.parseInt(tmp.split("\\.\\.")[1]);

							if (outStart <= start && end <= outEnd) {
								result.add(Integer.parseInt(lineCols[1]));
							}
						}
						if (result.size() > 0) {
							Collections.sort(result);
							spanToSenIdMap.put(span, result);
						}
					}
				}
			}
		}
		return spanToSenIdMap;
	}

}
