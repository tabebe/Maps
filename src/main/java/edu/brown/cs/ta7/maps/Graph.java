package edu.brown.cs.ta7.maps;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import edu.brown.cs.ta7.findPath.Vertex;







public class Graph {
	private String id1;
	private String id2;
	private Node id2Node;
	private PriorityQueue<Node> pq;
	private Map<Node, Node> map;
	private DbQuery database;
	
	
	
	
	
	
	public Graph(String idOne, String idTwo, String db) throws 
	SQLException, IllegalArgumentException, ClassNotFoundException {
		database = new DbQuery(db);
		
		
		if (idOne.equalsIgnoreCase(idTwo) || idOne.isEmpty() || idTwo.isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		id1 = idOne;
		id2 = idTwo;
		id2Node = includeVertex(id2);
		
		map = new HashMap<Node, Node>();
		
		
	}
	
	
	
	
	public List<Node> dijkstra() throws SQLException {
		Node first = includeVertex(id1);
		first.setDistance(0);
		
		pq = new PriorityQueue<Node>(Vertex.compareVertex());
		pq.add(first);
		map.put(first, first);
		
		
		List<Node> shortestPath = new ArrayList<Node>();
		
		while (!pq.isEmpty()) {
			Node curr = pq.poll();
			
			if(curr.getObject().equals(id2)) {
				id2Node = curr;
			break;
			}
			
			if (curr.getDistance() == Double.POSITIVE_INFINITY) {
				break;
			}
			
			List<String> wayIDs = database.getNodeWaysIDs(curr.getObject());
			
			
			for (String w : wayIDs) {
				String wayName = database.getWay(w);
				List<String> nodes = database.getAllNodes();
				
				
				for (String n : nodes) {
					Node newNode = includeVertex(n);
					
					if (map.containsKey(newNode)) {
						Node oldNode = map.get(newNode);
						if (curr.getDistance() < oldNode.getDistance()) {
							Way edge = includeEdge(curr, oldNode, wayName, n);
							curr.addEdge(edge);
							oldNode.setDistance(curr.getDistance());
							oldNode.setPrev(curr);
							oldNode.setWay(wayName);
							oldNode.setWayID(n);
							map.put(oldNode, oldNode);
							pq.add(oldNode);
						}
					} else {
						Way edge = includeEdge(curr, newNode, wayName, n);
						curr.addEdge(edge);
						newNode.setDistance(curr.getDistance());
						newNode.setPrev(curr);
						newNode.setWay(wayName);
						newNode.setWayID(n);
						map.put(newNode, newNode);
						pq.add(newNode);
							
				
					}
				}
				
			}
					
		}
		
		if (id2Node.getDistance() == Double.POSITIVE_INFINITY) {
			return shortestPath;
		}
		
		Node current = id2Node;
		
		while (current.getPrev() != null) {
			shortestPath.add(current);
			current = (Node) current.getPrev();
		}
		
		shortestPath.add(current);
		Collections.reverse(shortestPath);
		return shortestPath;
		
	}
	


	
	
	public Node includeVertex(String sg) throws SQLException {
		Node node = new Node(sg);
		return node;
	}
	
	
	
	public Way includeEdge(Node start, Node end, String name, String id) {
		Way way = new Way(start, end, name, id);
		return way;
	}
	
	
	public List<String> connectString(List<Node> path) {
		List<String> connections = new ArrayList<String>();
		
		if (path.isEmpty()) {
			connections.add(id1 + " -/- " + id2);
			return connections;
		}
		
		
		for (int i = 0; i < path.size() - 1; i++) {
			Node a1 = path.get(i);
			Node a2 = path.get(i + 1);
			String way = a2.getWay();
			connections.add(a1 + " => " + a2 + " : " + way);
		}
		return connections;
	}


	
	
	
	
	public void closeConnection() throws SQLException {
		database.closeConn();
	}

	
}




























































































