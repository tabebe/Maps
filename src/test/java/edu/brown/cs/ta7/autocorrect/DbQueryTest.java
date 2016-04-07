package edu.brown.cs.ta7.autocorrect;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.brown.cs.ta7.maps.DbQuery;

public class DbQueryTest {
  
  
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
  
//  @Test
//  public void getAllNodesTest() throws ClassNotFoundException, SQLException {
//	DbQuery sql = new DbQuery("/home/ta7/course/cs032/Maps/smallMaps.sqlite3");
//    List<String> nodes = sql.getAllNodes();
//    assertTrue(nodes.size() == 6);
//  }
  

  

  
  @Test
  public void getWayTest() throws ClassNotFoundException, SQLException {
	  DbQuery sql = new DbQuery("/course/cs032/data/maps/smallMaps.sqlite3");
    
   
    String way = sql.getWay("/w/0");
    assertTrue(way.equals("Chihiro Ave"));
    
    
    way = sql.getWay("Not a name");
    assertTrue(way.isEmpty());
  }
  
  
  @Test
  public void getIDTest() throws ClassNotFoundException, SQLException {
	  DbQuery sql = new DbQuery("/course/cs032/data/maps/smallMaps.sqlite3");
	 
   
    String node = sql.getID("41.82", "-71.4");
    assertTrue(node.equals("/n/0"));
    
    
    node = sql.getWay("Not a name");
    assertTrue(node.isEmpty());
  }
  
  @Test
  public void getNodeWaysIDsTest() throws ClassNotFoundException, SQLException {
	  DbQuery sql = new DbQuery("/course/cs032/data/maps/smallMaps.sqlite3");

    
    List<String> ways = sql.getNodeWaysIDs("/n/0");
    assertTrue(ways.contains("/w/0"));
    assertTrue(ways.contains("/w/2"));
//    assertTrue(ways.contains("/w/3"));
    
    ways = sql.getNodeWaysIDs("Not a film");
    assertTrue(ways.isEmpty());
  }
  
  
}
