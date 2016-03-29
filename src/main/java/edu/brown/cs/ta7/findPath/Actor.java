package edu.brown.cs.ta7.findPath;




/**
 * This is an extension of the generic Vertex class. 
 * This class stores the name of an actor, the name of a movie, 
 * and the movie's ID, which are used for finding the shortest path.  
 * @author ta7
 *
 */



public class Actor extends Vertex<String> {
	private final String name; 
	private String myMovie; 
	private String myMovieID; 


	/**
	 * Actor construct
	 * @param id - id of the actor
	 * @param name - name of the actor
	 */
	public Actor(String id, String name) {
		super(id);
		this.name = name;
		myMovie = "";
		myMovieID = "";
	}

	/**
	 * gets actor's name
	 * @return name
	 */
	public String getName() {
		return name; 
	}

	/**
	 * sets the name of a movie
	 * @param movie
	 */
	public void setMovie(String movie) {
		this.myMovie = movie; 
	}

	/**
	 * sets the id of a movie
	 * @param id
	 */
	public void setMovieID(String id)  {
		myMovieID = id; 
	}

	/**
	 * gets the name of the movie
	 * @return movie name
	 */
	public String getMovie() {
		return myMovie;
	}

	/**
	 * gets the id of a movie
	 * @return movie id
	 */
	public String getMovieID() {
		return myMovieID;
	}

	/**
	 * This overrides the hashCode so that this hashmap 
	 * looks for actor vertices. 
	 */
	@Override
	public int hashCode() {
		return this.getObject().hashCode();
	}

	/**
	 * This overrides equals so that we check if the names of 
	 * actors are equal
	 */
	@Override
	public boolean equals(Object e) {
		if (e instanceof Actor) {
			Actor actor = (Actor) e;
			return (this.getName().equals(actor.getName()));
		}
		return false; 
	}

}