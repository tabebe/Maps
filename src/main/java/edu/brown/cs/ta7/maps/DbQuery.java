package edu.brown.cs.ta7.maps;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DbQuery {


  private Connection conn;
  /**
   * This constructor takes in a path to the db file
   * @param db Path to the db file
   * @throws ClassNotFoundException
   * @throws SQLException incase
   */
  public DbQuery(String db) throws ClassNotFoundException, SQLException {
	  //TODO(1): Set up a connection
	    Class.forName("org.sqlite.JDBC");
	    String urlToDB = "jdbc:sqlite:" + db;

	    //TODO(2): Store the connection in a field
	    conn = DriverManager.getConnection(urlToDB);
  }





  public List<Node> queryTile(Double latTL, Double longTL, Double latBR, Double longBR) throws SQLException {

    String query = "SELECT n1.latitude AS lat1, n1.longitude AS long1, way.name AS name, n2.latitude AS lat2, n2.longitude AS long2 "
        + "FROM node n1 "
        + "INNER JOIN way ON n1.id=way.start AND n1.latitude<=? AND n1.latitude>=? AND n1.longitude<=? AND n1.longitude>=? "
        + "INNER JOIN node n2 ON n2.id=way.end AND n2.latitude<=? AND n2.latitude>=? AND n2.longitude<=? AND n2.longitude>=?";

    //TODO(2): Create a PreparedStatement
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setDouble(1, latTL);
    prep.setDouble(2, latBR);
    prep.setDouble(3, longTL);
    prep.setDouble(4, longBR);
    prep.setDouble(5, latTL);
    prep.setDouble(6, latBR);
    prep.setDouble(7, longTL);
    prep.setDouble(8, longBR);


    //TODO(3): Execute the query and retrieve a ResultStatement
    ResultSet rs = prep.executeQuery();

    //TODO(4): Add the results to this list
    List<Node> toReturn = new ArrayList<Node>();
    while (rs.next()) {
      Double lat1 = rs.getDouble("lat1");
      Double long1 = rs.getDouble("long1");
      Double lat2 = rs.getDouble("lat2");
      Double long2 = rs.getDouble("long2");
      String name = rs.getString("name");
      ArrayList<Double> coors1 = new ArrayList<Double>();
      ArrayList<Double> coors2 = new ArrayList<Double>();
      coors1.add(lat1);
      coors1.add(long1);
      coors2.add(lat2);
      coors2.add(long2);
      Node node1 = new Node("id");
      node1.setCoors(coors1);
      Node node2 = new Node("id");
      node2.setCoors(coors2);
      
      toReturn.add(node1);
      toReturn.add(node2);
      
        }
    //TODO(5): Close the ResultSet and the PreparedStatement
    rs.close();
    prep.close();
    return toReturn;
  }
  
  public List<String> getProperWays() throws SQLException {
	    String query = "SELECT id FROM way WHERE type != ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, "");
	    ResultSet results = stat.executeQuery();
	    List<String> toReturn = new ArrayList<String>();
	    while (results.next()) {
	      toReturn.add(results.getString(1));
	    }
	    stat.close();
	    results.close();
	    return toReturn;
	  }
  
 
  
  
  
  
  public String getWayPath(String firstNode, String secondNode) throws SQLException {
	  String query = "SELECT id FROM way WHERE (start = ? AND end = ?) "
	  		+ "OR (start = ? AND end = ?)";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, firstNode);
	    stat.setString(2, secondNode);
	    stat.setString(3, secondNode);
	    stat.setString(4, firstNode);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }
  
  
  
  
  /**
   * Given lat and long of an node, this function gets the 
   * id of the node
   * @param lat - lat of node
   * @param lon - long of node 
   * @return - ID of the node which cooresponds to lat and long
   * @throws SQLException
   */
  public String getID(String lat, String lon) throws SQLException {
	  String query = "SELECT id FROM node WHERE longitude = ? AND latitude = ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, lon);
	    stat.setString(2, lat);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }


  
  
  /**
   * Given an id of a way, this function returns it's start node
   * @param id - id of way
   * @return - id of start node
   * @throws SQLException
   */
  public String getStartN(String id) throws SQLException { 
	  String query = "SELECT start FROM way WHERE id = ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, id);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }
  
  
  /**
   * Given an id of a way, this function returns it's end node
   * @param id - id of way
   * @return - id of end node
   * @throws SQLException
   */
  public String getEndN(String id) throws SQLException { 
	  String query = "SELECT end FROM way WHERE id = ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, id);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }
  
  
  /**
   * Given an id of a node, it returns the long of that node
   * @param id - id of node 
   * @return - long of node (string)
   * @throws SQLException
   */
  public String getLongN(String id) throws SQLException { 
	  String query = "SELECT longitude FROM node WHERE id = ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, id);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }
  
  
  /**
   * Given if of node, it returns the lat of that node
   * @param id - id of node
   * @return - lat of node (string) 
   * @throws SQLException
   */
  public String getLatN(String id) throws SQLException {
	  String query = "SELECT latitude FROM node WHERE id = ?";
	    PreparedStatement stat = conn.prepareStatement(query);
	    stat.setString(1, id);
	    ResultSet results = stat.executeQuery();
	    //only add if results isn't empty
	    String toReturn = "";
	    if (results.next()) {
	      toReturn = results.getString(1);
	    }
	    stat.close();
	    results.close();
	    return toReturn;
  }


  /**
   * query that selects all distinct nodes
   * from the database.
   * @return List of all node ids.
   * @throws SQLException SQL error.
   */
  public List<String> getAllNodes() throws SQLException {
    String query = "SELECT id FROM node";
    PreparedStatement stat = conn.prepareStatement(query);
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
   * query that selects all distinct nodes
   * from the database.
   * @return List of all node ids.
   * @throws SQLException SQL error.
   */
  public List<String> getAllWays() throws SQLException {
    String query = "SELECT id FROM way";
    PreparedStatement stat = conn.prepareStatement(query);
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
   * query that given an way's id, returns the way's name.
   * @param id The id of a way.
   * @return The nodes's name.
   * @throws SQLException SQL error.
   */
  public String getWay(String id) throws SQLException { 
    String query = "SELECT name FROM way WHERE id = ? LIMIT 1";
    PreparedStatement stat = conn.prepareStatement(query);
    stat.setString(1, id);
    ResultSet results = stat.executeQuery();
    //only add if results isn't empty
    String toReturn = "";
    if (results.next()) {
      toReturn = results.getString(1);
    }
    stat.close();
    results.close();
    return toReturn;
  }
  
  


/**
   * query that given an node's ID, returns the wayIDs the node
   * connects to.
   * @param nodeID node's ID
   * @return A List of wayIDs that the node connects to.
   * @throws SQLException SQL error
   */
  public List<String> getNodeWaysIDs(String nodeID) throws
  SQLException {
    String query = "SELECT id FROM "
        + "way WHERE start = ? OR "
        + "end = ?";
    PreparedStatement stat = conn.prepareStatement(query);
    stat.setString(1, nodeID);
    stat.setString(2, nodeID);
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
   * Takes in a node id and creates a Node data structure
   * 
   * @param nodeID - node id
   * @return
   * @throws NumberFormatException
   * @throws SQLException
   */
  public Node makeNode(String nodeID) throws NumberFormatException, SQLException {
	  ArrayList<Double> coors = new ArrayList<Double>();
	  double lat = Double.parseDouble(this.getLatN(nodeID));
	  double lon = Double.parseDouble(this.getLongN(nodeID));
	  coors.add(lat);
	  coors.add(lon);
	  Node node = new Node(nodeID);
	  node.setCoors(coors);
	  return node;
  }
  

  /**
   * takes in two nodes and calculates the distance between them
   * @param start
   * @param end
   * @return
   */
  public Double calcWeight(Node start, Node end) {
	  ArrayList<Double> first = start.getCoors();
	  ArrayList<Double> second = end.getCoors();
	  double x1 = first.get(0);
	  double y1 = first.get(1);
	  double x2 = second.get(0);
	  double y2 = second.get(1);
	  
	  double one = x2 - x1;
	  double two = y2 - y1;
	  
	  double sqr = (one * one);
	  double sqr2 = (two * two);
	  return Math.sqrt(sqr + sqr2);
  }
  
  
  public void closeConn() throws SQLException {
	    conn.close();
	  }






}


