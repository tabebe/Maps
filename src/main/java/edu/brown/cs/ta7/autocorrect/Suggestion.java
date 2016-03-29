package edu.brown.cs.ta7.autocorrect;

import com.google.common.base.Objects;

/**
 * @author rwdodd
 *
 */
public class Suggestion {
  private final String sugg;
  private final Integer bCount;
  private final Integer uCount;
  private final String comp;
  private final String type;

  /**
   * @param type type of suggestion
   * @param sugg the returned suggestion
   * @param bCount count of the related bigram
   * @param uCount count of related unigram
   * @param comp the string to use for comparing
   */
  public Suggestion(String type, String sugg, Integer bCount, Integer uCount, String comp) {
    this.type = type;
    this.sugg = sugg;
    if (bCount == null) {
      this.bCount = 0;
    } else {
      this.bCount = bCount;
    }
    if (uCount == null) {
      this.uCount = 0;
    } else {
      this.uCount = uCount;
    }
    this.comp = comp;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }
  /**
   * @return suggestion string
   */
  public String getSugg() {
    return sugg;
  }
  /**
   * @return bigram count
   */
  public Integer getBCount() {
    return bCount;
  }
  /**
   * @return unigram count
   */
  public Integer getUCount() {
    return uCount;
  }
  /**
   * @return the comparison string
   */
  public String getComp() {
    return comp;
  }
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return sugg;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    return (o instanceof Suggestion)
        && ((Suggestion) o).getSugg().equals(sugg);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(sugg);
  }
}
