package edu.brown.cs.ta7.autocorrect;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



import edu.brown.cs.ta7.maps.Node;
import edu.brown.cs.ta7.maps.Way;

public class WayTest {
  
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
  public void edgeGetOtherVertexTest() {
    //test if getOtherVertex() returns the proper other vertex
	 
    Node v1 = new Node("temp1");
    Node v2 = new Node("temp2");
    Way e1 = new Way(v1, v2, "temp", "temp", 1);
    assertTrue(e1.getOppositeVertex(v1).equals(v2));
    assertTrue(e1.getOppositeVertex(v2).equals(v1));
  }
  
  @Test
  public void edgeGetNameAndIDTest() {
    //test if getName() and getID() returns the proper other vertex
	 
    Node v1 = new Node("temp1");
    Node v2 = new Node("temp2");
    Way e1 = new Way(v1, v2, "WayName", "WayID",1);
    assertTrue(e1.getName().equals("WayName"));
    assertTrue(e1.getID().equals("WayID"));
  }
}
