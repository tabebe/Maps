package edu.brown.cs.ta7.bacon;

import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import spark.template.freemarker.FreeMarkerEngine;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import freemarker.template.Configuration;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;




public class Main {
	private String name1;
	private String name2;
	private String database; 
	private String[] args;
	private int port = 1234;
	private static SQL sql;
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		new Main(args).run();
	}
	
	private Main(String[] args) {
		this.args = args;
	}
	
	private void run() throws ClassNotFoundException, SQLException {
		OptionParser parser = new OptionParser();
		parser.accepts("gui");
		OptionSet option = parser.parse(args);
		@SuppressWarnings("unchecked")
		List<String> nonOptions = (List<String>) option.nonOptionArguments();

		if (option.has("gui")) {
			
			if (nonOptions.size() != 1) {
				System.out.println("ERROR: too many arguements");
				System.exit(1);
			}

			database = nonOptions.get(0);
			runSparkServer();
		} else {
			
			if (nonOptions.size() != 3) {
				System.out.println("ERROR: wrong arguement format");
				System.exit(1);
			}

			name1 = nonOptions.get(0).trim();
			name2 = nonOptions.get(1).trim();
			database = nonOptions.get(2);
			try {
				Graph graph = new Graph(name1, name2, database);
				List<Actor> shortPath = graph.dijkstra();
				List<String> connects = graph.connectString(shortPath);

				for (String s : connects) {
					System.out.println(s);
				}

				graph.closeConnection();
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: database path not found");
				System.exit(1);
			} catch (IllegalArgumentException e) {
				System.out.println("ERROR: you must enter different actors");
				System.exit(1);
			} catch (SQLException e) {
				System.out.println("ERROR: SQL Exception");
				System.exit(1);
			}

		}
	}
	
	
	
	
	private void runSparkServer() {
		Spark.externalStaticFileLocation("src/main/resources/static");
		Spark.setPort(port);
		Spark.exception(Exception.class, (ExceptionHandler) new ExceptionPrinter());
		FreeMarkerEngine freeMarker = createEngine();

		Spark.get("/bacon", new FrontHandler(), freeMarker);
    	Spark.post("/results", new ResultsHandler(), freeMarker);
    	Spark.get("/actors/:name", new ActorsHandler(), freeMarker);
    	Spark.get("/movies/:name", new MovieHandler(), freeMarker);
	}

	private static FreeMarkerEngine createEngine() {
	    Configuration config = new Configuration();
	    File templates = new File(
	      "src/main/resources/spark/template/freemarker");
	    try {
	      config.setDirectoryForTemplateLoading(templates);
	    } catch (IOException ioe) {
	      System.out.printf("ERROR: Unable "
	          + "use %s for template loading.\n", templates);
	      System.exit(1);
	    }
	    return new FreeMarkerEngine(config);
	  }
		
	
	private class FrontHandler implements TemplateViewRoute {
	    @Override
	    public ModelAndView handle(Request req, Response res) {
	      Map<String, Object> variables =
	        ImmutableMap.of("title", "Bacon", "message", "");
	      return new ModelAndView(variables, "main.ftl");
	    }
	  }

	
	
	private class ResultsHandler implements TemplateViewRoute {
	    @Override
	    public ModelAndView handle(Request req, Response res) {
	      QueryParamsMap qm = req.queryMap();
	      String actor1 = qm.value("textbox1");
	      String actor2 = qm.value("textbox2");
	      String toPrint = "";
	      String beginning = "<div id=\"results\">";
	      String end = "</div>";
	      try {
	        Graph bg = new Graph(actor1, actor2, database);
	        List<Actor> path = bg.dijkstra();
	        if (path.isEmpty()) {
	          String nameOne = "<a href=\"/actors/"
	              + sql.getActorID(actor1).replaceAll("/", ".")
	              + "\">" + actor1 + "</a>";
	          String nameTwo = "<a href=\"/actors/"
	              + sql.getActorID(actor2).replaceAll("/", ".")
	              + "\">" + actor2 + "</a>";
	          toPrint += beginning + (nameOne + " -/- " + nameTwo) + end;
	        } else {
	          int i;
	          toPrint += beginning;
	          for (i = 0; i < path.size() - 1; i++) {
	            Actor a1 = path.get(i);
	            Actor a2 = path.get(i + 1);
	            String nameWon = "<a href=\"/actors/"
	                + a1.getObject().replaceAll("/", ".") + "\">"
	                + a1.getName() + "</a>";
	            String nameToo = "<a href=\"/actors/"
	                + a2.getObject().replaceAll("/", ".") + "\">"
	                + a2.getName() + "</a>";
	            String film = "<a href=\"/movies/"
	                + a2.getMovieID().replaceAll("/", ".") + "\">"
	                + a2.getMovie() + "</a>";
	            toPrint += nameWon + " => " + nameToo + " : " + film + "</br>";
	          }
	          toPrint += end;
	        }
	      } catch (ClassNotFoundException
	          | IllegalArgumentException | SQLException e) {
	        toPrint += beginning + "ERROR: Improper inputs."
	            + "Must input valid actor names." + end;
	      }
	      Map<String, Object> variables =
	        ImmutableMap.of("title", "Bacon", "message", toPrint);
	      return new ModelAndView(variables, "main.ftl");
	    }
	  }
	

	
	
	  private class ActorsHandler implements TemplateViewRoute {
		    @Override
		    public ModelAndView handle(Request req, Response res) {
		      String actorID = req.params(":name");
		      actorID = actorID.replace(".", "/");
		      String toReturn = "";
		      String beginning = "<div id=\"results\">";
		      String end = "</div>";
		      try {
		        List<String> filmIDs = sql.getActorFilmsIDs(actorID);
		        toReturn += beginning;
		        for (String fid : filmIDs) {
		          String filmLink = "<a href=\"/movies/"
		              + fid.replace("/", ".") + "\">" + sql.getFilm(fid) + "</a>";
		          toReturn += filmLink + "</br>";
		        }
		        toReturn += end;
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		      Map<String, Object> variables =
		        ImmutableMap.of("title", "Bacon", "results", toReturn);
		      return new ModelAndView(variables, "filmsactors.ftl");
		    }
		  }
	  
	  
	  
	  private class MovieHandler implements TemplateViewRoute {
		    @Override
		    public ModelAndView handle(Request req, Response res) {
		      String filmID = req.params(":name");
		      filmID = filmID.replace(".", "/");
		      String toReturn = "";
		      String beginning = "<div id=\"results\">";
		      String end = "</div>";
		      try {
		        List<String> actorIDs = sql.getFilmActorsIDs(filmID);
		        toReturn += beginning;
		        for (String aid : actorIDs) {
		          String actorLink = "<a href=\"/actors/"
		              + aid.replace("/", ".") + "\">" + sql.getActor(aid) + "</a>";
		          toReturn += actorLink + "</br>";
		        }
		        toReturn += end;
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		      Map<String, Object> variables =
		        ImmutableMap.of("title", "Bacon", "results", toReturn);
		      return new ModelAndView(variables, "movieactors.ftl");
		    }
		  }

	  
	  
	  private static class ExceptionPrinter implements ExceptionHandler {
		    @Override
		    public void handle(Exception e, Request req, Response res) {
		      res.status(500);
		      StringWriter stacktrace = new StringWriter();
		      try (PrintWriter pw = new PrintWriter(stacktrace)) {
		        pw.println("<pre>");
		        e.printStackTrace(pw);
		        pw.println("</pre>");
		      }
		      res.body(stacktrace.toString());
		    }
		  }
	
	
}