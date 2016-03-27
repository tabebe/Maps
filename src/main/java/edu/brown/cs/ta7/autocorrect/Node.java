package edu.brown.cs.ta7.autocorrect;
import java.util.HashSet;

public class Node {
  private HashSet<Node> leafs;
  private char letter;
  private String word;


  /**
   * This is a Node constuctor that takes in a character, sets word to null
   * and creates a HashSet of Node
   * @param c char
   */
   public Node(char c) {
     this.word = null;
     this.leafs = new HashSet<Node>();
     this.letter = c;
   }

   public Node getLeaf(char c) {
     if (leafs != null) {
       for (Node n : leafs) {
         if (n.getLetter() == c) {
           return n;
         }
       }
     }
     return null;
   }


   public void addLeaf(char c) {
     leafs.add(new Node(c));
   }


   public void makeWord(String word) {
     this.word = word;
   }



   public char getLetter() {
     return letter;
   }

   public String getWord() {
     return this.word;
   }

   public HashSet<Node> getAllLeafs() {
     return leafs;
   }


}
