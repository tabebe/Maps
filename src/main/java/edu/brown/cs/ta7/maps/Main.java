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
   */
  public static void main(String[] args) 
  throws ClassNotFoundException, SQLException {
	  	new Main(args).run();

  }


  /**
   * @param args the command line args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  
  
  
  private void run() throws ClassNotFoundException, SQLException {
	  OptionParser parser = new OptionParser();
	  
//	  OptionSet options = parser.parse(args);
//	  @SuppressWarnings("unchecked")
//	  List<String> listNonOpts = (List<String>) options.nonOptionArguments();
	  
//	  x1 = listNonOpts.get(0);
//	  y1 = listNonOpts.get(1);
//	  x2 = listNonOpts.get(2);
//	  y2 = listNonOpts.get(3);
//	  db = listNonOpts.get(4);
	  
	  x1 = args[0];
	  y1 = args[1];
	  x2 = args[2];
	  y2 = args[3];
	  db = args[4];
	 database = new DbQuery(db);
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
	  
	  
	  
	  for (int i = 0; i < nodes.size(); i++) {
		  Node node = nodes.get(i);
		  KDNode<Node> kdnode = new KDNode<Node>(node, node.getCoors());
		  knodes.add(kdnode);
	  }
	  
	  kdTree = new KDTree(2, knodes);
	  ArrayList<Double> coors = new ArrayList<Double>();
	  coors.add(41.8204);
	  coors.add(-71.4001);
	  ArrayList<KDNode<Node>> answers = kdTree.neighborSearch(1, coors);
	  
	  System.out.println(answers.get(0).getObject());
	  
	  
	  
	  Graph graph = new Graph(nodes, ways);
	  Dijkstra dij = new Dijkstra(graph);
	  dij.execute(nodes.get(0));
	  LinkedList<Node> path = dij.getPath(nodes.get(1));
	  
	  
	
  }
  
  
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
