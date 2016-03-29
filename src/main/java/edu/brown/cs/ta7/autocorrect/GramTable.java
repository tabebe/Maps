package edu.brown.cs.ta7.autocorrect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author rwdodd
 *
 */
public class GramTable {

  private HashMap<Bigram, Integer> bigramCounts
    = new HashMap<Bigram, Integer>();
  private HashMap<String, Integer> unigramCounts
    = new HashMap<String, Integer>();
  public GramTable(List<File> filenames) {
    for (File filename: filenames) {
      createGrams(filename);
    }
  }

  /**
   * @param filename file to use
   * for creating the unigrams and
   * bigrams
   */
  public void createGrams(File filename) {
    FileReader file = null;

    BufferedReader reader = null;
    try {
      file = new FileReader(filename);
      reader = new BufferedReader(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    String line;
    String lastWord = null;
    try {
      while ((line = reader.readLine()) != null) {
        line = line.toLowerCase();
        line = line.replaceAll("[^a-z]", " ");
        line = line.replaceAll("\\.", " ");
        line = line.replaceAll("\\s+", " ");
        String[] words = line.split(" ");
        if (words.length > 0) {
          String fWord = words[0];
          String sWord;
          if (lastWord != null) {
            addBigram(lastWord, fWord);
          }
          addUnigram(fWord);
          for (int i = 1; i < words.length; i++) {
            sWord = words[i];
            lastWord = sWord;
            addBigram(fWord, sWord);
            addUnigram(sWord);
            fWord = sWord;
          }
          if (words.length == 1) {
            lastWord = fWord;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addBigram(String word1, String word2) {
    if (word1.equals("") || word2.equals("")
        || word1.equals(" ") || word2.equals(" ")) {
      return;
    }
    Bigram bigram = new Bigram(word1, word2);
    Integer count = bigramCounts.get(bigram);
    if (count == null) {
      bigramCounts.put(bigram, 1);
    } else {
      bigramCounts.put(bigram, count + 1);
    }
  }

  private void addUnigram(String word) {
    Integer count = unigramCounts.get(word);
    if (count == null) {
      unigramCounts.put(word, 1);
    } else {
      unigramCounts.put(word, count + 1);
    }
  }

  /**
   * @param bigram the bigram to fetch count
   * @return the count of this bigram
   */
  public Integer getBigram(Bigram bigram) {
    return bigramCounts.get(bigram);
  }

  /**
   * @param word the word to fetch count
   * @return the count of this unigram
   */
  public Integer getUnigram(String word) {
    return unigramCounts.get(word);
  }

}
