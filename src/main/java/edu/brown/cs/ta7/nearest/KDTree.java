package edu.brown.cs.ta7.KDTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.lang.Object;

import com.google.common.collect.MinMaxPriorityQueue;

import edu.brown.cs.ta7.stars.Stars;


public class KDTree<T extends NodeData> {
	public Node<T> root = null;
	public static int dim;
	public static int i = 1;
	
	public KDTree(final List<Node<T>> nodes, final int dim) {
		Node<T> parent = new Node(null, null, null, new Stars(1, "asdf", 1));
		parent.dep = 0;
		
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).parent = parent;
		}
		this.dim = dim;
		this.root = buildTree(nodes, parent, true); 
	}
	
	public Node<T> buildTree(final List<Node<T>> nodes, Node<T> parent, boolean bool) {
		
		if (nodes.isEmpty()) {
			return null;
		}
		
		Collections.sort(nodes, comp);
		final int index = (int) Math.floor(nodes.size()/2);
		final Node<T> root = nodes.get(index);
		root.parent = parent;
		if (bool) {
			root.dep = 0;
		} else {
			root.dep = parent.dep + 1;
		}
		
		
		
		
		root.left = buildTree(nodes.subList(0, index), root, false);
		root.right = buildTree(nodes.subList(index + 1, nodes.size()), root, false);
		
		
		return root;
		                               
	}
	
	
	public final Comparator<Node<T>> comp = new Comparator<Node<T>>() {
		public int compare(Node<T> a, Node<T> b) {
			int depp = a.parent.dep;
			return Double.compare(a.getAxis(depp % dim), b.getAxis(depp % dim));
			
		}
	};
	
	
	
	public final Comparator<Node<T>> knncomp = new Comparator<Node<T>>() {
		public int compare(Node<T> a, Node<T> b) {
			return Double.compare(distance(a, target), distance(b, target));
			
		}
	};
	public class pair {
		private final Node<T> node;
		private final double dis;
		
		public pair(Node<T> node, double dis) {
			this.node = node;
			this.dis = dis;
		}
		
		public Node<T> getNode() { return node; }
		public double getDis() { return dis; }
	}
	
	
	public List<Node<T>> longKnn(Node target, List<Node<T>> list) {
		
		List<pair> pairList = new ArrayList<pair>();
		List<Node<T>> nodeList = new ArrayList<Node<T>>();
		
		for (int i = 0; i < list.size(); i++) {
			double distance = distance(list.get(i), target);
			pair pr = new pair(list.get(i), distance);
			pairList.add(pr);
		}
		
		Collections.sort(pairList, paircomp);
		
		for (int i = 0; i < list.size(); i++) {
			nodeList.add(pairList.get(i).node);
		}
		
		
		
	return nodeList;
	}
	
	public final Comparator<pair> paircomp = new Comparator<pair>() {
		public int compare(pair a, pair b) {
			return Double.compare(a.dis, b.dis);
			
		}
	};
	
	
	
	
	

	Node<T> target;
	MinMaxPriorityQueue<Node> nodes;
	public MinMaxPriorityQueue<Node> Knn(Node target, int size) {
		this.target = target;
		nodes = MinMaxPriorityQueue.orderedBy(knncomp).maximumSize(size).create();
		nN(target, this.root, size, nodes);
		
		return nodes;
	}
	
	public void nN(Node<T> target, Node<T> curr, int size,MinMaxPriorityQueue<Node> nodes ) {
		boolean left = false;
		if (curr == null) {
			return;
		}
		
		if (nodes.size() < size) {
		nodes.add(curr);
		} else {
			Node<T> far = nodes.peekLast();
			double distance = knncomp.compare(curr, far);
			if (distance < 0) {
				nodes.add(curr);
			}
		}
		
		/* Recursively search the half of the tree that contains the test point. */
		
		if (target.data[curr.dep % dim] < curr.data[curr.dep % dim]) {
			nN(target, curr.left, size, nodes);
			left = true;
		} else {
			nN(target, curr.right, size, nodes);
		}
		
		
		
//				node.data[node.dep % dim] - target.data[node.dep % dim];
		/**
		 * If the candidate hyper sphere crosses this splitting plane, look on 
		 * the other side of the plane by examining the other subtree.
		 */
		double diff = Math.abs(curr.data[curr.dep % dim] - target.data[curr.dep % dim]);
		Node node = nodes.peekLast();
		double diss = distance(node, target);
		
		if (nodes.size() < size || diff < diss) {
			if (left) {
				nN(target, curr.right, size, nodes);
			} else {
				nN(target, curr.left, size, nodes);
			}
		}
		
	}
	
	
	public List radius(double[] cords) {
		List node = new ArrayList<T>();
		return node;
		
	}
	
	//===============================================
	public double distance(Node<T> This, Node<T> that) {
		if (This.data.length != that.data.length) throw new IllegalArgumentException("Differing dimentions");
		return enorm(sub(This, that));
	}
	
	public Node<T> sub(Node<T> This, Node<T> that) {
		if (This.data.length != that.data.length) throw new IllegalArgumentException("Differing dimentions");
		 double[] load;
		 load = new double[This.data.length];
		
		for (int i = 0; i < that.data.length; i++) {
			load[i] = This.data[i] - that.data[i];
		}
		
		
		Node<T> toReturn = new Node<T>(load, null, null, This.t);
		return toReturn;
	}
	
	public double enorm(Node<T> This) {
		return Math.sqrt(product(This));
	}
	
	
	public double product(Node<T> This) {
		if (This.data.length != This.data.length) throw new IllegalArgumentException("Differing dimentions");
		double sum = 0.0;
		for (int i = 0; i < This.data.length; i++) {
			sum = sum + (This.data[i] * This.data[i]);
		}
		return sum;
	}
	
//	public double vert(Node<T> that, int dim) {
//		double a = this.data[dim] - that.data[dim];
//		return a*a;
//	}
//=========================================================	
	
	
	
	
	
	
	public String toString(Node node, int depth) {
		StringBuffer toReturn = new StringBuffer();
		Node current = node;
		
		if (current == null) {
			toReturn.append("Empty");
		} else {
			toReturn.append(current.toString());
		}
		
		return toReturn.toString();
	}
	

	
}
