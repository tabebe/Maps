<<<<<<< HEAD
package edu.brown.cs.ta7.nearest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * KDTree class. This class takes in a list of KDNodes
 * and constructs a KDTree based on the coordinates
 * stored in each KDNode in the list. This class also has two
 * functions neighborSearch() and radiusSearch() that returns
 * the nearest neighbors or nodes within a radius of a given
 * coordinate.
 * @author ta7
 * @param <T> generic
 *
 */
public class KDTree<T> {
  private KDNode<T> rootNode;
  private PriorityQueue<KDNode<T>> queue;
  private static int dimensions, neighbors, size;
  private double radius;
  private ArrayList<String> tostring;
  /**
   * KDTree constructor. Takes in an integer k as the dimensions
   * of the KDTree, and a list of KDNodes to be put into a
   * KDTree. The global queue declared above is used for
   * both neighborSearch() and radiusSearch() since it automatically
   * sorts the found nodes in proper order from closest to furthest
   * to a given coordinate.
   * @param k dimensions
   * @param nodes list of nodes.
   */
  public KDTree(int k, List<KDNode<T>> nodes) {
    tostring = new ArrayList<String>();
    size = 0;
    rootNode = buildTree(0, k, nodes);
    dimensions = k;
  }
  /**
   * buildTree() recursively builds the tree. The two base cases
   * in my design are if a sublist is either size 1 or 2. If it is
   * size 1, then simply set that node's left and right children to null.
   * If it is size 2, then find the node that has a greater coordinate for
   * that axis and make it the parent, set it's left child pointer to the other
   * node. Then, set both left and right children of that child to null, and the
   * right child of the parent to null. This function also keeps
   * track of the size of the tree as it is being built so that if a user
   * wants to find an amount of neighbors that is greater than the
   * size of the tree, an error will be printed to the user.
   * @param d depth of tree
   * @param k dimensions
   * @param n list of nodes
   * @return node
   */
  public KDNode<T> buildTree(int d, int k, List<KDNode<T>> n) {
    int axis = d % k;
    int median;
    size += 1;
    //base case of 1 node; set left and right to null (leaf node)
    if (n.size() == 1) {
      n.get(0).setLeft(null);
      n.get(0).setRight(null);
      return n.get(0);
      //base case of 2 nodes, find greater node (based on current
      //axis) and make it the parent, while making the lesser node
      //the child.
    } else if (n.size() == 2) {
      KDNode<T> curr1 = n.get(0);
      KDNode<T> curr2 = n.get(1);
      size += 1;
      if (curr1.getCoors().get(axis)
          >= curr2.getCoors().get(axis)) {
        curr1.setLeft(curr2);
        curr1.setRight(null);
        curr2.setLeft(null);
        curr2.setRight(null);
        return curr1;
      } else {
        curr2.setLeft(curr1);
        curr2.setRight(null);
        curr1.setLeft(null);
        curr1.setRight(null);
        return curr2;
      }
    }
    //sort nodes on axis and find median node
    Collections.sort(n, KDNode.compareCoor(axis));
    median = n.size() / 2;
    KDNode<T> curr = n.get(median);
    //recurrsively build left and right side of the tree
    curr.setLeft(buildTree(d + 1, k, n.subList(0, median)));
    curr.setRight(buildTree(d + 1, k, n.subList(median + 1, n.size())));
    return curr;
  }
  /**
   * getRoot() accessor method returns the root node.
   * @return rootNode
   */
  public KDNode<T> getRoot() {
    return rootNode;
  }
  /**
   * neighborSearch() sets up variables needed for
   * a recursive search of the tree. This also checks if the number
   * of neighbors the user is asking for is greater than the size
   * of the tree. If it is, return. This function uses a special
   * priority queue in which the comparator it takes in is flipped.
   * The comparator allows the PQ to sort its nodes based on their
   * distance from the given target coordinates. However,
   * the PQ is ordered as a maximum-based PQ, in which it'll store
   * the closest nodes to the target, with the head of the queue
   * being the best worst node. This way, if the size of the queue
   * is equal to the amount of neighbors needed to return, the funciton
   * pulls from the head of the queue and checks if the current node to be
   * potentially added is closer than the previous head. If it is, then
   * replace that pulled node with the new node and repeat this process
   * until we find the closest k neighbors.
   * @param amt amount of neighbors
   * @param coors coordinates of target
   * @return array list of neighbors
   */
  public ArrayList<KDNode<T>> neighborSearch(int amt, ArrayList<Double> coors) {
    ArrayList<KDNode<T>> nearestNeighbors = new ArrayList<KDNode<T>>();
    if (amt > size) {
      return nearestNeighbors;
    }
    KDNode<T> curr = getRoot();
    neighbors = amt;
    queue = new PriorityQueue<KDNode<T>>(KDNode.compareFlippedDist());
    recurNeighborSearch(coors, 0, dimensions, curr);
    //since the queue is reversed, the function polls nodes from the
    //head of the queue and pushes them onto a stack. the function
    //then pops from the stack and adds the nodes to the arraylist
    //to have them sorted in proper order form closest node to least
    //closest
    Stack<KDNode<T>> stack = new Stack<KDNode<T>>();
    for (int i = 0; i < neighbors; i++) {
      stack.push(queue.poll());
    }
    for (int i = 0; i < neighbors; i++) {
      nearestNeighbors.add(stack.pop());
    }
    return nearestNeighbors;
  }
  /**
   * recurNeighborSearch() recursively searches tree for nearest neighbor.
   * for each recursive call on a node, calculate and set the distance
   * from the current node to the target coordinates. The PQ is
   * maximum-based, so the head of the PQ is the furthest closest
   * node to the target. If the priority queue
   * is full (size = amt of neighbors),
   * then pull from the head of the queue and check if the head is closer
   * than the current node to the target. if it is, then keep the head and
   * ignore the current node. otherwise, replace the head with the current node.
   * The function returns on a leaf node or a null node. The algorithm then
   * recursively searches the left or right half
   * of the subtree (depending on
   * which side is closer to the target). In
   * the case in which a node is actually
   * closer on the side that was "chopped" off,
   * then the algorithm forces itself to
   * search that side of the tree.
   * @param target target coords
   * @param depth depth of tree
   * @param k dimensions
   * @param curr current node
   */
  public void recurNeighborSearch(ArrayList<Double> target, int depth, int k,
      KDNode<T> curr) {
    int axis = depth % k;
    boolean left;
    if (curr == null) {
      return;
    }
    curr.setDistFromTarg(distance(curr.getCoors(), target));
    if (queue.size() == neighbors) {
      KDNode<T> prev = queue.poll();
      if (prev.getDistFromTarg() < curr.getDistFromTarg()) {
        queue.add(prev);
      } else {
        queue.add(curr);
      }
    } else {
      queue.add(curr);
    }
    if (curr.getLeft() == null && curr.getRight() == null) {
      return;
    }
    //recursively search half of tree that is closer to target
    if (target.get(axis) < curr.getCoors().get(axis)) {
      left = true;
      recurNeighborSearch(target, depth + 1, k, curr.getLeft());
    } else {
      left = false;
      recurNeighborSearch(target, depth + 1, k, curr.getRight());
    }
    //search chopped of hyperspace in the case in which
    //a node is potentially closer to target than the cut
    //implies.
    if ((queue.size() < neighbors)
        || Math.abs(curr.getCoors().get(axis) - target.get(axis))
        <= queue.peek().getDistFromTarg()) {
      if (left) {
        recurNeighborSearch(target, depth + 1, k, curr.getRight());
      } else {
        recurNeighborSearch(target, depth + 1, k, curr.getLeft());
      }
    }
    return;
  }
  /**
   * radiusSearch() sets up variables needed for
   * a recursive radius search of the kd-tree. Sets up
   * priority queue with un-flipped comparator (so that it
   * is minimum-based) since radius search does not require us
   * to compare distances amongst the neighboring nodes. This uses
   * a PQ to sort the nodes from closest to furthest from the target,
   * but within the radius.
   * @param r double radius
   * @param coors arraylist of target coordinates
   * @return an array list of nodes
   */
  public ArrayList<KDNode<T>> radiusSearch(double r, ArrayList<Double> coors) {
    KDNode<T> curr = getRoot();
    radius = r;
    queue = new PriorityQueue<KDNode<T>>(KDNode.compareDist());
    ArrayList<KDNode<T>> radiusNodes = new ArrayList<KDNode<T>>();
    recurRadiusSearch(coors, 0, dimensions, curr);
    while (!(queue.isEmpty())) {
      radiusNodes.add(queue.poll());
    }
    return radiusNodes;
  }
  /**
   * recurRadiusSearch that recursively searches tree for
   * nodes within a given radius. Calculates the distance
   * from current node to target, if it is less than the radius,
   * then add it to the PQ. This method accounts for the case
   * in which the radius cuts into another hyperspace, and
   * so the algorithm searches those trees as well.
   * @param target arraylist of coors
   * @param depth depth of the tree
   * @param k dimensions
   * @param curr current node
   */
  public void recurRadiusSearch(ArrayList<Double> target, int depth, int k,
      KDNode<T> curr) {
    int axis = depth % k;
    boolean left;
    if (curr == null) {
      return;
    }
    curr.setDistFromTarg(distance(curr.getCoors(), target));
    if (curr.getDistFromTarg() <= radius) {
      queue.add(curr);
    }
    if (curr.getLeft() == null && curr.getRight() == null) {
      return;
    }
    if (target.get(axis) < curr.getCoors().get(axis)) {
      left = true;
      recurRadiusSearch(target, depth + 1, k, curr.getLeft());
    } else {
      left = false;
      recurRadiusSearch(target, depth + 1, k, curr.getRight());
    }
    //case for when radius cuts into other hyperspace, so search those
    //trees as well
    if (Math.abs(curr.getCoors().get(axis) - target.get(axis)) <= radius) {
      if (left) {
        recurRadiusSearch(target, depth + 1, k, curr.getRight());
      } else {
        recurRadiusSearch(target, depth + 1, k, curr.getLeft());
      }
    }
    return;
  }
  /**
   * distance() finds distance between 2 points for k dimensions.
   * @param coors1 first set of coordinates
   * @param coors2 second set of coordinates
   * @return euclidean distance between the two
   */
  public double distance(ArrayList<Double> coors1, ArrayList<Double> coors2) {
    double sum = 0;
    for (int i = 0; i < dimensions; i++) {
      sum += Math.pow(coors1.get(i) - coors2.get(i), 2);
    }
    return Math.sqrt(sum);
  }
  /**
   * returns string that contains values for the
   * coordinates of each node (used in my JUNIT test to
   * check that the KDTree was in the proper order).
   * @return string
   */
  public ArrayList<String> makeString() {
    toString(rootNode);
    return tostring;
  }
  /**
   * in-order traversal of the tree that prints
   * the coordinates of each node .
   * @param node node
   */
  public void toString(KDNode<T> node) {
    if (node.getLeft() != null) {
      toString(node.getLeft());
    }
    tostring.add(String.valueOf(node.getCoors().get(0)));
    if (node.getRight() != null) {
      toString(node.getRight());
    }
  }
}

=======
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
>>>>>>> 9904081ebdd478e22190d21e4ec54d822779322f
