package edu.brown.cs.rwdodd.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author rwdodd
 *
 */
public class Repl {

  private Trie trie;
  private GramTable gramTable;
  private boolean prefix;
  private boolean wSpace;
  private boolean smart;
  private Integer maxDist;
  private final Integer MAX_RESULTS = 5;

  /**
   * @param files files to create objects with
   * @param maxDist led distance
   * @param prefix if prefix on
   * @param wSpace if wspace on
   * @param smart if smart ranking on
   */
  public Repl(List<File> files, Integer maxDist, boolean prefix, boolean wSpace, boolean smart) {
    this.maxDist = maxDist;
    this.prefix = prefix;
    this.wSpace = wSpace;
    this.smart = smart;
    trie = new Trie(files, maxDist);
    gramTable = new GramTable(files);
    parseExecute();
  }

  /**
   * parse command line and execute it.
   */
  public void parseExecute() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Ready");
      String line;
      while ((line = reader.readLine()) != null && line.length() != 0) {
        line = line.toLowerCase();
        char[] lineChars = line.toCharArray();
        line = line.replaceAll("[^a-z]", " ");
        line = line.replaceAll("\\.", " ");
        line = line.replaceAll("\\s+", " ");
        String[] lineArr = line.split(" ");

        if (lineChars[lineChars.length - 1] != ' '
            && lineArr.length != 0) {
          //trailing whitespace or empty input
          String lastWord = lineArr[lineArr.length - 1];
          ArrayList<String> levSuggs = new ArrayList<String>();
          ArrayList<String> preSuggs = new ArrayList<String>();
          ArrayList<Bigram> wspaceSuggs = new ArrayList<Bigram>();
          if (maxDist >= 0) {
            levSuggs = trie.levSearch(lastWord);
            //for(String s:levSuggs)
            //System.out.println(s);
          }
          if (prefix) {
            preSuggs = trie.prefixSearch(lastWord);
            //for(String s:preSuggs)
            //System.out.println(s);
          }
          if (wSpace) {
            wspaceSuggs = trie.whitespaceSearch(lastWord);
          }
          TreeSet<Suggestion> suggs;
          suggs = new TreeSet<Suggestion>(new SuggComparator(line, smart));
          //prioritises whitespace suggestions if smart
          //ranking is turned on
          String suggTemp = "";
          String prevWord;
          for (int i = 0; i < lineArr.length - 1; i++) {
            suggTemp = suggTemp + lineArr[i] + " ";
          }
          if (lineArr.length == 1) {
            prevWord = "";
            suggTemp = "";
          } else {
            prevWord = lineArr[lineArr.length - 2];
          }
          for (String s:levSuggs) {
            String strSugg = suggTemp + s;
            Integer bCount =
                gramTable.getBigram(new Bigram(prevWord, s));
            Integer uCount = gramTable.getUnigram(s);
            Suggestion sugg =
                new Suggestion("led", strSugg, bCount, uCount, s);
            suggs.add(sugg);
          }
          for (String s:preSuggs) {
            String strSugg = suggTemp + s;
            Integer bCount =
                gramTable.getBigram(new Bigram(prevWord, s));
            Integer uCount = gramTable.getUnigram(s);
            Suggestion sugg =
                new Suggestion("pre", strSugg, bCount, uCount, s);
            suggs.add(sugg);
          }
          for (Bigram b: wspaceSuggs) {
            String strSugg = suggTemp + b.getWord1()
                + " " + b.getWord2();
            Integer bCount =
                gramTable.getBigram(new Bigram(prevWord,
                    b.getWord1()));
            Integer uCount = gramTable.getUnigram(b.getWord1());
            String comp = b.getWord1() + " " + b.getWord2();
            Suggestion sugg =
                new Suggestion("wspace", strSugg, bCount, uCount, comp);
            suggs.add(sugg);
          }
          for (int i = 0; i < MAX_RESULTS; i++) {
            Suggestion sugg = suggs.pollFirst();
            if (sugg == null) {
              break;
            }
            System.out.println(sugg.toString());
          }

        }
        System.out.println();

      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
