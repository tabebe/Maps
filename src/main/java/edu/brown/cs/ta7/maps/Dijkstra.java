package edu.brown.cs.ta7.maps;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Dijkstra {

	
	private final List<Node> nodes;
	private final List<Way> ways;
	private Set<Node> settledNodes;
	private Set<Node> unSettledNodes;
	private Map<Node, Node> pred;
	private Map<Node, Double> distance;
	private String startID;
	private String endID;
	
	
	
	
	public Dijkstra(Graph graph) throws ClassNotFoundException, SQLException {
		this.nodes = new ArrayList<Node>(graph.getNodes());
		this.ways = new ArrayList<Way>(graph.getWays());
		
	}
	
	
	public void execute(Node start) {
		this.startID = start.getID();
		
		settledNodes = new HashSet<Node>();
		unSettledNodes = new HashSet<Node>();
		distance = new HashMap<Node, Double>();
		pred = new HashMap<Node, Node>();
		distance.put(start, 0.0);
		unSettledNodes.add(start);
		
		while (unSettledNodes.size() > 0) {
			Node node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistance(node);
		}
		
	}
	
	
	private void findMinimalDistance(Node node) {
		List<Node> adjacentNodes = getNeighbors(node);
		for (Node target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) 
					+ getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) 
						+ getDistance(node, target));
				pred.put(target, node);
				unSettledNodes.add(target);
			}	
		}
	}
	
	
	private double getDistance(Node node, Node target) {
		for (Way way : ways) {
			if (way.getStart().equals(node) && way.getEnd().equals(target)) {
				return way.getWeight();
			}
		}
		throw new RuntimeException("Should not happen");
	}
	
	
	private List<Node> getNeighbors(Node node) {
		List<Node> neighbors = new ArrayList<Node>();
		for (Way way : ways) {
			if (way.getStart().equals(node) 
				&& !isSettled(way.getEnd())) {
					neighbors.add(way.getEnd());
				}
		}
		return neighbors;
	}
	
	private boolean isSettled(Node node) {
		return settledNodes.contains(node);
	}
	
	
	private Node getMinimum(Set<Node> nodes) {
		Node min = null;
		for (Node n : nodes) {
			if (min == null) {
				min = n;
			} else {
				if (getShortestDistance(n) < getShortestDistance(min)) {
					min = n;
				}
			}
		}	
		return min;
	}
	
	
	private double getShortestDistance(Node end) {
		Double d = distance.get(end);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}
	
	
	public LinkedList<Node> getPath(Node target) {
		this.endID = target.getID();
		
		LinkedList<Node> path = new LinkedList<Node>();
		Node step = target;
		
		if (pred.get(step) == null) {
			return null;
		}
		path.add(step);
		while (pred.get(step) != null) {
			step = pred.get(step);
			path.add(step);
		}
		
		
		Collections.reverse(path);
		return path;
	}
	
	
	
	public List<String> getConnString(LinkedList<Node> path) {
		List<String> connections = new ArrayList<String>();
		if (path.isEmpty()) {
			connections.add(startID + "-/-" + endID);
			return connections;
		}
		
		for (int i = 0; i < path.size() - 1; i++) {
			Node a1 = path.get(i);
			Node a2 = path.get(i + 1);
			String name1 = a1.getID();
			String name2 = a2.getID();
			String way = a2.getWay();
			connections.add(name1 + " => " + name2 + " : " + way);
		}
		return connections;
		
	}
	
	
	
	
	
	
}
