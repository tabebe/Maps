package edu.brown.cs.ta7.autocorrect;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.ta7.findPath.Edge;
import edu.brown.cs.ta7.maps.Node;
import edu.brown.cs.ta7.maps.Way;



public class EdgeTest {
  
  @BeforeClass
  public static void setUpClass() throws Exception {
    
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    // (Optional) Code to run after all tests finish goes here.
  }

  @Before
  public void setUp() {
    // (Optional) Code to run before each test case goes here.
  }

  @After
  public void tearDown() {
    // (Optional) Code to run after each test case goes here.
  }
  
  @Test
  public void addAndGetEdgesTest() {
    //tests if edges are added to vertex's list of edges properly
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    Node v1 = new Node("nodeID1", list);
    Node v2 = new Node("nodeID2", list);
    Node v3 = new Node("nodeID3", list);
    Node v4 = new Node("nodeID4", list);
    
    Way e1 = new Way(v1, v2, "wayName1", "wayID1", 1);
    Way e2 = new Way(v1, v3, "wayName2", "wayID2", 1);
    Way e3 = new Way(v1, v4, "wayName3", "wayID3", 1);
  
    v1.addEdge(e1);
    v1.addEdge(e2);
    v1.addEdge(e3);
    
    List<Edge<String>> testList = v1.getEdges();
    
    assertTrue(testList.contains(e1));
    assertTrue(testList.contains(e2));
    assertTrue(testList.contains(e3));
  }
  

  
  @Test
  public void setAndGetPreviousTest() {
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    Node v1 = new Node("NodeID1", list);
    Node v2 = new Node("NodeID2", list);
    Node v3 = new Node("NodeID3", list);
    Node v4 = new Node("NodeID4", list);
    
    v4.setPrev(v3);
    v3.setPrev(v2);
    v2.setPrev(v1);
    
    assertTrue(v4.getPrev().equals(v3));
    assertTrue(v3.getPrev().equals(v2));
    assertTrue(v2.getPrev().equals(v1));
    //v1 does not have a previous vertex since it is the starting
    //node, so its previous should be null.
    assertNull(v1.getPrev());
  }
  
  @Test
  public void setAndGetDistanceTest() {
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    //when a vertex is first instantiated, distance is set to inf
    Node v1 = new Node("NodeID1", list);
    assertTrue(v1.getDistance() == Double.POSITIVE_INFINITY);
    v1.setDistance(100);
    assertTrue(v1.getDistance() == 100);
  }

  @Test
  public void getObjectTest() {
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    Node v1 = new Node("NodeID1", list);
    assertTrue(v1.getObject().equals("NodeID1"));
  }
  
  @Test
  public void equalsTest() {
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    Node v1 = new Node("NodeID1", list);
    Node v2 = new Node("NodeID2", list);
    Node v3 = new Node("NodeID1", list);
    assertTrue(!v1.equals(v2));
    assertTrue(v1.equals(v3));
  }
  
  @Test
  public void setAndGetFilmTest() {
	  ArrayList list = new ArrayList<Double>();
		list.add(1.0);
		list.add(2.0);
		
    Node v1 = new Node("NodeID1", list);
    v1.setWay("dawg");
    assertTrue(v1.getWay().equals("dawg"));
  }
}
