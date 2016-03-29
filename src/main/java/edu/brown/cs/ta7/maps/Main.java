package edu.brown.cs.ta7.maps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import freemarker.template.Configuration;

/**
 * @author rwdodd
 *
 */
public class Main {
  private File file;
  private static DbQuery sparkDb;
 // private static Graph sparkGraph;
 // private static Dijkstra dijkstra;
  private String name1;
  private String name2;
  private String db;
  private List<String> strings;
  private Integer portNum;
  private String[] args;
  private final static Gson GSON = new Gson();

  /**
   * @param args the command line args
   */
  public static void main(String[] args) 
  throws ClassNotFoundException, SQLException {
	  	new Main(args).run();

  }


  /**
   * @param args the command line args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  
  
  
  private void run() throws ClassNotFoundException, SQLException {
	  OptionParser parser = new OptionParser();
	  
	  OptionSet options = parser.parse(args);
	  @SuppressWarnings("unchecked")
	  List<String> listNonOpts = (List<String>) options.nonOptionArguments();
	  
	  if (listNonOpts.size() != 3) {
	        System.out.println("ERROR: Arguments must be in"
	            + "the form of <name1> <name2> <sql_db>.");
	        System.exit(1);
	      }
	      name1 = listNonOpts.get(0).trim();
	      name2 = listNonOpts.get(1).trim();
	      db = listNonOpts.get(2);
	      try {
	        //creates bacon graph, calls dijkstras,
	        //then prints out shortest path if found.
	        Graph bg = new Graph(name1, name2, db);
	        List<Node> shortestPath = bg.dijkstra();
	        List<String> conns = bg.connectString(shortestPath);
	        for (String a : conns) {
	          System.out.println(a);
	        }
	        bg.closeConnection();
	      } catch (ClassNotFoundException e) {
	        System.out.println("ERROR: DB not found.");
	        System.exit(1);
	      } catch (SQLException e) {
	        System.out.println("ERROR: SQLException error.");
	        System.exit(1);
	      } catch (IllegalArgumentException e) {
	        System.out.println("ERROR: Cannot make"
	            + "actor pass bacon to him/her/pheself.");
	        System.exit(1);
	      }
	  
	  
  }
  
  
  
}
