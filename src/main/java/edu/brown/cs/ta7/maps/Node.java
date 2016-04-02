package edu.brown.cs.ta7.maps;
import java.util.ArrayList;
import java.util.Comparator;

import edu.brown.cs.ta7.findPath.Edge;
import edu.brown.cs.ta7.findPath.Vertex;
/**
 * Node class. This class store Node's id and it's a way it's connected to. 
 * @author ta7
 *
 */

public class Node extends Vertex<String> {
	// properties for finding the shortest path
	private String myID;
	private String fromWay;
	private String fromWayID;
	
	// properties for finding the nearest node
	private double distFromTarg;
	private ArrayList<Double> coors;
	private Node leftChild;
	private Node rightChild;

  /**
   * Node constructor that takes in an ID and stores
   * that value for that particular instance.
   * @param nodeID id
   */
  public Node(String nodeID) {
	super(nodeID);
    this.myID = nodeID;
    this.fromWay = "";
    this.fromWayID = "";
  }

  /**
   * Sets the left node of the current node
   * @param left - left node
   */
  public void setLeft(Node left) {
	  leftChild = left;
  }
  
  /**
   * gets the left child
   * @return - node
   */
  public Node getleft() {
	  return leftChild;
  }
  
  /**
   * Sets the right node of the current node
   * @param right -- right node
   */
  public void setRight(Node right) {
	  rightChild = right;
  }
  
  /**
   * gets the right child
   * @return - node
   */
  public Node getRight() {
	  return rightChild;
  }
  
  /**
   * Sets the distance from the current node to 
   * the given target
   * @param dist - distance
   */
  public void setDistFromTarg(double dist) {
	  distFromTarg = dist;
  }
  
  /**
   * gets the distance
   * @return - double
   */
  public double getDistFromTarg() {
	  return distFromTarg;
  }
  
  
  public void setCoors(ArrayList<Double> coor) {
	  this.coors = coor;
  }
  
  /**
   * Gets the coordinates of the current node
   * @return - array of coordinates
   */
  public ArrayList<Double> getCoors() {
	  return coors;
  }
  
  
  /**
   * getID() returns the ID of the star.
   * @return myID
   */
  public String getID() {
    return myID;
  }

  /**
   * setWay() sets the way connecting to this node
   * 
   */
  public void setWay(String way) {
  	this.fromWay = way;
  }


  /**
   * setWayID() sets the wayID of the way connecting to this node
   * 
   */
  public void setWayID(String wayID) {
  	this.fromWayID = wayID;
  }


  /**
   * getWay() returns the name of the way connecting to this node
   * @return way name;
   */
  public String getWay() {
  	return fromWay;
  }


  /**
   * getWay() returns the name of the way connecting to this node
   * @return way id;
   */
  public String getWayID() {
  	return fromWayID;
  }

  
  @Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + ((myID == null) ? 0 : myID.hashCode());
	  return result;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj)
    	return true;
    if (obj == null)
    	return false;
    if (getClass() != obj.getClass()) 
    	return false;
    Node other = (Node) obj;
    
    if (myID == null) {
    	if (other.myID != null)
    		return false;
    } else if (!myID.equals(other.myID))
    	return false;
    return true;
  }
  
  @Override
  public String toString() {
	  return myID;
  }
  static  Comparator<Node> compareCoor(int axis) {
	    return new Comparator<Node>() {
	      @Override
	      public int compare(Node o1, Node o2) {
	        double coord1 = o1.getCoors().get(axis);
	        double coord2 = o2.getCoors().get(axis);
	        return Double.compare(coord1, coord2);
	      }
	    };
	  }
  
  
  static <T> Comparator<Node> compareFlippedDist() {
	    return new Comparator<Node>() {
	      @Override
	      public int compare(Node o1, Node o2) {
	        double coord1 = o1.getDistFromTarg();
	        double coord2 = o2.getDistFromTarg();
	        return -Double.compare(coord1, coord2);
	      }
	    };
	  }
  
  
  static <T> Comparator<Node> compareDist() {
	    return new Comparator<Node>() {
	      @Override
	      public int compare(Node o1, Node o2) {
	        double coord1 = o1.getDistFromTarg();
	        double coord2 = o2.getDistFromTarg();
	        return Double.compare(coord1, coord2);
	      }
	    };
	  }

}
