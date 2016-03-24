package edu.brown.cs.ta7.KDTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;


public class Node<T> implements NodeData{
	double[] data;
	public Node<T> parent;
	public Node<T> left;
	public Node<T> right;
	public int dep;
	public T t;
	
	
	
	public Node(double[] data, Node<T> left, Node<T> right, T t){
		this.data = data;
		this.left = left;
		this.right = right;
		this.t = t;
	}
	
	public double getAxis(int dim){
		return data[dim];
	}
	
	public double[] getDouble(){
		return data;
	}
	
	
	//Test Helpers--------------------------------
	public double findD(Node<T> target) {
		return this.eclid(target);
	}
	
	public double shortest(List<Node<T>> target) {
		double[] disList;
		disList = new double[target.size()];
		
		
		for (int i = 0; i < target.size(); i++) {
			disList[i] = this.eclid(target.get(i));
		}
		Arrays.sort(disList);
		
		return disList[1];
	}
	//--------------------------------------------
	
	Stack<Node<T>> st = new Stack<Node<T>>();
	List<Node<T>> node = new ArrayList<Node<T>>();
	public Node<T> knn(Node target) {
		
		this.nn(target);
		return st.pop();
	}
	
	
	public Node<T> nn(Node target) {
		
		
		
		int direction = comp.compare(this, target);
		Node<T> next = (direction < 0) ? this.left : this.right;
		//Node<T> other = (direction < 0) ? this.right : this.left;
		//Node<T> best = (next == null) ? this : next.nn(next);
		
		
		if (next != null) {
			st.push(next.nn(target));
		} 
		st.push(this);
		return this;
//		st.push(best);
//		if (this.eclid(target) < best.eclid(target)) {
//			best = this;
//			st.push(this);
//		}
//		
//		if (other != null) {
//			int dim = this.dep % this.data.length;
//			if (this.vert(target, dim) < best.eclid(target)){
//				Node<T> possbest = other.nn(target);
//				if (possbest.eclid(target) < best.eclid(target)) {
//					best = possbest;
//					st.push(this);
//				}
//			}
//		}
		
		//return st.pop();
	}
	
	public static final Comparator<Node> comp = new Comparator<Node>() {
		public int compare(Node a, Node b) {
			return Double.compare(a.getAxis(a.dep % a.data.length), b.getAxis(a.dep % a.data.length));
			
		}
	};
	
	
	
	//Helper functions for KNN and Radius 
	//---------------------------------------------------------------------


	
	
	public double eclid(Node<T> that) {
		if (this.data.length != that.data.length) throw new IllegalArgumentException("Differing dimentions");
		return this.sub(that).enorm();
	}
	
	public Node<T> sub(Node<T> that) {
		if (this.data.length != that.data.length) throw new IllegalArgumentException("Differing dimentions");
		 double[] load;
		 load = new double[this.data.length];
		
		for (int i = 0; i < that.data.length; i++) {
			load[i] = this.data[i] - that.data[i];
		}
		
		
		Node<T> toReturn = new Node<T>(load, null, null, this.t);
		return toReturn;
	}
	
	public double enorm() {
		return Math.sqrt(this.product(this));
	}
	
	
	public double product(Node<T> that) {
		if (this.data.length != that.data.length) throw new IllegalArgumentException("Differing dimentions");
		double sum = 0.0;
		for (int i = 0; i < this.data.length; i++) {
			sum = sum + (this.data[i] * that.data[i]);
		}
		return sum;
	}
	
	public double vert(Node<T> that, int dim) {
		double a = this.data[dim] - that.data[dim];
		return a*a;
	}
	//-------------------------------------------------------------------------
	
	
	
	
	
	
	StringBuffer toReturn = new StringBuffer();
	public String toString(){
		
		toReturn.append("(");
		for (int i = 0; i < data.length; i++) {
			toReturn.append(data[i] + " ");
		}
		toReturn.append(")");

//		if (this.left != null) {
//			this.left.toString(depth++);
//		}
//		
//		if (this.right != null) {
//			this.right.toString(depth++);
//		}
//		
		return toReturn.toString();
		
	}
	
	public Node<T> getLeft() {
		return this.left;
	}
	
	public Node<T> getRight() {
		return this.right;
	}
	
	
	
	
	

}
