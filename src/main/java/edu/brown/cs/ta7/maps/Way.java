package edu.brown.cs.ta7.maps;
import edu.brown.cs.ta7.findPath.Edge;





public class Way extends Edge<String> {

private String id; 
private String name;



/**
   * Constructor that builds a way edge given
   * the following parameters. Cannot create
   * an edge if either of the two vertices are null (no dangling edges).
   * @param start Node 
   * @param end Node
   * @param name Name of way
   * @param id ID of way
   */
public Way(Node start, Node end, String name, String id) {
	super(start, end, 1);
	this.name = name;
	this.id = id;
}

/**
   * returns the way name it stores.
   * @return name movie's name.
   */
public String getName() {
	return name;
}


/**
   * returns the ways' ID.
   * @return id movie's id
   */
public String getID() {
	return id;
}


}