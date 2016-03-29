package edu.brown.cs.ta7.autocorrect;

import java.util.Comparator;


/**
 * @author rwdodd
 *
 */
public class SuggComparator implements Comparator<Suggestion> {
  private final String original;
  private final boolean isSmart;

  /**
   * @param original original input string
   * @param isSmart if smart rank is on
   */
  public SuggComparator(String original, boolean isSmart) {
    this.original = original;
    this.isSmart = isSmart;
  }

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Suggestion s1, Suggestion s2) {
    if (s1.getSugg().equals(s2.getSugg())) {
      return 0;
    }
    if (s1.getSugg().equals(original)) {
      return -1;
    } else if (s2.getSugg().equals(original)) {
      return 1;
    }
    if (isSmart && s1.getType().equals("wspace")
        && !s2.getType().equals("wspace")) {
      return -1;
    } else if (isSmart && s2.getType().equals("wspace")
        && !s1.getType().equals("wspace")) {
      return 1;
    }
    Integer val = s2.getBCount() - s1.getBCount();
    if (val < 0) {
      val = -1;
    } else if (val > 0) {
      val = 1;
    } else {
      val = s2.getUCount() - s1.getUCount();
      if (val < 0) {
        val = -1;
      } else if (val > 0) {
        val = 1;
      } else {
        val = s1.getComp().compareTo(s2.getComp());
      }
    }
    return val;
  }
}
