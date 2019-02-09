/**
   Copyright (c) 2012, the JIGSAW AUTHORS.

   All rights reserved.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:

 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided
   with the distribution.

 * Neither the name of the University of Bari nor the names
   of its contributors may be used to endorse or promote products
   derived from this software without specific prior written
   permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
   A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
    
   GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007
 **/

package jigsaw;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import module.graph.helper.Configurations;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import jigsaw.data.Token;
import jigsaw.data.TokenGroup;
import jigsaw.nlp.SimpleTextProcessing;
import jigsaw.wn.Tag2Wn;
import jigsaw.wn.WordNet;

/**
 * This class implements JIGSAW algorithm for Word Sense Disambiguation.
 * (Revision for Semeval-1/EVALITA 2007)
 * -fixed some bugs in Lesk algorithm
 * -added long and short output
 * -added ZIPF distribution in synset rank computation
 *
 * @author Basile Pierpaolo
 */
public class JIGSAW {

   
    public static final int SIM_WEIGTH = 0;
   
    public static final int SIM_OCCURENCE = 1;
    
    public static final int SIM_TFIDF = 2;
    private WordNet wordNet = null;
    private SimpleTextProcessing textProcessing;
    private int maxVerb = 0;
    private int radius = 9;
    private int measure = SIM_OCCURENCE;
    private int depth = 6;
    private boolean verbose = false;
    private boolean shortOutput = false;
    private boolean posTagNotation = false;
    private double cut = -1.0d;
    private int commonDepth = 2;
  
    private double alfa;
    private double beta;
    private double theta;
    private double sigma;
    private int lookGram;
    private static final double s_verb = 1.977;
    private static final double s_noun = 2.688;
    private static final double s_adj = 2.855;

