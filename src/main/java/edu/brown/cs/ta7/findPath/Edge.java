package edu.brown.cs.ta7.findPath;




/**
 * This is a generic edge class that stores the two 
 * vertices it's connected to as well as it's weight.
 * @author ta7
 *
 * @param <T> object held by edge
 */

public class Edge<T> {
	private final Vertex<T> head; 
	private final Vertex<T> tail; 
	private final double weight; 


	/**
	 * Edge constructor
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param wght weight
	 */
	public Edge(Vertex<T> v1, Vertex<T> v2, double wght) {
		if (v1 == null || v2 == null) {
			throw new IllegalArgumentException("ERROR: null vertex.");
		}
		head = v1;
		tail = v2;
		weight = wght;
	}

	/**
	 * gets the weight of this edge
	 * @return weight
	 */
	public double getWeight() {
		return weight;
	}


	/**
	 * gets the vertex at the other end of the edge
	 * @param vertex, the vertex of which we want the opposite
	 * @return opposite vertex
	 */
	public Vertex<T> getOppositeVertex(Vertex<T> vertex) {
		if (head.equals(vertex)) {
			return tail;
		} else if (tail.equals(vertex)) {
			return head;
		}

		throw new IllegalArgumentException("ERROR: edge without a vertex");
	}















}