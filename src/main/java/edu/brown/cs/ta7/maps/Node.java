package edu.brown.cs.ta7.maps;
import edu.brown.cs.ta7.findPath.Edge;
import edu.brown.cs.ta7.findPath.Vertex;
/**
 * Node class. This class store Node's id and it's a way it's connected to. 
 * @author ta7
 *
 */

public class Node extends Vertex<String> {
  private String myID;
  private String fromWay;
  private String fromWayID;

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
  public boolean equals(Object o) {
    if (o instanceof Node) {
      //type cast
      Node a = (Node) o;
      return (this.getID().equals(a.getID()));
    }
    return false;
  }

}
