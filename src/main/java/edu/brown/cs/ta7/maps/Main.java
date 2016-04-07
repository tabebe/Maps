package edu.brown.cs.ta7.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.hamcrest.Matcher;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.ta7.nearest.KDNode;
import edu.brown.cs.ta7.nearest.KDTree;
import freemarker.template.Configuration;

/**
 * @author rwdodd
 *
 */
public class Main {

  private String db;
  private List<String> strings;
  private Integer portNum;
  private String[] args;
  private DbQuery database;
  private final static Gson GSON = new Gson();
  private KDTree<Node> kdTree;

  /**
   * @param args the command line args
 * @throws IOException 
   */
  public static void main(String[] args) 
  throws ClassNotFoundException, SQLException, IOException {
	  	new Main(args).run();

  }


  /**
   * @param args the command line args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  
  
  
  private void run() throws ClassNotFoundException, SQLException, IOException {
	 
	 // make sure the number of run arguments are correct
	 // (The number of legal arguments will change when we add --GUI"
	 if (args.length != 1) {
		 System.out.println("Error: wrong number of arguments");
		 System.exit(1);
	 }
	  
	 // initialize database
	 db = args[0];
	 database = new DbQuery(db);
	
	 
	 
	  // Data structures to get and store Nodes and Ways
	  Map<Node, Node> nodeMap = new HashMap();
	  List<Node> nodes = new ArrayList<Node>();
	  List<String> wayIDs;
	  List<Way> ways = new ArrayList<Way>();
	  
	  // query ways that include nodes that have types 
	  wayIDs = database.getProperWays();
	  
	  
	  
	  // get the nodes that are connected to the proper ways, 
	  // and store them in a hash map
	  for (int i = 0; i < wayIDs.size(); i++) {
		  String start = database.getStartN(wayIDs.get(i));
		  String end = database.getEndN(wayIDs.get(i));
		  nodeMap.put(database.makeNode(start), database.makeNode(start));
		  nodeMap.put(database.makeNode(end), database.makeNode(end));
	  }
	  
	  // Go the nodes from the hash map and put them in a list
	  Set<Node> newNodes = nodeMap.keySet();
	  Iterator<Node> nodeIter = newNodes.iterator();
	  while (nodeIter.hasNext()) {
		  nodes.add(nodeIter.next());
	  }
	  

	  
	  //Creates array of Ways. it also calculates the weighs of nodes
	  for (int i = 0; i < wayIDs.size(); i++) {
		  String id = wayIDs.get(i);
		  String start = database.getStartN(id);
		  String end = database.getEndN(id);
		  
		  Node startN = database.makeNode(start);
		  Node endN = database.makeNode(end);
		  String name = database.getWay(id);
		  double weight = database.calcWeight(startN, endN);
		  
		  Way way = new Way(startN, endN, name, id, weight);
		  ways.add(way);
	  }
	  
	  
	  // Find nearest neighbor
	  HashMap<String, ArrayList<Double>> starNames;
	  ArrayList<KDNode<Node>> knodes = new ArrayList<KDNode<Node>>();
	 
	  
	  
	  /**
	   * turn Nodes into KDNodes to use for nearest n. search
	   */
	  for (int i = 0; i < nodes.size(); i++) {
		  Node node = nodes.get(i);
		  KDNode<Node> kdnode = new KDNode<Node>(node, node.getCoors());
		  knodes.add(kdnode);
	  }
	  
