package edu.brown.cs.ta7.autocorrect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rwdodd
 *
 */
public class Trie {

  private TrieNode root = new TrieNode('\0');
  private ArrayList<String> sugg = new ArrayList<String>();
  private ArrayList<Bigram> bigramSugg = new ArrayList<Bigram>();
  private final Integer maxDist;
  private StringBuilder strb;

  /**
   * @param filenames the files used to create
   * the trie
   * @param maxDist the max led distance
   */
  public Trie(List<File> filenames, Integer maxDist) {
    this.maxDist = maxDist;
    for (File filename: filenames) {
      createTree(filename);
    }
  }

  private void createTree(File filename) {
    FileReader file = null;

    BufferedReader reader = null;
    try {
      file = new FileReader(filename);
      reader = new BufferedReader(file);
    } catch (FileNotFoundException e) {
      System.out.println("ERROR: file doesn't exist");
      System.exit(1);
    }
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        line = line.toLowerCase();
        line = line.replaceAll("[^a-z]", " ");
        line = line.replaceAll("\\.", " ");
        line = line.replaceAll("\\s+", " ");
        String[] words = line.split(" ");
        for (String word: words) {
          addWord(word);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  
  
  private void addWord(String word) {
    TrieNode currNode = root;
    for (char c: word.toCharArray()) {
      if (currNode.isChild(c)) {
        currNode = currNode.getChild(c);
      } else {
        currNode = currNode.addChild(c);
      }
    }
    currNode.setWordTrue();
  }

  
  
  private int levDistance(String word1, String word2) {
    int[][] levMat = new int[word1.length() + 1][word2.length() + 1];
    for (int i = 0; i < word1.length() + 1; i++) {
      levMat[i][0] = i;
    }
    for (int i = 0; i < word2.length() + 1; i++) {
      levMat[0][i] = i;
    }
    char[] arr1 = word1.toCharArray();
    char[] arr2 = word2.toCharArray();
    for (int i = 1; i < word1.length() + 1; i++) {
      for (int j = 1; j < word2.length() + 1; j++) {
        if (Math.min(arr1[i - 1], arr2[j - 1]) == 0) {
          levMat[i][j] = Math.max(arr1[i - 1], arr2[j - 1]);
        } else {
          int subCost = 0;
          if (arr1[i - 1] == arr2[j - 1]) {
            subCost = 0;
          } else {
            subCost = 1;
          }
          levMat[i][j] = Math.min(Math.min(levMat[i][j - 1] + 1,
              levMat[i - 1][j] + 1),
              levMat[i - 1][j - 1] + subCost);
        }
      }
    }
    return levMat[word1.length()][word2.length()];
  }

  /**
   * @param word used to find suggestions
   * @return white space suggestions
   */
  public ArrayList<Bigram> whitespaceSearch(String word) {
    bigramSugg = new ArrayList<Bigram>();
    StringBuilder sb1 = new StringBuilder("");
    StringBuilder sb2 = new StringBuilder(word);
    whitespaceSearchHelp(sb1, sb2);
    return bigramSugg;
  }

  private void whitespaceSearchHelp(StringBuilder sb1, StringBuilder sb2) {
    while (sb2.length() != 1) {
      sb1.append(sb2.charAt(0));
      sb2.deleteCharAt(0);
      if (exists(sb1.toString()) && exists(sb2.toString())) {
        Bigram bigram = new Bigram(sb1.toString(), sb2.toString());
        bigramSugg.add(bigram);
      }
    }
  }

  /**
   * @param word used for prefix suggestions
   * @return prefix suggestions
   */
  public ArrayList<String> prefixSearch(String word) {
    sugg = new ArrayList<String>();
    //StringBuilder sb = new StringBuilder();
    char[] wordArr = word.toCharArray();
    TrieNode currNode = root;
    for (char c: wordArr) {
      if (currNode.isChild(c)) {
        currNode = currNode.getChild(c);
        //sb.append(c);
      } else {
        return sugg;
      }
    }
    if (currNode.getIsWord()) {
      sugg.add(word);
    }
    ArrayList<TrieNode> children = currNode.getChildren();
    if (children.size() != 0) {
      for (TrieNode child: children) {
        prefixSearchHelper(word, child);
      }
    }
    return sugg;
  }

  private void prefixSearchHelper(String word, TrieNode node) {
    //sb.append(node.getChar());
    String newWord = word + node.getChar();
    if (node.getIsWord()) {
      sugg.add(newWord);
    }
    ArrayList<TrieNode> children = node.getChildren();
    if (children.size() != 0) {
      for (TrieNode child: children) {
        prefixSearchHelper(newWord, child);
      }
    }

  }

  /**
   * @param word used for led search
   * @return led search suggestions
   */
  public ArrayList<String> levSearch(String word) {
    sugg = new ArrayList<String>();
    levSearchHelper("", word, root);
    return sugg;
  }

  private void levSearchHelper(String wordBuilder, String word, TrieNode node) {
    String newWord = wordBuilder;
    if (node.getChar() != '\0') {
      newWord = wordBuilder + node.getChar();
    }
    ArrayList<TrieNode> children = node.getChildren();
    if (node.getIsWord()) {
      int dist = levDistance(word, newWord);
      if (dist <= maxDist) {
        sugg.add(newWord);
        for (TrieNode next: children) {
          levSearchHelper(newWord, word, next);
        }
      } else {
        if (word.length() <= newWord.length()) {
          return;
        } else {
          for (TrieNode next: children) {
            levSearchHelper(newWord, word, next);
          }
        }
      }
    } else {
      for (TrieNode next: children) {
        levSearchHelper(newWord, word, next);
      }
    }

  }

  /**
   * @param word to check if exists in trie
   * @return true if exist o/w false
   */
  public boolean exists(String word) {
    boolean wordFound = true;
    char[] chars = word.toCharArray();
    TrieNode curNode = root;
    for (char c: chars) {
      if (curNode.isChild(c)) {
        curNode = curNode.getChild(c);
      } else {
        wordFound = false;
        break;
      }
    }
    return wordFound && curNode.getIsWord();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    strb = new StringBuilder();
    toStrHelp(root);
    return strb.toString();
  }

  private void toStrHelp(TrieNode node) {
    strb.append(node.getChar() + ": " + node.getIsWord() + '\n');
    for (TrieNode child: node.getChildren()) {
      toStrHelp(child);
    }
  }


}
