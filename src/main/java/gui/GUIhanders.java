package gui;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.brown.cs.ta7.maps.DbQuery;
import edu.brown.cs.ta7.maps.Dijkstra;
import edu.brown.cs.ta7.maps.Graph;
import edu.brown.cs.ta7.maps.Node;
import edu.brown.cs.ta7.maps.Way;
import edu.brown.cs.ta7.nearest.KDNode;
import edu.brown.cs.ta7.nearest.KDTree;

public class GUIhanders {
	

	public static Gson GSON = new GsonBuilder()
	.registerTypeAdapter(Node.class, new nodeSerializer()).create();
	
	
	
	public static class TileInfo implements Route {
		private DbQuery database;
		
		public TileInfo(DbQuery db) {
			this.database = db;
		}

		@Override
		public Object handle(Request req, Response res) {
			QueryParamsMap qm = req.queryMap();
			
			double lat1 = Double.parseDouble(qm.value("lat1"));
			double long1 = Double.parseDouble(qm.value("long1"));
			double lat2 = Double.parseDouble(qm.value("lat2"));
			double long2 = Double.parseDouble(qm.value("long2"));
			
			try {
				List<Node> nodes = database.queryTile(lat1, long1, lat2, long2);
				return GSON.toJson(nodes);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		
		
		
		
		
	}
	
	
	
	
	
	public static class ShortestPath  implements Route {
		private DbQuery database;
		
		public ShortestPath(DbQuery db) {
			this.database = db;
		}
		
		@Override
		public Object handle(Request req, Response res){
			
			QueryParamsMap qm = req.queryMap();
			double lat1 = Double.parseDouble(qm.value("lat1"));
			double long1 = Double.parseDouble(qm.value("long1"));
			double lat2 = Double.parseDouble(qm.value("lat2"));
			double long2 = Double.parseDouble(qm.value("long2"));
			
			
			
			try {
			
			Map<Node, Node> nodeMap = new HashMap<Node, Node>();
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
			  //HashMap<String, ArrayList<Double>> starNames;
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
			  KDTree<Node> tree = new KDTree<Node>(2, knodes);
			
			
			 ArrayList<Double> startCoors = new ArrayList<Double>();
			 ArrayList<Double> endCoors = new ArrayList<Double>();
			 startCoors.add(lat1);
			 startCoors.add(long1);
			 endCoors.add(lat2);
			 endCoors.add(long2);
			 
			 ArrayList<KDNode<Node>> nearestStart = tree.neighborSearch(1, startCoors);
			  ArrayList<KDNode<Node>> nearestEnd = tree.neighborSearch(1, endCoors);
			  
			  Node startNode = nearestStart.get(0).getObject();
			  Node endNode = nearestEnd.get(0).getObject();
			  
			  
			  
			  
			  Graph graph = new Graph(nodes, ways);
			  Dijkstra dij = new Dijkstra(graph);
			  dij.execute(startNode);
			  LinkedList<Node> path = dij.getPath(endNode);
			  List<Node> toReturn = ImmutableList.copyOf(path);
			  
			  return GSON.toJson(toReturn);
			
			} catch (SQLException e) {
				
			} catch (ClassNotFoundException e) {
				
			}
			
			
			
			
			return null;
		}
		
		
		
	}
	
	
	
	
	
}
