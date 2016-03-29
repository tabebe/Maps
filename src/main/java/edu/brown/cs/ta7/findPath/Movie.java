package edu.brown.cs.ta7.findPath;



/**
 * This class is an extension of the generic class Edge.
 * Movie takes in everything an Edge takes, plus a movie name
 * and a movie ID. 
 * @author ta7
 *
 */

public class Movie extends Edge<String> {
	private String movieName;
	private String movieID; 


	/**
	 * Movie edge constructor. 
	 * @param a1 first actor
	 * @param a2  second actor
	 * @param wght	weight of edge
	 * @param mov1	movie name	
	 * @param mov2 movie ID
	 */
	public Movie(Actor a1, Actor a2, double wght, String mov1, String mov2) {
		super(a1, a2, wght);
		movieName = mov1;
		movieID = mov2;	
	}


	/**
	 * gets the name of the movie
	 * @return movie name
	 */
	public String getMovieName() {
		return movieName;
	}

	/**
	 * gets the ID of the movie
	 * @return movie ID
	 */
	public String getMovieID() {
		return movieID;
	}
}