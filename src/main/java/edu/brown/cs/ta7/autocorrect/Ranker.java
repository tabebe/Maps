package edu.brown.cs.ta7.autocorrect;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Collections;


public class Ranker {
  private static HashMap<String, Integer> unigramHash;
  private static HashMap<String, Integer> bigramHash;


  //Ranker constructor
  public Ranker() {
    unigramHash = new HashMap<String, Integer>();
    bigramHash = new HashMap<String, Integer>();
  }

  public HashMap<String, Integer> getUnigram() {
    return unigramHash;
  }

  public HashMap<String, Integer> getBigram() {
    return bigramHash;
  }

  public void addToHash(String word, boolean uni) {
    if (uni) {
      if (unigramHash.containsKey(word)) {
        int count = unigramHash.get(word);
        count++;
        unigramHash.put(word, count);
      } else {
        unigramHash.put(word, 1);
      }
    } else {
      if (bigramHash.containsKey(word)) {
        int count = bigramHash.get(word);
        count++;
        bigramHash.put(word, count);
      } else {
        bigramHash.put(word, 1);
      }
    }
  }

  public List<String> rankthis(List<String> matches, String[] previous) {
    List<String> ranked = new ArrayList<String>();

    Collections.sort(matches, Ranker.rankCompare(previous));
    if (matches.size() < 5) {
      ranked = matches.subList(0, matches.size());
    } else {
      ranked = matches.subList(0, 5);
    }

    return ranked;
  }


  static Comparator<String> rankCompare(String[] previous) {
    return new Comparator<String>() {
      public int compare(String one, String two) {
        String word1 = one.split(" ")[0];
        String word2 = two.split(" ")[0];
        
        if (previous.length == 2) {
          if (word1.equals(previous[1]) && !(word2.equals(previous[1]))) {
            return -1;
          } else if (!(word1.equals(previous[1]))) {
            return 1;
          }
          word1 = previous[0] + " " + word1;
          word2 = previous[0] + " " + word2;
        } else if (previous.length == 1) {
            if (word1.equals(previous[0]) && !word2.equals(previous[0])) {
              return -1;
            } else if (!word1.equals(previous[0]) && word2.equals(previous[0])) {
              return 1;
            }
        }


        if (bigramHash.get(word1) != null & bigramHash.get(word1) != null) {
            // Condition where bigram probablity is equal
            if (Integer.compare(bigramHash.get(word1), bigramHash.get(word2)) == 0) {
              String firstWord = word1.split(" ")[1];
              String secondWord = word2.split(" ")[1];
                  // Condition where unigram probablity is qual
                  if (Integer.compare(unigramHash.get(firstWord), unigramHash.get(secondWord)) == 0) {
                      return firstWord.compareTo(secondWord);
                  } else {
                    return -Integer.compare(unigramHash.get(firstWord), unigramHash.get(secondWord));
                  }

            } else {
              return -Integer.compare(bigramHash.get(word1), bigramHash.get(word2));
            }

        } else if (bigramHash.get(word1) != null && bigramHash.get(word2) == null) {
          return -1;
        } else if (bigramHash.get(word1) == null && bigramHash.get(word2) != null) {
          return 1;
        } else {
              String onew = "";
              String twow = "";
              String[] firstw = word1.split(" ");
              String[] secondw = word2.split(" ");
              if (firstw.length == 2) {
                onew = firstw[1];
              } else {
                onew = firstw[0];
              }

              if (secondw.length == 2) {
                twow = secondw[1];
              } else {
                twow = secondw[0];
              }

              if (Integer.compare(unigramHash.get(onew), unigramHash.get(twow)) != 0) {
                return -Integer.compare(unigramHash.get(onew), unigramHash.get(twow));
              } else {
                return onew.compareTo(twow);
              }
        }

      }
    };
  }
























}
