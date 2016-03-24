package edu.brown.cs.rwdodd.auto;

import java.util.ArrayList;

/**
 * @author rwdodd
 *
 */
public class TrieNode {

  private final Integer ASCII_OFFSET = 97;
  private final Integer NUM_CHARS = 26;
  private final char nChar;
  private TrieNode[] children = new TrieNode[NUM_CHARS];
  private boolean isWord = false;

  /**
   * @param nChar the character stored in node
   */
  public TrieNode(char nChar) {
    this.nChar = nChar;
  }

  /**
   * @return the char
   */
  public char getChar() {
    return nChar;
  }
  /**
   * @param childChar to be added
   * @return the child
   */
  public TrieNode addChild(char childChar) {
    TrieNode child = new TrieNode(childChar);
    children[(childChar - ASCII_OFFSET)] = child;
    return child;
  }
  /**
   * @param childChar find this child node
   * @return returns the node corresponding
   * to the char
   */
  public TrieNode getChild(char childChar) {
    return children[(childChar) - ASCII_OFFSET];
  }

  /**
   * @return the nodes children nodes
   */
  public ArrayList<TrieNode> getChildren() {
    ArrayList<TrieNode> arr = new ArrayList<TrieNode>();
    for (int i = 0; i < NUM_CHARS; i++) {
      if (children[i] != null) {
        arr.add(children[i]);
      }
    }
    return arr;
  }

  /**
   * @param childChar check if this is a child
   * @return true if char is a child
   */
  public boolean isChild(char childChar) {
    return children[(childChar) - ASCII_OFFSET] != null;
  }

  /**
   * sets the current node as a
   * word ending.
   */
  public void setWordFalse() {
    isWord = false;
  }

  /**
   * sets isWord as true.
   */
  public void setWordTrue() {
    isWord = true;
  }

  /**
   * @return whether node is the end of a word
   */
  public boolean getIsWord() {
    return isWord;
  }

  //search for child in an array of 26 and convert
  //Char to ascii int and use as index
}
