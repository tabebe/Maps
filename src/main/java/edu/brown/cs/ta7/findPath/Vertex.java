package edu.brown.cs.ta7.bacon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;




/**
 * This is a super class named Vertex that takes an object T. This 
 * class is used to build the graph based on the relationship between a
 * vertex and edges, which are stored in this class as a list. 
 * @author ta7
 *
 * @param <T> an object
 */


public class Vertex<T> {
	private T object;
	private double distance;
	private Vertex<T> previous; 
	private List<Edge<T>> edges; 


	/**
	 * Vertex Constructor
	 * @param object, an object T
	 */
	public Vertex(T object) {
		if (object == null) {
			throw new IllegalArgumentException("ERROR: Null object passed as an arugment.");
		}

		this.object = object; 
		distance = Double.POSITIVE_INFINITY; 
		edges = new ArrayList<Edge<T>>();
		previous = null;
	}

	/**
	 * Adds an edge to the list of edges. 
	 * @param edge, an edge
	 */
	public void addEdge(Edge<T> edge) {
		edges.add(edge);
	}

	/**
	 * gets the edges of this vertex
	 * @return list of edges
	 */
	public List<Edge<T>> getEdges() {
		return edges;
	}

	/**
	 * sets the previous pointer
	 * @param vertex, new previous pointer vertex
	 */
	public void setPrev(Vertex<T> vertex) {
		previous = vertex; 
	}

	/**
	 * gets the previous pointer
	 * @return previous pointer vertex
	 */
	public Vertex<T> getPrev() {
		return previous;
	}

	/**
	 * gets the object stored in this vertex
	 * @return object T
	 */
	public T getObject() {
		return object;
	}

	/**
	 * sets the distance it takes to reach the current vertex
	 * @param distance
	 */
	public void setDistance(double distance) {
		this.distance = distance; 
	}

	/**
	 * gets the distance it took to reach the current vertex
	 * @return distance 
	 */
	public double getDistance() {
		return distance; 
	}

	/**
	 * This comparator is used to sort the vertices
	 * according to their distance
	 * @return object T
	 */
	public static <T> Comparator<Vertex<T>> compareVertex() {
		return new Comparator<Vertex<T>>() {
			@Override 
			public int compare(Vertex<T> v1, Vertex<T> v2) {
				double first = v1.getDistance();
				double second = v2.getDistance();
				return Double.compare(first, second);
			}
		};
	}


}