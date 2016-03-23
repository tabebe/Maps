package edu.brown.cs.ta7.autocorrect;
import java.util.ArrayList;

public class Trie {
  private Node root;
  private ArrayList<String> matches;
  private int size = 0;
  /* You can add a size parameter later for testing purposes.*/



  /**
  * Trie()
  * This is a Trie constructor that sets the root.
  */
  public Trie() {
    this.root = new Node(' ');
  }

 // getRoot() gets the root of the trie
  public Node getRoot() {
    return root;
  }
  
  
  public int getSize() {
	  return size;
  }
  /**
  * addWord()
  * This function adds a word to our trie is that word isn't already
  * in the trie.
  * @param word word
  */
  public void addWord(String word) {
    if (lookUp(word) != null) {
      return;
    }


    Node current = this.root;
    for (char c : word.toCharArray()) {
      Node leaf = current.getLeaf(c);
      if (leaf != null) {
        current = leaf;
      } else {
        // increment size for testing purposes
    	size++;
        current.addLeaf(c);
        current = current.getLeaf(c);
      }
    }
    current.makeWord(word);
  }

  /**
 * lookUp()
 * This function looks up the trie for a word and returns a node which
 *  contains the word. If not, it will return null;
 * @param word, word
 * @return Node, node that contains the word. Null
 */
 public Node lookUp(String word) {
   if (word.isEmpty()) {
     return null;
   }

   Node current = root;
   for (char c : word.toCharArray()) {
     if (current.getLeaf(c) == null) {
       return null;
     } else {
       current = current.getLeaf(c);
     }
   }

   if (current.getWord() == null) {
     return null;
   } else {
     return current;
   }
 }

 /**
   * prefixMatching()
   * This function looks for any possible word that could be
   * created using the exact given prefix. If the given input
   * is a word, no suggestions are given. If the given input is not
   * a word, the 5 closest suggestions are returned.
   * @param prefix prefix
   * @return arraylist of suggestions
   */
   public ArrayList<String> prefixMatching(String prefix) {
     matches = new ArrayList<String>();
     if (prefix.isEmpty()) {
       return matches;
     }

     Node current = root;
     for (char c : prefix.toCharArray()) {
       if (current.getLeaf(c) != null) {
         current = current.getLeaf(c);
       } else {
         return null;
       }
     }

     prefixHelper(current);
     return matches;
   }

   // Prefix helper that I use to recur
    public void prefixHelper(Node n) {
      if (n.getWord() != null) {
        matches.add(n.getWord());
      }
      for (Node nn : n.getAllLeafs()) {
        prefixHelper(nn);
      }
    }

    /**
      * whiteSpaceSplit()
      * This function looks through an input word to see if it is a comblination
      * of more than one word. If so, the first word found is split off.
      * @param word word
      * @return arraylist of suggestions
      */

      public ArrayList<String> whiteSpaceSplit(String word) {
        matches = new ArrayList<String>();
        if (word.isEmpty()) {
          return matches;
        }
        if (lookUp(word) != null) {
          matches.add(word);
        }

        Node current = root;
        String word1;
        String word2;
        char[] charArray = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
          if (current.getLeaf(charArray[i]) != null) {
            current = current.getLeaf(charArray[i]);
            if (current.getWord() != null) {
              Node substring = lookUp(word.substring(i + 1, word.length()));
              if (substring != null) {
                word1 = current.getWord();
                word2 = word.substring(i + 1, word.length());
                matches.add(word1 + " " + word2);
              }
            }
          }
        }
        return matches;
      }



















}
