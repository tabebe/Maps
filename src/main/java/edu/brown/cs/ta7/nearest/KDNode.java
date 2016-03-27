package edu.brown.cs.ta7.nearest;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * KDNode class. This class takes in a generic object and
 * an arraylits of coordinates (doubles). It has accessors and mutator
 * methods to store/retrieve a left/right child (also a KDNode),
 * the node's coordinates (as an arraylist of doubles), and also
 * the distance from the current node to a particular target cooridinate
 * if that target coordinate is specified.
 * @author
 * @param <T> generic
 */

public class KDNode<T> {
  private double distFromTarg;
  private ArrayList<Double> coors;
  private T t;
  private KDNode<T> leftChild;
  private KDNode<T> rightChild;
  /**
   * KDNode constructor. Takes in a generic object and an arraylist
   * of doubles (coordinates) and stores both appropriately.
   * @param generic generic object
   * @param c arraylist of doubles (coordinates)
   */
  public KDNode(T generic, ArrayList<Double> c) {
    this.t = generic;
    this.coors = c;
  }
  /**
   * setLeft() mutator method that sets left child for curr node.
   * @param lchild left child node.
   */
  public void setLeft(KDNode<T> lchild) {
    leftChild = lchild;
  }
  /**
   * setRight() mutator method that sets right child for curr node.
   * @param rchild right child node.
   */
  public void setRight(KDNode<T> rchild) {
    rightChild = rchild;
  }
  /**
   * setDistFromTarg() accessor method that
   * stores the distance from this node to a given
   * target. used in PQ for nearest neighbor search.
   * @param dist double distnace from a target coordinate
   */
  public void setDistFromTarg(double dist) {
    distFromTarg = dist;
  }
  /**
   * getCoors() accessor method that returns the
   * coordinates that this node stores.
   * @return coors.
   */
  public ArrayList<Double> getCoors() {
    return coors;
  }
  /**
   * getObject() accessor method that returns
   * the object that this KDNode stores.
   * @return the object that this node stores
   */
  public T getObject() {
    return t;
  }
  /**
   * getLeft() accessor method  that reutrns the left child.
   * @return leftChild
   */
  public KDNode<T> getLeft() {
    return leftChild;
  }
  /**
   * getRight() accessor method that returns the right child.
   * @return rightChild
   */
  public KDNode<T> getRight() {
    return rightChild;
  }
  /**
   * getDistFromTarg() accessor method returns
   * dist from targ.
   * @return distFroMTarg
   */
  public double getDistFromTarg() {
    return distFromTarg;
  }
  /**
   * Comparator inner class to customize comparing
   * the node's object based on coordinates. used when
   * sorting the list of nodes per level in the
   * kdtree while building the tree.
   * @param axis the axis we're comparing
   * @return proper comparison values
   */
  static <T> Comparator<KDNode<T>> compareCoor(int axis) {
    return new Comparator<KDNode<T>>() {
      @Override
      public int compare(KDNode<T> o1, KDNode<T> o2) {
        double coord1 = o1.getCoors().get(axis);
        double coord2 = o2.getCoors().get(axis);
        return Double.compare(coord1, coord2);
      }
    };
  }
  /**
   * Comparator inner class to customize comparing
   * the node's distance from the target coor. The returned
   * comparison value is flipped since Java's built-in
   * PriorityQueue is minimum-based, meaning that the
   * node at the head of the PQ is the node with the
   * shortest distance from a target. My PQ is flipped for
   * reasons you can find in the comments of my KDTree
   * class or my README.
   * @return comparison
   */
  static <T> Comparator<KDNode<T>> compareFlippedDist() {
    return new Comparator<KDNode<T>>() {
      @Override
      public int compare(KDNode<T> o1, KDNode<T> o2) {
        double coord1 = o1.getDistFromTarg();
        double coord2 = o2.getDistFromTarg();
        return -Double.compare(coord1, coord2);
      }
    };
  }
  /**
   * comparator inner class for comparing the distance
   * between two nodes and a target coordinate.
   * @return comparison
   */
  static <T> Comparator<KDNode<T>> compareDist() {
    return new Comparator<KDNode<T>>() {
      @Override
      public int compare(KDNode<T> o1, KDNode<T> o2) {
        double coord1 = o1.getDistFromTarg();
        double coord2 = o2.getDistFromTarg();
        return Double.compare(coord1, coord2);
      }
    };
  }
}
