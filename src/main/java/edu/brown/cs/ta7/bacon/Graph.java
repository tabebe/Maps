package edu.brown.cs.ta7.bacon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * Graph is the class that creates the vertices and edges 
 * and performs dijkstras algorithm to find the shortest 
 * path from one actor to another. 
 * @author ta7
 *
 */


public class Graph {
	private String name1;
	private String name2;
	private String name2ID;
	private Actor name2Vertex;
	private PriorityQueue<Actor> pq;
	private Map<Actor, Actor> map; 
	private SQL database;




	/**
	 * Graph constructor that takes in the first actor's name
	 * the second actor's name and a path to the database. This 
	 * object is used to perform dijkstras algorithm to find the 
	 * shortest path. 
	 * 
	 * @param nameOne - giver of the bacon
	 * @param nameTwo	- receiver of the bacon
	 * @param - db path to database
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	public Graph(String nameOne, String nameTwo, String db) throws 
	SQLException, IllegalArgumentException, ClassNotFoundException {
		database = new SQL(db);

		if (nameOne.equals(nameTwo) || nameOne.isEmpty() || nameTwo.isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (database.getActorID(nameOne).isEmpty() 
			|| database.getActorID(nameTwo).isEmpty()) {
			throw new SQLException();
		}

		name1 = nameOne;
		name2 = nameTwo;
		name2ID = database.getActorID(nameTwo);
		name2Vertex = includeVertex(name2ID);

		map = new HashMap<Actor, Actor>();

	}




	/**
	 * Dijkstra algorithm used to find the shortest path
	 * from one actor to another. 
	 * @return List of actors
	 * @throws SQLException
	 */
	public List<Actor> dijkstra() throws SQLException {
		Actor first = includeVertex(database.getActorID(name1));
    
    first.setDistance(0);
    
    pq = new PriorityQueue<Actor>(Vertex.compareVertex());
    pq.add(first);
    map.put(first, first);
    
    List<Actor> shortestPath = new ArrayList<Actor>();
   
    while (!pq.isEmpty()) {
      Actor curr = pq.poll();
      
      if (curr.getObject().equals(name2ID)) {
    	  name2Vertex = curr;
        break;
      }
      
      if (curr.getDistance() == Double.POSITIVE_INFINITY) {
        break;
      }
      
      List<String> filmIDs = database.getActorFilmsIDs(curr.getObject());
      
      for (String fid : filmIDs) {
        String filmName = database.getFilm(fid);
       
        double amt = database.getFilmCount(fid);
        double weight = (1 / amt);
        
        String[] splitName = curr.getName().split(" ");
        String firstChar = splitName[splitName.length - 1].substring(0, 1);
        List<String> baconActors = database.getFilmActorsCaveat(firstChar, fid);
       
        for (String a : baconActors) {
          Actor newVertex = includeVertex(a);
         
          if (map.containsKey(newVertex)) {
            Actor oldVertex = map.get(newVertex);
            if (weight + curr.getDistance() < oldVertex.getDistance()) {
              Movie edge = includeEdge(curr,
                  oldVertex, weight, filmName, fid);
              curr.addEdge(edge);
              oldVertex.setDistance(weight + curr.getDistance());
              oldVertex.setPrev(curr);
              oldVertex.setMovie(filmName);
              oldVertex.setMovieID(fid);
              map.put(oldVertex, oldVertex);
              pq.add(oldVertex);
            }
            
          } else {
            Movie edge = includeEdge(curr, newVertex, weight, filmName, fid);
            curr.addEdge(edge);
            newVertex.setDistance(weight + curr.getDistance());
            newVertex.setPrev(curr);
            newVertex.setMovie(filmName);
            newVertex.setMovieID(fid);
            map.put(newVertex, newVertex);
            pq.add(newVertex);
          }
        }
      }
    }
   
    if (name2Vertex.getDistance() == Double.POSITIVE_INFINITY) {
      return shortestPath;
    }
    Actor current = name2Vertex;
    
    while (current.getPrev() != null) {
      shortestPath.add(current);
      current = (Actor) current.getPrev();
    }
    
    shortestPath.add(current);
    Collections.reverse(shortestPath);
    return shortestPath;
	}


	/**
	 * constructs a vertex that has the correct properties
	 * @param sg - id of the actor
	 * @return - an actor
	 * @throws SQLException
	 */
	public Actor includeVertex(String sg) throws SQLException {
		String actor = database.getActor(sg);
		Actor vertex = new Actor(sg, actor);
		return vertex;
	}

	/**
	 * Constructs an edge that has the correct properties
	 * 
	 * @param a1 - actor one
	 * @param a2 - actor two
	 * @param weight - weight of edge
	 * @param movieName - name of movie
	 * @param movieID - id of movie
	 * @return
	 */
	public Movie includeEdge(Actor a1, Actor a2, double weight, String movieName, String movieID) {
		Movie movieEdge = new Movie(a1, a2, weight, movieName, movieID);
		return movieEdge;
	}

	/**
	 * Properly connects actors with film names
	 * @param path - path of actors
	 * @return path in a string
	 */
	public List<String> connectString(List<Actor> path) {
		List<String> connections = new ArrayList<String>();
    if (path.isEmpty()) {
      connections.add(name1 + " -/- " + name2);
      return connections;
    }
    
    for (int i = 0; i < path.size() - 1; i++) {
      Actor a1 = path.get(i);
      Actor a2 = path.get(i + 1);
      String name1 = a1.getName();
      String name2 = a2.getName();
      String film = a2.getMovie();
      connections.add(name1 + " -> " + name2 + " : " + film);
    }
    return connections;
	}


	/**
	 * Closes SQL parser connection 
	 * @throws SQLException SQL Error
	 */
	public void closeConnection() throws SQLException {
		database.closeConn();
	}


} 