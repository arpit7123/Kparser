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
package module.graph.pdtbp.resources;

import edu.stanford.nlp.trees.Tree;

/**
 * Class that associates tree node with it's tree root in order to be able to get it's unique node
 * number. The unique node number is needed while generating the dependency tree features.
 * 
 * @author ilija.ilievski@u.nus.edu
 */
public class TreeNode {

  /**
   * 
   */
  private Tree root;

  /**
   * 
   */
  private Tree node;

  /**
   * Tree number corresponds to the sentence number in the article.
   */
  private int treeNumber;

  /**
   * 
   * @param root
   * @param node
   * @param treeNumber
   */
  public TreeNode(Tree root, Tree node, int treeNumber) {
    this.root = root;
    this.treeNumber = treeNumber;
    this.node = node;
  }

  /**
   * @return the root
   */
  public Tree getRoot() {
    return root;
  }

  /**
   * @param root the root to set
   */
  public void setRoot(Tree root) {
    this.root = root;
  }

  /**
   * @return the node
   */
  public Tree getNode() {
    return node;
  }

  /**
   * @param node the node to set
   */
  public void setNode(Tree node) {
    this.node = node;
  }

  /**
   * @return the treeNumber
   */
  public int getTreeNumber() {
    return treeNumber;
  }

  /**
   * @param treeNumber the treeNumber to set
   */
  public void setTreeNumber(int treeNumber) {
    this.treeNumber = treeNumber;
  }


}
