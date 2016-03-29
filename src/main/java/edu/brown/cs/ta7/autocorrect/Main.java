package edu.brown.cs.ta7.autocorrect;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
public final class Main {

  private static Trie trie;
  private static GramTable gramTable;
  private List<File> files;
  private static Integer maxDist;
  private static boolean prefix = false;
  private static boolean wspace = false;
  private static boolean smart = false;
  private final static Gson GSON = new Gson();

  /**
   * @param args the command line args
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

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
    OptionSpec<Integer> ledArg
      = parser.accepts("led").withRequiredArg().ofType(Integer.class);
    parser.accepts("prefix");
    parser.accepts("whitespace");
    parser.accepts("smart");
    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);
    OptionSet options = parser.parse(args);

    files = options.valuesOf(fileSpec);
    if (files == null) {
      System.out.println("ERROR: Please specify a file");
      System.exit(1);
    }
    if (files.isEmpty()) {
      System.out.println("ERROR: Please specify a file");
      System.exit(1);
    }
    if (options.has("prefix")) {
      prefix = true;
    }
    if (options.has("smart")) {
      smart = true;
    }
    if (options.has("whitespace")) {
      wspace = true;
    }
    if (options.has("led")) {
      if (options.hasArgument(ledArg)) {
        maxDist = options.valueOf(ledArg);
      } else {
        System.out.println("ERROR: Please specify an arg for led");
        System.exit(1);
      }
    } else {
      maxDist = 0;
    }
    if (options.has("gui")) {
      runSparkServer();
    } else {

      @SuppressWarnings("unused")
      Repl repl = new Repl(files, maxDist, prefix, wspace, smart);
      // Process commands
    }
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

    trie = new Trie(files, maxDist);

    gramTable = new GramTable(files);

    FreeMarkerEngine freeMarker = createEngine();
    // Setup Spark Routes
    Spark.get("/auto", new FrontHandler(), freeMarker);
    Spark.post("/suggest", new SuggestHandler());
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
      ArrayList<String> results = new ArrayList<String>();
      results.add("");
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database");
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * @author rwdodd
   *
   */
  private static class SuggestHandler implements Route  {

    private final Integer MAX_RESULTS = 5;

    /* (non-Javadoc)
     * @see spark.Route#handle(spark.Request, spark.Response)
     */
    @Override
    public Object handle(final Request req, final Response res) {
      QueryParamsMap qm = req.queryMap();
      String line = qm.value("text");
      ArrayList<String> results = new ArrayList<String>();
      line = line.toLowerCase();
      char[] lineChars = line.toCharArray();
      if (lineChars[lineChars.length - 1] == ' ') {
        //trailing whitespace occurs
        Map<String, Object> variables = ImmutableMap.of("title",
            "Stars: Query the database", "results", new String[0]);
        return GSON.toJson(variables);

      }
      line = line.replaceAll("[^a-z]", " ");
      line = line.replaceAll("\\.", " ");
      line = line.replaceAll("\\s+", " ");
      String[] lineArr = line.split(" ");
      String lastWord = lineArr[lineArr.length - 1];
      ArrayList<String> levSuggs = new ArrayList<String>();
      ArrayList<String> preSuggs = new ArrayList<String>();
      ArrayList<Bigram> wspaceSuggs = new ArrayList<Bigram>();
      if (maxDist >= 0) {
        levSuggs = trie.levSearch(lastWord);
      }
      if (prefix) {
        preSuggs = trie.prefixSearch(lastWord);
      }
      if (wspace) {
        wspaceSuggs = trie.whitespaceSearch(lastWord);
      }
      TreeSet<Suggestion> suggs
        = new TreeSet<Suggestion>(new SuggComparator(line, smart));
      String suggTemp = "";
      String prevWord;
      for (int i = 0; i < lineArr.length - 1; i++) {
        suggTemp = suggTemp + lineArr[i] + " ";
      }
      if (lineArr.length == 1) {
        prevWord = "";
      } else {
        prevWord = lineArr[lineArr.length - 2];
      }
      for (String s:levSuggs) {
        String strSugg = suggTemp + s;
        Integer bCount = gramTable.getBigram(new Bigram(prevWord, s));
        Integer uCount = gramTable.getUnigram(s);
        Suggestion sugg = new Suggestion("led", strSugg, bCount, uCount, s);
        suggs.add(sugg);
      }
      for (String s:preSuggs) {
        String strSugg = suggTemp + s;
        Integer bCount = gramTable.getBigram(new Bigram(prevWord, s));
        Integer uCount = gramTable.getUnigram(s);
        Suggestion sugg = new Suggestion("pre", strSugg, bCount, uCount, s);
        suggs.add(sugg);
      }
      for (Bigram b: wspaceSuggs) {
        String strSugg = suggTemp + b.getWord1() + " " + b.getWord2();
        Integer bCount =
            gramTable.getBigram(new Bigram(prevWord, b.getWord1()));
        Integer uCount = gramTable.getUnigram(b.getWord1());
        String comp = b.getWord1() + " " + b.getWord2();
        Suggestion sugg =
            new Suggestion("wspace", strSugg, bCount, uCount, comp);
        suggs.add(sugg);
      }
      for (int i = 0; i < MAX_RESULTS; i++) {
        Suggestion sugg = suggs.pollFirst();
        if (sugg == null) {
          break;
        }
        results.add(sugg.toString());
      }
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "results", results);
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
