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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
  private File file;
  private static DbQuery sparkDb;
 // private static Graph sparkGraph;
 // private static Dijkstra dijkstra;
  private String x1;
  private String y1;
  private String x2;
  private String y2;
  private String db;
  private List<String> strings;
  private Integer portNum;
  private String[] args;
  private static final Charset UTF8 = Charsets.UTF_8;
  private DbQuery database;
  private final static Gson GSON = new Gson();

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
	  
	  x1 = args[0];
	  y1 = args[1];
	  x2 = args[2];
	  y2 = args[3];
	  db = args[4];
	 database = new DbQuery(db);
	 
	 double x11 = Double.parseDouble(x1);
	 double y11 = Double.parseDouble(y1);
	 double x22 = Double.parseDouble(x2);
	 double y22 = Double.parseDouble(y2);
	 
	 
	 
	 
	  List<String> nodeIDs;
	  List<String> wayIDs;
	  List<Node> nodes = new ArrayList<Node>();
	  List<Way> ways = new ArrayList<Way>();
	  
	  nodeIDs = database.getAllNodes();
	  for (int i = 0; i < nodeIDs.size(); i++) { 
		  nodes.add(makeNode(nodeIDs.get(i)));
	  }
	  
	  
	  wayIDs = database.getAllWays();
	  for (int i = 0; i < wayIDs.size(); i++) {
		  String id = wayIDs.get(i);
		  String start = database.getStartN(id);
		  String end = database.getEndN(id);
		  
		  
		  
		  Node startN = makeNode(start);
		  Node endN = makeNode(end);
		  String name = database.getWay(id);
		  double weight = calcWeight(startN, endN);
		  
		  
		  Way way = new Way(startN, endN, name, id, weight);
		  ways.add(way);
	  }
	  
	  
	  // Find nearest neighbor
	  HashMap<String, ArrayList<Double>> starNames;
	  ArrayList<KDNode<Node>> knodes = new ArrayList<KDNode<Node>>();
	  KDTree<Node> kdTree;
	  
	  
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
  
  
  public void scanInputs(KDTree tree, List<Node> nodes, List<Way> ways) throws IOException {
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
  
  
  public void parseCommands(String[] inputs, KDTree tree, List<Node> nodes, List<Way> ways) {
	  StringBuilder othersb = new StringBuilder();
	  
	  if (inputs.length != 4) {
		  System.out.println("ERROR: wrong number of arguments");
		  System.exit(1);
	  }
	  
	  try {
		  double x1 = Double.parseDouble(inputs[0]);
		  double y1 = Double.parseDouble(inputs[1]);
		  double x2 = Double.parseDouble(inputs[2]);
		  double y2 = Double.parseDouble(inputs[3]);
		  
		  
		  
		  ArrayList<Double> startCoors = new ArrayList<Double>();
		  ArrayList<Double> endCoors = new ArrayList<Double>();
		  
		  startCoors.add(x1);
		  startCoors.add(y1);
		  endCoors.add(x2);
		  endCoors.add(y2);
		  
		  /**
		   * Find the nearest nodes for the start and end coordinates
		   */
		  ArrayList<KDNode<Node>> nearestStart = tree.neighborSearch(1, startCoors);
		  ArrayList<KDNode<Node>> nearestEnd = tree.neighborSearch(1, endCoors);
		  
		  Node startNode = nearestStart.get(0).getObject();
		  Node endNode = nearestEnd.get(0).getObject();
		  
		  
		  
		  if (startNode.getID() == endNode.getID()) {
			  System.out.println("Start and End nodes are the same.");
			  
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
		  
		  System.exit(1);
	  } catch (SQLException e) {
		  
		  System.exit(1);
	  } catch (ClassNotFoundException e) {
		  
		  System.exit(1);
	  }
	  
	  
  }
  
  
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
  
  
  
  
  /**
   * Takes in a node id and creates a Node data structure
   * 
   * @param nodeID - node id
   * @return
   * @throws NumberFormatException
   * @throws SQLException
   */
  private Node makeNode(String nodeID) throws NumberFormatException, SQLException {
	  ArrayList<Double> coors = new ArrayList<Double>();
	  double lat = Double.parseDouble(database.getLatN(nodeID));
	  double lon = Double.parseDouble(database.getLongN(nodeID));
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
  private Double calcWeight(Node start, Node end) {
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
  
  
  
}
