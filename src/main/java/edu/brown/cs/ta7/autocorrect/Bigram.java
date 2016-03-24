package edu.brown.cs.rwdodd.auto;

import com.google.common.base.Objects;

/**
 * @author rwdodd
 *
 */
public class Bigram {
  private final String word1;
  private final String word2;

  /**
   * @param word1 first half of bigram
   * @param word2 second half of bigram
   */
  public Bigram(String word1, String word2) {
    this.word1 = word1;
    this.word2 = word2;
  }

  /**
   * @return first word
   */
  public String getWord1() {
    return word1;
  }

  /**
   * @return second word
   */
  public String getWord2() {
    return word2;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(word1, word2);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    return (o instanceof Bigram) && ((Bigram) o).getWord1().equals(word1)
        && ((Bigram) o).getWord2().equals(word2);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return word1 + " " + word2;
  }
}
