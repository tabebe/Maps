package edu.brown.cs.ta7.maps;



import java.util.List;


public class Graph {
	private final List<Node> nodes;
	private final List<Way> ways;


	public Graph(List<Node> nodes, List<Way> ways) {
		this.nodes = nodes;
		this.ways = ways;
	}
	
	
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	
	public List<Way> getWays() {
		return ways;
	}





}



