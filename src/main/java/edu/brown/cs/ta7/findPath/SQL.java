package edu.brown.cs.ta7.findPath;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * SQL class is in charge of querying the database. Each 
 * function is responsible for different queries. 
 * @author ta7
 *
 */
public class SQL {
	private Connection connection;



	/**
	 * SQL constructor that sets up the connection to our database. 
	 * @param database - path to database
	 * @throws ClassNotFoundException - path to database doesn't exist
	 * @throws SQLException - SQL error
	 */
	public SQL(String database) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:" + database);
		connection.createStatement();
	} 



	/**
	 * getAllActors() gets all the actors from the database
	 * @return - list of actors
	 * @throws SQLException - SQL error
	 */
	public List<String> getAllActors() throws SQLException {
		String query = "SELECT DISTINCT name FROM actor";
		PreparedStatement stat = connection.prepareStatement(query);
		ResultSet result = stat.executeQuery();
		List<String> toReturn = new ArrayList<String>();
		while(result.next()) {
			toReturn.add(result.getString(1));
		}
		stat.close();
		result.close();
		return toReturn;
	}

	
	/**
	 * getActorID gets the ID of an actor given the actor's name
	 * @param name - name of actor
	 * @return - ID of actor
	 * @throws SQLException - SQL error
	 */
	public String getActorID(String name) throws SQLException {
		String query = "SELECT id FROM actor WHERE name = ? LIMIT 1";
		PreparedStatement stat = connection.prepareStatement(query);
		stat.setString(1, name);
		ResultSet result = stat.executeQuery();
		String toReturn = "";

		if (result.next()) {
			toReturn = result.getString(1);
		}
		stat.close();
		result.close();
		return toReturn;
	}


	/**
	 * getActor gets the name of an actor given the actor's ID
	 * @param id - id of actor
	 * @return - name of actor
	 * @throws SQLException - SQL error
	 */
	public String getActor(String id) throws SQLException {
		String query = "SELECT name FROM actor WHERE id = ? LIMIT 1";
		PreparedStatement stat = connection.prepareStatement(query);
		stat.setString(1, id);
		ResultSet results = stat.executeQuery();

		String toReturn = "";
		if (results.next()) {
			toReturn = results.getString(1);
		}
		stat.close();
		results.close();
		return toReturn;
	}


	/**
	 * getFilm gets the name of a film given the film's ID
	 * @param id - ID of film
	 * @return - name of film
	 * @throws SQLException - SQL error
	 */
	public String getFilm(String id) throws SQLException {
		String query = "SELECT name FROM film WHERE id = ? LIMIT 1";
		PreparedStatement stat = connection.prepareStatement(query);
		stat.setString(1, id);
		ResultSet results = stat.executeQuery();

		String toReturn = "";
		if (results.next()) {
			toReturn = results.getString(1);
		}
		stat.close();
		results.close();
		return toReturn;
	}

	
	
	/**
	 * getFilmsIDs gets the IDs of all of the films which the actor was in
	 * @param actorID -actor for which to find films
	 * @return - list of filmIDs
	 * @throws SQLException - SQL error
	 */
	public List<String> getFilmsIDs(String actorID) throws SQLException {
		String query = "SELECT id FROM "
        + "film JOIN actor_film ON "
        + "film.id = actor_film.film "
        + "WHERE actor_film.actor = ?";
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setString(1, actorID);
        ResultSet results = stat.executeQuery();
        List<String> toReturn = new ArrayList<String>();
        while (results.next()) {
        	toReturn.add(results.getString(1));
        }
        return toReturn;
	}

	
	/**
	 * getActorFilmIDs returns the filmIDs of the movies the actor was is
	 * @param actorID - id of the actor in question
	 * @return list of filmids
	 * @throws SQLException
	 */
	  public List<String> getActorFilmsIDs(String actorID) throws
	  SQLException {
	    String query = "SELECT id FROM "
	        + "film JOIN actor_film ON "
	        + "film.id = actor_film.film "
	        + "WHERE actor_film.actor = ?";
	    PreparedStatement stat = connection.prepareStatement(query);
	    stat.setString(1, actorID);
	    ResultSet results = stat.executeQuery();
	    List<String> toReturn = new ArrayList<String>();
	    while (results.next()) {
	      toReturn.add(results.getString(1));
	    }
	    stat.close();
	    results.close();
	    return toReturn;
	  }
	  
	  
	  /**
	   * getFilmActorsCaveat gets actors of a film that complies with 
	   * the caveat stated in the project hand out. 
	   * @param firstChar
	   * @param filmID
	   * @return
	   * @throws SQLException
	   */
	  public List<String> getFilmActorsCaveat(String firstChar,
		      String filmID) throws SQLException {
		    String query = "SELECT id FROM actor JOIN actor_film ON "
		        + "actor.id = actor_film.actor "
		        + "WHERE name LIKE ? AND actor_film.film = ?";
		    PreparedStatement stat = connection.prepareStatement(query);
		    stat.setString(1, firstChar + "%");
		    stat.setString(2, filmID);
		    ResultSet results = stat.executeQuery();
		    List<String> toReturn = new ArrayList<String>();
		    while (results.next()) {
		      toReturn.add(results.getString(1));
		    }
		    return toReturn;
		  }

	  /**
	   * getFilmActorsIDs gets a list of ids of actors that were in a film
	   * @param filmID - film that actors were in
	   * @return - list of actor ids
	   * @throws SQLException - SQL error 
	   */
	public List<String> getFilmActorsIDs(String filmID) throws SQLException {
		String query = "SELECT id FROM actor JOIN actor_film ON "
        + "actor.id = actor_film.actor "
        + "WHERE actor_film.film = ?";
	    PreparedStatement stat = connection.prepareStatement(query);
	    stat.setString(1, filmID);
	    ResultSet results = stat.executeQuery();
	    List<String> toReturn = new ArrayList<String>();
	    while (results.next()) {
	      toReturn.add(results.getString(1));
	    }
	    return toReturn;
	}


	/**
	 * getFilmCount gets the number of actors that were in a film
	 * @param filmID - film that actors were in 
	 * @return - double 
	 * @throws SQLException - SQL error
	 */
	public double getFilmCount(String filmID) throws SQLException {
		String query = "SELECT COUNT(actor) AS cnt FROM actor_film "
        + "WHERE actor_film.film = ?";
	    PreparedStatement stat = connection.prepareStatement(query);
	    stat.setString(1, filmID);
	    ResultSet results = stat.executeQuery();
	    double toReturn = 0;

	    if (results.next()) {
	      toReturn = results.getDouble(1);
	    }
	    results.close();
	    return toReturn;
	}


	/**
	 * Closes the sql connection
	 * @throws SQLException - SQL error
	 */
	public void closeConn() throws SQLException {
    	connection.close();
  	}


}