	  /**
	   * Create KDTree
	   * Get coordinates 
	   */
	  kdTree = new KDTree(2, knodes);
	  scanInputs(kdTree, nodes, ways);
	  database.closeConn();
	  

	
  }
  
  
  public void scanInputs(KDTree tree, List<Node> nodes, List<Way> ways) throws IOException, SQLException {
	  BufferedReader newBR = new BufferedReader(new InputStreamReader(System.in));
	  String s;
	  
	  while ((s = newBR.readLine()) != null) {
		  if (s.isEmpty()) {
			  break;
		  }
		  
		  String[] inputs = s.split(" ");
		  parseCommands(inputs, tree, nodes, ways);
		  
		  
	  }
	  newBR.close();
  }
  
  
  public void parseCommands(String[] inputs, KDTree tree, List<Node> nodes, List<Way> ways) throws SQLException, IOException {
	  StringBuilder othersb = new StringBuilder();
	  
	  if (inputs.length != 4) {
		  
		  if (inputs[0] == "exit") {
			  System.exit(1);
		  }
		  System.out.println("ERROR: wrong number of arguments");
		  scanInputs(kdTree, nodes, ways);
	  }
	  
	  
	  ArrayList<Double> startCoors = new ArrayList<Double>();
	  ArrayList<Double> endCoors = new ArrayList<Double>();
	  
	  
	  try {
		  double x1 = Double.parseDouble(inputs[0]);
		  double y1 = Double.parseDouble(inputs[1]);
		  double x2 = Double.parseDouble(inputs[2]);
		  double y2 = Double.parseDouble(inputs[3]);
		  startCoors.add(x1);
		  startCoors.add(y1);
		  endCoors.add(x2);
		  endCoors.add(y2);  
	  } catch (IllegalArgumentException e) {
		  String way1 = inputs[0];
		  String crossWay1 = inputs[1];
		  String way2 = inputs[2];
		  String crossWay2 = inputs[3];
		  
		  
		  String way1Start = database.getStartN(way1);
		  String way1End = database.getEndN(way1);

		  String crossWay1Start = database.getStartN(crossWay1);
		  String crossWay1End = database.getEndN(crossWay1);

		  String way2Start = database.getStartN(way2);
		  String way2End = database.getEndN(way2);

		  String crossWay2Start = database.getStartN(crossWay2);
		  String crossWay2End = database.getEndN(crossWay2);

		  
		  boolean pass = false;
		  boolean extrapass = false;
		  
		  
		  if (way1Start.equals(crossWay1Start) || way1Start.equals(crossWay1End)) {
			  startCoors = database.makeNode(way1Start).getCoors();
			  pass = true;
		  } else if (way1End.equals(crossWay1Start) || way1End.equals(crossWay1End)) {
			  startCoors = database.makeNode(way1End).getCoors();
			  pass = true;
		  } 
		  
		  if (way2Start.equals(crossWay2Start) || way2Start.equals(crossWay2End)) {
			  endCoors = database.makeNode(way2Start).getCoors();
			  extrapass = true;
		  } else if (way2End.equals(crossWay2Start) || way2End.equals(crossWay2End)) {
			  endCoors = database.makeNode(way2End).getCoors();
			  extrapass = true;
		  } 
		  
		  if (pass != true || extrapass != true) {
			  System.out.println("Intersection not found. Try again with "
			  		+ "streets that intersect");
			  scanInputs(kdTree, nodes, ways);
		  }
		  
		  
		  
		  
		  
	  }
	
	try {
		  /**
		   * Find the nearest nodes for the start and end coordinates
		   */
		  ArrayList<KDNode<Node>> nearestStart = tree.neighborSearch(1, startCoors);
		  ArrayList<KDNode<Node>> nearestEnd = tree.neighborSearch(1, endCoors);
		  
		  Node startNode = nearestStart.get(0).getObject();
		  Node endNode = nearestEnd.get(0).getObject();
		  
		  
		  
		  if (startNode.getID() == endNode.getID()) {
			  System.out.println("Start and End nodes are the same.");
			  scanInputs(kdTree, nodes, ways);
		  } else {
		  
		  Graph graph = new Graph(nodes, ways);
		  Dijkstra dij = new Dijkstra(graph);
		  dij.execute(startNode);
		  LinkedList<Node> path = dij.getPath(endNode);
		  List<String> wayPath = listOfWays(path);
		  

		  List<String> conns = dij.getConnString(path);
		  int i = 0;
		  for (String a : conns) {
			  System.out.println(a + wayPath.get(i));
			  i++;
		  }
		 
		 }
		  
  
	  } catch (IllegalArgumentException e) {
		  System.out.println("Error: Illegal Arguements");
		  System.exit(1);
	  } catch (SQLException e) {
		  System.out.println("Error: SQL Exception");
		  System.exit(1);
	  } catch (ClassNotFoundException e) {
		  System.out.println("Error: Class Not Found Exception");
		  System.exit(1);
	  }
	  
	  
  }
  
  /**
   * Given a path, this function finds the ways that the path takes
   * @param path - list of path
   * @return - list of way IDs
   * @throws SQLException
   */
  private List<String> listOfWays(LinkedList<Node> path) throws SQLException {
	   List<String> wayList = new ArrayList<String>();
	  for (int i = 0; i < path.size() - 1; i++) {
		  String firstID = path.get(i).getID();
		  String secondID = path.get(i + 1).getID();
		  String wayID = database.getWayPath(firstID, secondID);
		  wayList.add(wayID);
	  }
	  return wayList;
	  
  }
  
  
//  
//  
//  /**
//   * Takes in a node id and creates a Node data structure
//   * 
//   * @param nodeID - node id
//   * @return
//   * @throws NumberFormatException
//   * @throws SQLException
//   */
//  private Node makeNode(String nodeID) throws NumberFormatException, SQLException {
//	  ArrayList<Double> coors = new ArrayList<Double>();
//	  double lat = Double.parseDouble(database.getLatN(nodeID));
//	  double lon = Double.parseDouble(database.getLongN(nodeID));
//	  coors.add(lat);
//	  coors.add(lon);
//	  Node node = new Node(nodeID);
//	  node.setCoors(coors);
//	  return node;
//  }
//  
//  
//  /**
//   * takes in two nodes and calculates the distance between them
//   * @param start
//   * @param end
//   * @return
//   */
//  private Double calcWeight(Node start, Node end) {
//	  ArrayList<Double> first = start.getCoors();
//	  ArrayList<Double> second = end.getCoors();
//	  double x1 = first.get(0);
//	  double y1 = first.get(1);
//	  double x2 = second.get(0);
//	  double y2 = second.get(1);
//	  
//	  double one = x2 - x1;
//	  double two = y2 - y1;
//	  
//	  double sqr = (one * one);
//	  double sqr2 = (two * two);
//	  return Math.sqrt(sqr + sqr2);
//  }
  
  
  
}
