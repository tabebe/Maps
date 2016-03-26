package edu.brown.cs.rwdodd.maps;

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
  private List<String> strings;
  private Integer portNum;
  private String[] args;
  private final static Gson GSON = new Gson();

  /**
   * @param args the command line args
   */
  public static void main(String[] args) {

    new Main(args).run();
  }


  /**
   * @param args the command line args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   * runs app.
   */
  private void run() {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);
    OptionSet options = parser.parse(args);

    file = options.valueOf(fileSpec);
    runSparkServer();
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates
    = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.\n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer() {
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    try {
      sparkDb = new DbQuery(file);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


    FreeMarkerEngine freeMarker = createEngine();
    // Setup Spark Routes
    Spark.get("/home", new FrontHandler(), freeMarker);
    Spark.post("/update", new UpdateHandler());
  }

  /**
   * @author rwdodd
   *
   */
  private class FrontHandler implements TemplateViewRoute {
    /* (non-Javadoc)
     * @see spark.TemplateViewRoute#handle(spark.Request, spark.Response)
     */
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Maps");
      return new ModelAndView(variables, "play.ftl");

    }
  }

  /**
   * @author rwdodd
   *
   */
  private class UpdateHandler implements Route {
    /* (non-Javadoc)
     * @see spark.TemplateViewRoute#handle(spark.Request, spark.Response)
     */
    @Override
    public Object handle(final Request req, final Response res) {
      System.out.println("hello");
      QueryParamsMap qm = req.queryMap();

      Double latTL = Double.parseDouble(qm.value("latTL"));
      Double longTL = Double.parseDouble(qm.value("longTL"));
      Double latBR = Double.parseDouble(qm.value("latBR"));
      Double longBR = Double.parseDouble(qm.value("longBR"));

      List<Road> results = null;
      try {
        results = sparkDb.queryWays(latTL, longTL, latBR, longBR);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      Map<String, Object> variables = new ImmutableMap.Builder()
      .put("roads", results).build();

      return GSON.toJson(variables);

    }
  }

  /**
   * @author rwdodd
   *
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    private final Integer STATUS = 500;
    /* (non-Javadoc)
     * @see spark.ExceptionHandler#handle(java.lang.Exception, spark.Request, spark.Response)
     */
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(STATUS);
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