    private File wordnetPropFile = null;
    private File nlpTokenModel = null;
    private File nlpPosTagModel = null;
    private File nlpStopWordFile = null;
    
    
    /**
     * Creates a new instance
     */
    public JIGSAW() {
    	String path = null;//JIGSAW.class.getResource("JIGSAW.class").toString();
//    	path = path.substring(0, path.lastIndexOf("/")).split(":")[1];
    	try {
			path = Configurations.getProperty("jigsawResources");
			if(path.startsWith("./")){
				path = Configurations.getCWD() + path.substring(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		wordnetPropFile = new File(path + "/wn_file_properties.xml");
		nlpTokenModel = new File(path + "/en-token.bin");
		nlpPosTagModel = new File(path + "/en-pos-maxent.bin");
		nlpStopWordFile = new File(path + "/en-stop");
    }

    private void init() {
        try {
            depth = 3;
            commonDepth = 3;
            measure = 1;
            radius = 20;
            alfa = 0.5;
            beta = 0.5;
            theta = 0;
            sigma = 2.0;
            maxVerb = 1;
            lookGram = 3;
            verbose = false;
            this.posTagNotation = true;
            this.shortOutput = true;
            this.wordNet = new WordNet();
            this.wordNet.init(wordnetPropFile);
            this.textProcessing = new SimpleTextProcessing(nlpTokenModel,nlpPosTagModel,nlpStopWordFile);
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "ERROR to init JIGSAW algorithm (check config file)", ex);
        }
    }

    private TokenGroup getToken(String[] tokens, String[] posTags, String[] stems, String[] lemmas, boolean convertTag) throws Exception {
        TokenGroup result = new TokenGroup();
        try {
            for (int i = 0; i < tokens.length; i++) {

                String posTag = posTags[i];
                if (convertTag) {
                    posTag = Tag2Wn.getPos(posTags[i]);
                }
                Token token = new Token();
                token.setToken(tokens[i]);
                token.setPosition(i);
                token.setGroupPosition(result.size());
                token.setPosTag(posTag);
                token.setStem(stems[i]);
                token.setLemma(lemmas[i]);
                String[] syns = wordNet.getAllSynsetByWord(token.getLemma(), posTag);
                if (syns == null) {
                    syns = wordNet.getAllSynsetByWord(token.getToken(), posTag);
                }
                if (syns == null) {
                    syns = wordNet.getAllSynsetByWord(token.getStem(), posTag);
                }
                if (syns != null) {
                    token.setSyns(syns);
                } else {
                    token.setSyns(new String[0]);
                }
                result.add(token);
            }
        } catch (Exception ex) {
            throw new Exception("Error to retrieve synsets from text", ex);
        }
        return result;
    }

    /**
     * Generate context
     *
     * @param tokens Token group which contains tokens
     * @param offset Start offset in the sentence
     * @param insertTarget Add target word to the context.
     * @return TokenGroup the context
     */
    public TokenGroup getContext(TokenGroup tokens, int offset, boolean insertTarget) {

        TokenGroup result = new TokenGroup();
        try {
            String posTag = tokens.get(offset).getPosTag();
            String tokenStem = tokens.get(offset).getLemma();
            int i = offset - 1;
            int count = 0;
            while (i >= 0 && count < radius) {
                String ptc = tokens.get(i).getPosTag();
                String contextStem = tokens.get(i).getLemma();
                boolean stopWord = textProcessing.isStopWord(tokens.get(i).getToken());
                if (stopWord || tokenStem.equalsIgnoreCase(contextStem)) {
                    i--;
                    continue;
                }
                if (posTag.equals("v") && ptc.equals("n")) {
                    result.add(tokens.get(i));
                    count++;
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                } else if (posTag.equals("n") && ptc.equals("n")) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));
                    count++;
                } else if (posTag.equals("a") && (ptc.equals("r") || ptc.equals("n"))) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));
                    count++;
                } else if (posTag.equals("r") && (ptc.equals("a") || ptc.equals("n"))) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));
                    count++;
                }

                i--;
            }
            if (insertTarget) {
                result.add(tokens.get(offset));
            }
            i = offset + 1;
            count = 0;
            while (i < tokens.size() && count < radius) {
                String ptc = tokens.get(i).getPosTag();
                String contextStem = tokens.get(i).getLemma();
                boolean stopWord = textProcessing.isStopWord(tokens.get(i).getToken());
                if (stopWord || tokenStem.equalsIgnoreCase(contextStem)) {
                    i++;
                    continue;
                }
                if (posTag.equals("v") && (ptc.equals("n"))) {
                    result.add(tokens.get(i));
                    count++;
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                } else if (posTag.equals("n") && ptc.equals("n")) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));

                    count++;
                } else if (posTag.equals("a") && (ptc.equals("r") || ptc.equals("n"))) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));
                    count++;
                } else if (posTag.equals("r") && (ptc.equals("a") || ptc.equals("n"))) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), ptc});
                    }
                    result.add(tokens.get(i));
                    count++;
                }
                i++;

            }

            if (posTag.equals("n")) {
                int countVerb = 0;
                i = offset - 1;
                while (i >= 0 && countVerb < maxVerb) {
                    if (tokens.get(i).getPosTag().equals("v")) {
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), tokens.get(i).getPosTag()});
                        }
                        result.add(tokens.get(i));
                        countVerb++;
                    }
                    i--;
                }
                countVerb = 0;
                i = offset + 1;
                while (i < tokens.size() && countVerb < maxVerb) {
                    if (tokens.get(i).getPosTag().equals("v")) {
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to context: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), tokens.get(i).getPosTag()});
                        }
                        result.add(tokens.get(i));
                        countVerb++;
                    }
                    i++;
                }
            }

            return result;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to build context, return empty context", ex);
            return result;
        }
    }

    /**
     * Generate context
     *
     * @param tokens Token group which contains tokens
     * @return TokenGroup the context
     */
    public TokenGroup getNouns(TokenGroup tokens) {
        TokenGroup result = new TokenGroup();
        try {
            for (int i = 0; i < tokens.size(); i++) {
                String posTag = tokens.get(i).getPosTag();
                boolean stopWord = textProcessing.isStopWord(tokens.get(i).getToken());
                if (stopWord) {
                    continue;
                }
                if (posTag.equals("n")) {
                    result.add(tokens.get(i));
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Add token to nouns: {0} Pos-tag: {1}", new Object[]{tokens.get(i).getToken(), posTag});
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to build context (nouns), return empty context", ex);
            return result;
        }
    }

    private String normalizeDescription(String description, Token st) throws Exception {
        try {
            description = textProcessing.normalize(description);

            StringBuilder sb = new StringBuilder(description);
            String[] tokens = textProcessing.tokenize(description);
            for (int i = 0; i < tokens.length; i++) {
                if (textProcessing.isStopWord(tokens[i])) {
                    continue;
                }

                String tokenLemma = textProcessing.stem(tokens[i]);
                if (tokenLemma.equalsIgnoreCase(st.getStem())) {
                    continue;
                }
                sb.append(" ").append(tokenLemma);
            }
            return description;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to build description, return empty string", ex);
            return "";
        }
    }

    private String generateContextGloss(TokenGroup tg) throws Exception {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tg.size(); i++) {
                Token t = tg.get(i);
                String[] offset = t.getSyns();
                for (int j = 0; offset != null && j < offset.length; j++) {
                    String description = wordNet.getNormalizeDescriptionByOffset(offset[j], t.getPosTag());
                    sb.append(this.normalizeDescription(description, t));
                    if (j < offset.length - 1) {
                        sb.append(" ");
                    }
                }
                if (i < tg.size() - 1) {
                    sb.append(" ");
                }
            }
            return sb.toString().replaceAll("[ ]{2,}", " ");
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String generateTargetGloss(Token t, int i) throws Exception {
        try {
            StringBuilder description = new StringBuilder();
            description.append(wordNet.getAllGlossByOffset(t.getSyns()[i], t.getPosTag()));
            description.append(" ");
            description.append(wordNet.getAllWordsInSynset(t.getSyns()[i], t.getPosTag()));
            String normDesc = this.normalizeDescription(description.toString(), t);
            description = new StringBuilder(normDesc);
            if (t.getPosTag().equals("v")) {
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.HYPERNYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.HYPONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.CAUSE, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.ENTAILMENT, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SEE_ALSO, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.USAGE, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.DOMAIN_ALL, depth, true));
            } else if (t.getPosTag().equals("n")) {
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.HYPERNYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.HYPONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.PART_HOLONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.MEMBER_HOLONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.PART_MERONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.MEMBER_MERONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SUBSTANCE_HOLONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SUBSTANCE_MERONYM, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SEE_ALSO, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.USAGE, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.DOMAIN_ALL, depth, true));
            } else if (t.getPosTag().equals("a")) {
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.ATTRIBUTE, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SIMILAR_TO, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.SEE_ALSO, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.PERTAINYM, depth, true));
            } else if (t.getPosTag().equals("r")) {
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.USAGE, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.DOMAIN_ALL, depth, true));
                description.append(" ").append(wordNet.getRelationElement(t.getSyns()[i], t.getPosTag(), PointerType.PERTAINYM, depth, true));
            }
            return description.toString().replaceAll("[ ]{2,}", " ");

        } catch (Exception ex) {
            throw ex;
        }
    }

    private double compareSimWeight(String tg, String cg, String tokenStem) {
        try {
            Map<String, Integer> count = new HashMap<String, Integer>();
            String[] tokens = cg.split("[ ]+");
            for (int i = 0; i < tokens.length; i++) {
                Integer c = count.get(textProcessing.stem(tokens[i]));
                if (c == null) {
                    count.put(textProcessing.stem(tokens[i]), new Integer(1));
                } else {
                    count.put(textProcessing.stem(tokens[i]), new Integer(c.intValue() + 1));
                }
            }
            int n = tokens.length;
            tokens = tg.split("[ ]+");
            int nt = tokens.length;
            double result = 0;
            ArrayList<String> stop = new ArrayList<String>();
            for (int i = 0; i < tokens.length; i++) {
                String stem = textProcessing.stem(tokens[i]);
                Integer c = count.get(stem);
                if (c != null && !stop.contains(stem) && !stem.equals(tokenStem)) {
                    double intR = 0;
                    if (this.measure == SIM_WEIGTH) {
                        intR = Math.log((double) n / (double) c.intValue());
                    } else {
                        intR = (double) c.intValue();
                    }
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Find token: {0} c: {1} n: {2} sim: {3}", new Object[]{tokens[i], c.intValue(), n, intR});
                    }
                    result += intR;
                    stop.add(stem);
                }
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Sim: {0} N.context: {1} N.target: {2}", new Object[]{result, n, nt});
            }
            return result;
        } catch (Exception ex) {
            return 0;
        }
    }

    private double compareTfIdf(String tg, String cg, String tokenStem) {
        try {
            Map<String, Integer> count = new HashMap<String, Integer>();
            String[] tokens = cg.split("[ ]+");
            for (int i = 0; i < tokens.length; i++) {
                Integer c = count.get(tokens[i]);
                if (c == null) {
                    count.put(tokens[i], new Integer(1));
                } else {
                    count.put(tokens[i], new Integer(c.intValue() + 1));
                }
            }
            int n = tokens.length;
            tokens = tg.split("[ ]+");
            int nt = tokens.length;
            double result = 0;
            Set<String> stop = new HashSet<String>();
            int tf = 0;
            int df = 0;
            for (int i = 0; i < tokens.length; i++) {
                String stem = textProcessing.stem(tokens[i]);
                Integer c = (Integer) count.get(stem);
                if (c != null && !stop.contains(stem) && !stem.equals(tokenStem)) {
                    df += c.intValue();
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Find token: " + tokens[i] + " c: " + c.intValue());
                    }
                    tf++;
                    stop.add(stem);
                }
            }
            if (tf == 0) {
                result = 0;
            } else {
                result = tf * (Math.log((double) n / (double) df) + 1);
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Sim: {0} N.context: {1} N.target: {2}", new Object[]{result, n, nt});
            }
            return result;
        } catch (Exception ex) {
            return 0;
        }
    }

    private double sim(Token t1, Token t2) {
        try {
            double max = -Double.MAX_VALUE;
            String[] s1 = t1.getSyns();
            String[] s2 = t2.getSyns();
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Sim start on: {0} syns.", s1.length * s2.length);
            }
            for (int i = 0; i < s1.length; i++) {
                for (int j = 0; j < s2.length; j++) {
                    int d = wordNet.getDepthByOffset(s1[i], s2[j], t1.getPosTag(), PointerType.HYPERNYM, depth);

                    double sim = 0;
                    if (d == 0) {
                        sim = -Math.log((double) 1 / (double) (2 * WordNet.MAX_DEPTH));
                    } else if (d <= wordNet.getMaxDepth()) {
                        sim = -Math.log((double) d / (double) (2 * WordNet.MAX_DEPTH));
                    } else {
                        sim = -Math.log((double) (WordNet.MAX_DEPTH + 1) / (double) (2 * WordNet.MAX_DEPTH)) / -Math.log((double) 1 / (double) WordNet.MAX_DEPTH);
                    }

                    if (sim > max) {
                        max = sim;
                    }
                }
            }
            return max;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to compute similarity...return zero.", ex);
            return 0;
        }
    }

    private double gauss(int d1, int d2) {
        int x = d1 - d2;
        if (verbose) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Distance: {0}", x);
        }
        double k = (double) 1 - ((double) 2 / Math.sqrt(2 * Math.PI));
        double gauss = (double) 4 * ((double) 1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-(((double) x * x) / (2 * sigma * sigma))) + k;
        if (verbose) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Gauss: {0}", gauss);
        }
        return gauss;
    }

    private double computeH(int N, double s) {
        double H = 0;
        for (int i = 1; i <= N; i++) {
            H += 1 / (Math.pow((double) i, s));
        }
        return H;
    }

    private double computeZIPF(int z, int n, double s) {
        return 1 / (Math.pow((double) z + 1, s) * computeH(n, s));
    }

    private String commonMinSyn(Token t1, Token t2, PointerType r) {
        try {
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Find min common synset: {0}, {1}", new Object[]{t1.getToken(), t2.getToken()});
            }
            String[] s1 = t1.getSyns();
            String[] s2 = t2.getSyns();
            String[] result;
            String offset = "-1";
            int minDepth = wordNet.getMaxDepth() + 1;
            for (int i = 0; i < s1.length; i++) {
                for (int j = 0; j < s2.length; j++) {
                    result = wordNet.getCommon(s1[i], s2[j], t1.getPosTag(), r, commonDepth);
                    int rdepth = Integer.parseInt(result[0]);
                    if (rdepth < minDepth) {
                        offset = result[1];
                        minDepth = rdepth;
                    }
                }
            }
            return offset;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to find MSC...return unknow", ex);
            return "U";
        }
    }
    
    //NOT USED 
    @SuppressWarnings("unused")
	private double simVerbNoun(String offset, int indexSyn, Token target, TokenGroup verbs) {
        try {
            double maxPhi = -Double.MAX_VALUE;
            for (int i = 0; i < verbs.size(); i++) {
                if (verbs.get(i).getSyn() == null) {
                    continue;
                }
                String syn = verbs.get(i).getSyn();
                TokenGroup name = getNameInDef(syn, verbs.get(i).getPosTag());
                double somGauss = 0;
                double somTot = 0;
                double maxj = 0;
                somGauss += gauss(target.getGroupPosition(), verbs.get(i).getGroupPosition());
                for (int k = 0; k < name.size(); k++) {
                    double sim = simVerb(offset, name.get(k));
                    if (sim > maxj) {
                        maxj = sim;
                    }
                }
                somTot += gauss(target.getGroupPosition(), verbs.get(i).getGroupPosition()) * maxj / somGauss;
                double r = computeZIPF(indexSyn, target.getSyns().length, JIGSAW.s_verb);
                double phi = r * somTot;
                if (phi > maxPhi) {
                    maxPhi = phi;
                }
            }
            return maxPhi;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to compute similarity between verb and noun...return 0.", ex);
            return 0;
        }
    }

    private void setSynNouns(TokenGroup tg) throws Exception {
        try {
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Number of noun: {0}", tg.size());
            }
            int MAX_SYN = 0;
            for (int i = 0; i < tg.size(); i++) {
                if (tg.get(i).getSyns().length > MAX_SYN) {
                    MAX_SYN = tg.get(i).getSyns().length;
                }
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "MAX_SYN: {0}", MAX_SYN);
            }
            double[][] v = new double[tg.size()][tg.size()];
            String[][] c = new String[tg.size()][tg.size()];
            double support[][] = new double[tg.size()][MAX_SYN];
            double[] normalization = new double[tg.size()];
            //int max=0;
            for (int i = 0; i < tg.size(); i++) {
                for (int j = 0; j < tg.size(); j++) {
                    if (i < j) {
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Compute sim, to: {0} and {1}", new Object[]{tg.get(i).getToken(), tg.get(j).getToken()});
                        }
                        v[i][j] = sim(tg.get(i), tg.get(j)) * gauss(tg.get(i).getGroupPosition(), tg.get(j).getGroupPosition());
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Max similarty: {0}", v[i][j]);
                        }
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Compute common min syn.");
                        }
                        c[i][j] = commonMinSyn(tg.get(i), tg.get(j), PointerType.HYPERNYM);
                        if (verbose) {
                            Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Min common hype: {0}", wordNet.getAllDescriptionByOffset(c[i][j], "n"));
                        }
                        String[] syns = tg.get(i).getSyns();
                        for (int k = 0; k < syns.length; k++) {
                            if (wordNet.isHypernym(c[i][j], syns[k], tg.get(i).getPosTag())) {
                                support[i][k] += v[i][j];
                            }
                        }
                        syns = tg.get(j).getSyns();
                        for (int k = 0; k < syns.length; k++) {
                            if (wordNet.isHypernym(c[i][j], syns[k], tg.get(j).getPosTag())) {
                                support[j][k] += v[i][j];
                            }
                        }
                        normalization[i] += v[i][j];
                        normalization[j] += v[i][j];
                    }
                }
            }

            for (int i = 0; i < tg.size(); i++) {

                double maxPhi = -Double.MAX_VALUE;
                int maxSynPos = 0;
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Check syn on token: {0}", tg.get(i).getToken());
                }
                String[] syns = tg.get(i).getSyns();
                double phi = 0;
                StringBuilder buf = new StringBuilder();

                for (int k = 0; k < syns.length; k++) {

                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Syn: {0}", syns[k]);
                    }
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Normalization: {0}", normalization[i]);
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Support: {0}", support[i][k]);
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "ComputeR: {0}", computeZIPF(k, syns.length, JIGSAW.s_noun));
                    }
                    if (normalization[i] != 0) {
                        phi = alfa * support[i][k] / normalization[i] + beta * computeZIPF(k, syns.length, JIGSAW.s_noun);
                    } else {
                        phi = alfa * 1 / (double) syns.length + beta * computeZIPF(k, syns.length, JIGSAW.s_noun);
                    }
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "PHI: {0}", phi);
                    }
                    if (phi > maxPhi) {
                        maxPhi = phi;
                        maxSynPos = k;
                    }

                    buf.append(syns[k]).append("/").append(phi);
                    if (k < syns.length - 1) {
                        buf.append(" ");
                    }

                }
                if (shortOutput) {
                    if (maxPhi >= cut) {
                        tg.get(i).setSyn(syns[maxSynPos]);
                    }
                } else {
                    tg.get(i).setSyn(buf.toString());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to disambiguate nouns: " + ex.toString(), ex);
            throw ex;
        }
    }

    private double simVerb(Token t1, Token t2) {
        try {
            double max = -Double.MAX_VALUE;
            String[] s1;
            s1 = t1.getSyns();
            String[] s2;
            s2 = t2.getSyns();
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Sim start on: {0} syns.", s1.length * s2.length);
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Token: {0} and {1}", new Object[]{t1.getToken(), t2.getToken()});
            }
            for (int i = 0; i < s1.length; i++) {
                for (int j = 0; j < s2.length; j++) {
                    int d = wordNet.getDepthByOffset(s1[i], s2[j], t1.getPosTag(), PointerType.HYPERNYM, depth);

                    double sim = 0;
                    if (d == 0) {
                        sim = -Math.log((double) 1 / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                    } else if (d <= wordNet.getMaxDepth()) {
                        sim = -Math.log((double) d / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                    } else {
                        sim = -Math.log((double) (wordNet.getMaxDepth() + 1) / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                    }
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Depth: {0}", d);
                    }
                    if (sim > max) {
                        max = sim;
                    }
                }
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Max sim: {0}", max);
            }
            return max;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error tp compute similarity between verbs...return 0.", ex);
            return 0;
        }
    }

    private double simVerb(String offset, Token t2) {
        try {
            double max = -Double.MAX_VALUE;
            String[] s2;
            s2 = t2.getSyns();
            for (int j = 0; j < s2.length; j++) {

                int d = wordNet.getDepthByOffset(offset, s2[j], t2.getPosTag(), PointerType.HYPERNYM, depth);

                double sim = 0;
                if (d == 0) {
                    sim = -Math.log((double) 1 / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                } else if (d <= wordNet.getMaxDepth()) {
                    sim = -Math.log((double) d / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                } else {
                    sim = -Math.log((double) (wordNet.getMaxDepth() + 1) / (double) (2 * wordNet.getMaxDepth())) / -Math.log((double) 1 / (double) wordNet.getMaxDepth());
                }
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Depth: {0}", d);
                }
                if (sim > max) {
                    max = sim;
                }
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Max sim: {0}", max);
            }
            return max;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error tp compute similarity between verbs...return 0.", ex);
            return 0;
        }
    }

    private String normalizeDescription(String text) {
        try {
            if (text != null) {
                text.replaceAll("[^A-Za-z0-9]+", " ");
                text = text.trim();
                text = text.replaceAll("[ ]{2,}", " ");
            }
            return text;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to normalize descriotion, skip...", ex);
            return text;
        }
    }

    private TokenGroup getNameInDef(String offset, String pos) {
        TokenGroup result = new TokenGroup();
        try {
            String description = wordNet.getAllGlossByOffset(offset, pos);
            description = normalizeDescription(description);
            String[] tokens = textProcessing.tokenize(description);
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Description: {0}", description);
            }
            String[] posTags = textProcessing.posTagging(tokens);
            for (int i = 0; i < posTags.length; i++) {
                if (textProcessing.isStopWord(tokens[i])) {
                    continue;
                }
                String pt = Tag2Wn.getPos(posTags[i]);
                if (pt.equals("n")) {
                    Token t = new Token();
                    t.setGroupPosition(result.size());
                    t.setPosTag(pt);
                    t.setToken(tokens[i]);
                    t.setStem(textProcessing.stem(t.getToken()));
                    t.setLemma(wordNet.lemmatize(tokens[i], pt));
                    t.setSyn(null);
                    t.setSyns(null);
                    String[] syns = wordNet.getAllSynsetByWord(t.getToken(), t.getPosTag());
                    if (syns == null) {
                        syns = wordNet.getAllSynsetByWord(t.getLemma(), t.getPosTag());
                    }
                    if (syns == null) {
                        syns = wordNet.getAllSynsetByWord(t.getStem(), t.getPosTag());
                    }
                    if (syns == null) {
                        continue;
                    }
                    t.setSyns(syns);
                    result.add(t);
                }
            }
            return result;
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to extract nouns from description", ex);
            return result;
        }

    }

    private void setSynVerb(Token t, TokenGroup tg) throws Exception {
        try {
            if (t.getSyns() != null && t.getSyns().length == 1) {
                if (shortOutput) {
                    t.setSyn(t.getSyns()[0]);
                } else {
                    t.setSyn(t.getSyns()[0] + "/1");
                }
                return;
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "START synVerb on token: {0}", t.getToken());
            }
            String[] syns = t.getSyns();
            double maxPhi = -Double.MAX_VALUE;
            int maxSynPos = 0;
            boolean exit = false;
            StringBuilder buf = new StringBuilder();
            for (int i = 0; !exit && i < syns.length; i++) {
                double[] max = new double[tg.size()];
                Arrays.fill(max, 0);
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "On syn: {0}", syns[i]);
                }
                TokenGroup name = getNameInDef(syns[i], t.getPosTag());
                double somGauss = 0;
                double somTot = 0;
                for (int j = 0; j < tg.size(); j++) {
                    double maxj = 0;
                    somGauss += gauss(t.getGroupPosition(), tg.get(j).getGroupPosition());
                    for (int k = 0; k < name.size(); k++) {
                        double sim = simVerb(tg.get(j), name.get(k));
                        if (sim > maxj) {
                            maxj = sim;
                        }
                    }
                    max[j] = maxj;
                }
                for (int j = 0; j < tg.size(); j++) {
                    somTot += gauss(t.getGroupPosition(), tg.get(j).getGroupPosition()) * max[j] / somGauss;
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Max sim: {0}", max[j]);
                    }
                }
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Somma gauss: {0}", somGauss);
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Somma totale: {0}", somTot);
                }
                double r = computeZIPF(i, syns.length, JIGSAW.s_verb);
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "R, on syn: {0} tot syns: {1} => {2}", new Object[]{i, syns.length, r});
                }

                double phi = r * somTot;
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Phi: {0}", phi);
                }
                if (phi > maxPhi) {
                    maxPhi = phi;
                    maxSynPos = i;
                }
                if (shortOutput && i < syns.length - 1 && phi > computeZIPF(i + 1, syns.length, JIGSAW.s_verb)) {
                    exit = true;
                }
                buf.append(syns[i]).append("/").append(phi);
                if (i < syns.length - 1) {
                    buf.append(" ");
                }
            }
            if (shortOutput) {
                if (maxPhi >= cut) {
                    t.setSyn(syns[maxSynPos]);
                }
            } else {
                t.setSyn(buf.toString());
            }
        } catch (Exception ex) {
            throw ex;
        }

    }

    private void setSynAdjAdv(Token t, TokenGroup tg) throws Exception {
        try {
            if (t.getSyns() != null && t.getSyns().length == 1) {
                if (shortOutput) {
                    t.setSyn(t.getSyns()[0]);
                } else {
                    t.setSyn(t.getSyns()[0] + "/1");
                }
                return;
            }
            String contextGloss = generateContextGloss(tg);
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Context gloss: {0}", contextGloss);
            }
            String[] syns = t.getSyns();
            double[] score = new double[syns.length];
            double N = 0;
            Arrays.fill(score, 0d);
            double maxSim = -Double.MAX_VALUE;
            int pos = -1;
            StringBuilder buf = new StringBuilder();
            for (int j = 0; j < syns.length; j++) {
                String targetGloss = generateTargetGloss(t, j);
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Syn target gloss: {0}", targetGloss);
                }
                double sim = 0;
                if (measure == SIM_OCCURENCE || measure == SIM_WEIGTH) {
                    sim = compareSimWeight(targetGloss, contextGloss, t.getStem());
                } else {
                    sim = compareTfIdf(targetGloss, contextGloss, t.getStem());
                }
                if (verbose) {
                    Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Syn misure: {0}", sim);
                }
                score[j] = sim;
                N += sim;

            }
            for (int j = 0; j < score.length; j++) {
                score[j] = alfa * (score[j] / N) + beta * computeZIPF(j, score.length, JIGSAW.s_adj);
                if (score[j] > maxSim) {
                    maxSim = score[j];
                    pos = j;
                }
                buf.append(syns[j]).append("/").append(score[j]);
                if (j < syns.length - 1) {
                    buf.append(" ");
                }
            }
            if (t.getSyns().length > 0) {
                if (shortOutput) {
                    if (pos == -1) {
                        t.setSyn(syns[0]);
                    } else {
                        if (maxSim >= cut) {
                            t.setSyn(syns[pos]);
                        }
                    }
                } else {
                    if (pos == -1) {
                        t.setSyn(syns[0] + "/1");
                    } else {
                        t.setSyn(buf.toString());
                    }
                }
            } else {
                t.setSyn("U");
            }
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * Disambiguate the token group
     *
     * @param tg Token group
     */
    public void setSyn(TokenGroup tg) {
        try {
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Start disambiguation (text)...");
            }
            for (int tg_i = 0; tg_i < tg.size(); tg_i++) {
                if (tg.get(tg_i).getPosTag().equals("v")) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Token: {0} POS-tag: {1}", new Object[]{tg.get(tg_i).getToken(), tg.get(tg_i).getPosTag()});
                    }
                    TokenGroup context = this.getContext(tg, tg_i, false);
                    setSynVerb(tg.get(tg_i), context);
                }
            }
            boolean call_noun = false;
            for (int tg_i = 0; tg_i < tg.size(); tg_i++) {
                if (tg.get(tg_i).getPosTag().equals("n") && !call_noun) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Token: {0} POS-tag: {1}", new Object[]{tg.get(tg_i).getToken(), tg.get(tg_i).getPosTag()});
                    }
                    TokenGroup nouns = this.getNouns(tg);
                    setSynNouns(nouns);
                    call_noun = true;
                } else if (tg.get(tg_i).getPosTag().equals("a") || tg.get(tg_i).getPosTag().equals("r")) {
                    if (verbose) {
                        Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Token: {0} POS-tag: {1}", new Object[]{tg.get(tg_i).getToken(), tg.get(tg_i).getPosTag()});
                    }
                    TokenGroup context = this.getContext(tg, tg_i, false);
                    setSynAdjAdv(tg.get(tg_i), context);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to disambiguate text", ex);
        }
    }

    /**
     * Disambiguate a specific word
     *
     * @param tg Token group
     * @param lemmas List of lemmas
     * @param index Word position in the token group
     * @throws Exception Exception
     */
    public void setSyn(TokenGroup tg, List<?> lemmas, int index) {
        try {
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Start disambiguation token...");
            }
            if (verbose) {
                Logger.getLogger(JIGSAW.class.getName()).log(Level.INFO, "Token: {0} POS-tag: {1}", new Object[]{tg.get(index).getToken(), tg.get(index).getPosTag()});
            }
            if (tg.get(index).getPosTag().equals("n")) {
                int countVerb = 0;
                int i = index - 1;
                while (i >= 0 && countVerb < maxVerb) {
                    if (tg.get(i).getPosTag().equals("v")) {
                        TokenGroup context = this.getContext(tg, i, false);
                        setSynVerb(tg.get(i), context);
                        countVerb++;
                    }
                    i--;
                }
                countVerb = 0;
                i = index + 1;
                while (i < tg.size() && countVerb < maxVerb) {
                    if (tg.get(i).getPosTag().equals("v")) {
                        TokenGroup context = this.getContext(tg, i, false);
                        setSynVerb(tg.get(i), context);
                        countVerb++;
                    }
                    i++;
                }
                TokenGroup nouns = this.getNouns(tg);
                setSynNouns(nouns);
            } else if (tg.get(index).getPosTag().equals("v")) {
                TokenGroup context = this.getContext(tg, index, false);
                setSynVerb(tg.get(index), context);
            } else if (tg.get(index).getPosTag().equals("a") || tg.get(index).getPosTag().equals("r")) {
                TokenGroup context = this.getContext(tg, index, false);
                setSynAdjAdv(tg.get(index), context);
            }

        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to disambiguate token", ex);
        }

    }

    private List<String> convertArrayToList(String[] array) {
        return Arrays.asList(array);
    }

    private String convertArrayToNGram(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = list.size() - 1; i >= 0; i--) {
            sb.append(list.get(i)).append(" ");
        }
        return sb.toString().trim();
    }

    private String convertArrayToNGramStem(List<String> list, List<String> lemmas, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(lemmas.get(start + 1 + i)).append(" ");
        }
        return sb.toString().trim();
    }

    //NOT USED
    public List<String> foundNGram(String text) throws Exception {
        List<String> list = new ArrayList<String>();
        if (text == null) {
            return list;
        }
        String[] tokens = textProcessing.tokenize(text);
        String[] posA = textProcessing.posTagging(tokens);
        List<String> pos = convertArrayToList(posA);
        List<String> stems = new ArrayList<String>();
        List<String> lemmas = new ArrayList<String>();
        for (int i = 0; i < tokens.length; i++) {
            stems.add(textProcessing.stem(tokens[i]));
            String wnPos = Tag2Wn.getPos(pos.get(i));
            lemmas.add(wordNet.lemmatize(tokens[i], wnPos));
        }
        int i = 0;
        List<String> ngram = new ArrayList<String>();
        for (i = tokens.length - 1; i > 0; i--) {
            int insPos = i;
            ngram.clear();
            for (int j = 0; j < lookGram; j++) {
                if ((i - j) < 0) {
                    break;
                }
                ngram.add(tokens[i - j]);
                insPos--;
            }
            if (ngram.size() == 1) {
                list.add(0, tokens[i]);
                continue;
            }
            String posCode = null;
            while (ngram.size() > 1) {
                posCode = wordNet.hasAnySyns(convertArrayToNGram(ngram), false);
                if (posCode == null) {
                    posCode = wordNet.hasAnySyns(convertArrayToNGramStem(ngram, lemmas, insPos), false);
                }
                if (posCode == null) {
                    ngram.remove(ngram.size() - 1);
                    insPos++;
                } else {
                    break;
                }
            }
            if (posCode != null) {
                if (pos != null) {
                    pos.add(insPos + 1, Tag2Wn.getPos(posCode));
                    for (int j = 0; j < ngram.size(); j++) {
                        pos.remove(insPos + 2);
                    }
                }
                if (stems != null) {
                    stems.add(insPos + 1, convertArrayToNGramStem(ngram, stems, insPos));
                    for (int j = 0; j < ngram.size(); j++) {
                        stems.remove(insPos + 2);
                    }

                }
                if (lemmas != null) {
                    lemmas.add(insPos + 1, convertArrayToNGramStem(ngram, lemmas, insPos));
                    for (int j = 0; j < ngram.size(); j++) {
                        lemmas.remove(insPos + 2);
                    }

                }
                list.add(0, convertArrayToNGram(ngram));
                i = insPos + 1;
            } else {
                list.add(0, tokens[i]);
            }
        }
        if (i == 0) {
            list.add(0, tokens[i] + "\n");
        }
        return list;
    }

    public TokenGroup mapText(String text) throws Exception {
        String[] tokens = textProcessing.tokenize(text);
        String[] pos = textProcessing.posTagging(tokens);
        String[] stems = new String[tokens.length];
        String[] lemmas = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            stems[i] = textProcessing.stem(tokens[i]);
            String wnPos = Tag2Wn.getPos(pos[i]);
            lemmas[i] = wordNet.lemmatize(tokens[i], wnPos);
        }
        TokenGroup tg = this.getToken(tokens, pos, stems, lemmas, true);
        setSyn(tg);
        for (int j = 0; j < tg.size(); j++) {
            Token t = tg.get(j);
            if (t.getSyn() != null && !t.getSyn().equals("U")) {
                if (this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynset(t));
                } else if (!this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynsetFull(t));
                } else {
                    t.setSyn(t.getSyn());
                }
            } else {
                t.setSyn("U");
            }
        }
        return tg;
    }

    public TokenGroup mapText(String[] tokens) throws Exception {
        String[] pos = textProcessing.posTagging(tokens);
        String[] stems = new String[tokens.length];
        String[] lemmas = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            stems[i] = textProcessing.stem(tokens[i]);
            String wnPos = Tag2Wn.getPos(pos[i]);
            lemmas[i] = wordNet.lemmatize(tokens[i], wnPos);
        }
        TokenGroup tg = this.getToken(tokens, pos, stems, lemmas, true);
        setSyn(tg);
        for (int j = 0; j < tg.size(); j++) {
            Token t = tg.get(j);
            if (t.getSyn() != null && !t.getSyn().equals("U")) {
                if (this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynset(t));
                } else if (!this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynsetFull(t));
                } else {
                    t.setSyn(t.getSyn());
                }
            } else {
                t.setSyn("U");
            }
        }
        return tg;
    }

    public TokenGroup mapText(String[] tokens, String[] posTag) throws Exception {
        String[] stems = new String[tokens.length];
        String[] lemmas = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            stems[i] = textProcessing.stem(tokens[i]);
            lemmas[i] = wordNet.lemmatize(tokens[i], posTag[i]);
        }
        TokenGroup tg = this.getToken(tokens, posTag, stems, lemmas, false);
        setSyn(tg);
        for (int j = 0; j < tg.size(); j++) {
            Token t = tg.get(j);
            if (t.getSyn() != null && !t.getSyn().equals("U")) {
                if (this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynset(t));
                } else if (!this.isShortOutput() && this.isPosTagNotation()) {
                    t.setSyn(formatPosTagSynsetFull(t));
                } else {
                    t.setSyn(t.getSyn());
                }
            } else {
                t.setSyn("U");
            }
        }
        return tg;
    }

   
    public boolean isVerbose() {
        return verbose;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setMisure(int misure) {
        this.measure = misure;
    }
    
    public int getMaxVerb() {
        return maxVerb;
    }
    
    public void setMaxVerb(int maxVerb) {
        this.maxVerb = maxVerb;
    }
    
    public int getCommonDepth() {
        return this.commonDepth;
    }
    
    public void setCommonDepth(int commonDepth) {
        this.commonDepth = commonDepth;
    }
    
    public double getAlfa() {
        return this.alfa;
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public double getBeta() {
        return this.beta;
    }
    
    public void setBeta(double beta) {
        this.beta = beta;
    }
    
    public double getTheta() {
        return this.theta;
    }
    
    public void setTheta(double theta) {
        this.theta = theta;
    }
    
    public double getSigma() {
        return sigma;
    }
    
    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public int getLookGram() {
        return lookGram;
    }
    
    public void setLookGram(int lookGram) {
        this.lookGram = lookGram;
    }

    public boolean isShortOutput() {
        return shortOutput;
    }

    public void setShortOutput(boolean shortOutput) {
        this.shortOutput = shortOutput;
    }

    public boolean isPosTagNotation() {
        return posTagNotation;
    }

    public void setPosTagNotation(boolean posTagNotation) {
        this.posTagNotation = posTagNotation;
    }

    private String formatPosTagSynset(Token t) {
        if (t.getSyn() == null) {
            return "U";
        }
        if (t.getSyn().equals("U")) {
            return t.getSyn();
        }
        if (t.getPosTag().equals("o")) {
            return "U";
        }
        return t.getPosTag() + formatSynset(t.getSyn());
    }

    private String formatPosTagSynsetFull(Token t) {
        if (t.getSyn() == null) {
            return "U";
        }
        if (t.getSyn().equals("U")) {
            return t.getSyn();
        }
        if (t.getPosTag().equals("o")) {
            return "U";
        }
        if (t.getSyn().length() == 0) {
            return "U";
        }
        StringBuilder buf = new StringBuilder();
        try {
            String[] synsets = t.getSyn().split(" ");
            for (int i = 0; i < synsets.length; i++) {
                String[] tokens = synsets[i].split("/");
                String syn = tokens[0];
                String score = tokens[1];
                syn = t.getPosTag() + formatSynset(syn);
                buf.append(syn);
                buf.append(":");
                buf.append(score);
                if (i < synsets.length - 1) {
                    buf.append(",");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, "Error to format output: " + t.getSyn(), ex);
        }
        return buf.toString();
    }

    private String formatSynset(String string) {
        while (string.length() < 8) {
            string = "0" + string;
        }
        return string;
    }

    public double getCut() {
        return cut;
    }

    public void setCut(double cut) {
        this.cut = cut;
    }

    public static void main(String[] args) {
    	JIGSAW jigsaw = new JIGSAW();
    	HashMap<String,ArrayList<String>> senses = jigsaw.getWordSenses("Tom loves his chair",0);
    	for(String s : senses.keySet()){
    		for(String s1 : senses.get(s)){
    			System.out.print(s1+"::");
    		}
    		System.out.println();
    	}
    }

    public HashMap<String,ArrayList<String>> getWordSenses(String sentence,int index) {
    	HashMap<String,ArrayList<String>> wordSenseMap = new HashMap<String,ArrayList<String>>();
    	try {
//    		String path = JIGSAW.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//    		path = path.substring(0, path.length()-1);
//    		path = path.substring(0, path.lastIndexOf("/")+1);
    		init();
    		TokenGroup tg = this.mapText(sentence);
    		if (tg != null) {
    			for (int i = 0; i < tg.size(); i++) {
    				String wordInSentence = tg.get(i).getToken();
    				String posOffset = tg.get(i).getSyn();
    				if(posOffset.equalsIgnoreCase("U")){
						ArrayList<String> tempList = new ArrayList<String>(3);
						tempList.add(0,null);
						tempList.add(1,null);
						tempList.add(2,null);
						wordSenseMap.put(wordInSentence.toLowerCase()+"_"+(index+i+1), tempList);
    				}else{
    					String offset = posOffset.substring(1);
    					String pos = posOffset.substring(0,1);
    					Synset synset = wordNet.returnSynset(offset, pos);
    					List<Word> word = synset.getWords();
    					String firstWord = word.get(0).getLemma();
    					String lexFile = synset.getLexFileName();
    					ArrayList<String> tempList = new ArrayList<String>(3);
    					tempList.add(0,offset);
    					tempList.add(1,lexFile);
    					tempList.add(2,firstWord);
    					wordSenseMap.put(wordInSentence.toLowerCase()+"_"+(index+i+1), tempList);
    				}
    			}
    		}
    	} catch (Exception ex) {
    		Logger.getLogger(JIGSAW.class.getName()).log(Level.SEVERE, null, ex);
    	}
    	
    	this.wordNet.destroy();
    	return wordSenseMap;
    }

}
