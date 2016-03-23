package edu.brown.cs.ta7.autocorrect;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;







public class Main {


private static boolean prefix = false;
private static boolean whitespace = false;
private static boolean gui = false;
private static Gson GSON = new Gson();
private static Trie trie;
private static Ranker ranker;
private static List<String> fileList = new ArrayList<String>();
private static int numMatches = 5;




  public static void main(String[] args) throws IOException {
    trie = new Trie();
    ranker = new Ranker();
              //parse args
    		  
              if (args.length == 0) {
                System.out.println("Error: No arguments.");
                System.exit(1);
              }
              for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--whitespace")) {
                  whitespace = true;
                } else if (args[i].equals("--prefix")) {
                  prefix = true;
                } else if (args[i].equals("--gui")) {
                  gui = true;
                } else if (args[i].endsWith(".txt")) {
                  fileList.add(args[i]);
                }
              }

    //read text files
    if (fileList.isEmpty()) {
      System.out.println("Error: no text file found");
      System.exit(1);
    } else {
      for (String f : fileList) {
        readFile(f);
      }
    }

    //set up UI
    if (gui) {
      runSparkServer();
    } else {
      runCommandLine();
    }
  }




  public static void readFile(String file) {

    List<String> wordlist = new ArrayList<String>();
    BufferedReader br = null;
    String word1 = "";
    String word2 = "";
    String line = "";
    try {
      br = new BufferedReader(new FileReader(file));
       while ((line = br.readLine()) != null) {
         line = line.replaceAll("[^a-zA-z ]", " ").toLowerCase().trim();
         List<String> words = new ArrayList<String>(Arrays.asList(line.split(" ")));
         wordlist.addAll(words);
       
         if (words.size() >= 2) {
          	word1 = words.get(0);
          	word2 = words.get(1);
          	trie.addWord(word1);
          	trie.addWord(word2);
          	ranker.addToHash(word1, true);
          	ranker.addToHash(word2, true);
          	ranker.addToHash(word1 + " " + word2, false);

          	for (int i = 2; i < words.size(); i++) {
          		word1 = word2;
          		word2 = words.get(i);
          		trie.addWord(word2);
          		ranker.addToHash(word2, true);
          		ranker.addToHash(word1 + " " + word2, false);
          	}
          } else if (words.size() == 2) {
          	word1 = words.get(0);
          	word2 = words.get(1);
          	trie.addWord(word1);
          	trie.addWord(word2);
          	ranker.addToHash(word1, true);
          	ranker.addToHash(word2, true);
          	ranker.addToHash(word1 + " " + word2, false);
          } else if (words.size() == 1) {
          	word1 = words.get(0);
          	trie.addWord(word1);
          	ranker.addToHash(word1, true);
          }
       
       
       }

       

    } catch (FileNotFoundException e) {
      System.out.println("Error: file not found.");
      System.exit(1);
    } catch (IOException e) {
      System.out.println("Error: io exception");
      System.exit(1);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          System.out.println("Error: io exception");
          System.exit(1);
        }
      }
    }
  }


  public static void runCommandLine() throws IOException {
    System.out.println("Ready");
    String toReturn = "";
    List<String> matchesMain = new ArrayList<String>();
    List<String> matches = new ArrayList<String>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        break;
      }
     
      line = line.replaceAll("[^a-zA-z ]", " ").replaceAll("\\s+", " ").toLowerCase().trim();
      String[] inputs = line.split(" ");
      
      // Genegrates prefix matches
      if (prefix) {
    	
        if (trie.prefixMatching(inputs[inputs.length - 1]) != null) {
          matches = trie.prefixMatching(inputs[inputs.length - 1]);
          
          for (String s : matches) {
            if (!(matchesMain.contains(s))) {
              matchesMain.add(s);
            }
          }
        }
      }
      // Generates white space matches
      if (whitespace) {
        if (trie.whiteSpaceSplit(inputs[inputs.length - 1]) != null) {
          matches = trie.whiteSpaceSplit(inputs[inputs.length - 1]);
          for (String s : matches) {
            if (!(matchesMain.contains(s))) {
              matchesMain.add(s);
            }
          }
        }
      }


      // Prints out the found matches
     if (matchesMain.size() >= numMatches) {
      for (int i = 0; i < numMatches; i ++) {
        if (inputs.length > 1) {
          toReturn = line.substring(0, line.lastIndexOf(" ")).trim() + " " + matchesMain.get(i);
        } else {
          toReturn = matchesMain.get(i);
        }
        System.out.println(toReturn);
      }
     }
      System.out.println();
      matchesMain.clear();
    }
  }

  
  private static void runSparkServer() {
	    Spark.externalStaticFileLocation("src/main/resources/static");
	    Spark.setPort(2345);
	    Spark.get("/autocorrect", new GetHandler(), new FreeMarkerEngine());
	    Spark.post("/results", new ResultsHandler());
	  }

	  private static class GetHandler implements TemplateViewRoute {
	    @Override
	    public ModelAndView handle(Request req, Response res) {
	      Map<String, Object> var = ImmutableMap.of("title", "Autocorrect");
	      return new ModelAndView(var, "main.ftl");
	    }
	  }

	  private static class ResultsHandler implements Route {
	    public Object handle(final Request req, final Response res) {

	      List<String> matchesMain = new ArrayList<String>();
	      List<String> matches = new ArrayList<String>();
	      QueryParamsMap map = req.queryMap();
	      String input = GSON.fromJson(map.value("string"), String.class);
	      input = input.replaceAll("[^a-zA-z ]", " ").replaceAll("\\s+", " ").toLowerCase().trim();

	      String[] inputs = input.split(" ");
	      //if (prefix) {
	        if (trie.prefixMatching(inputs[inputs.length - 1]) != null) {
	          matches = trie.prefixMatching(inputs[inputs.length - 1]);
	        //  System.out.println(matches.get(0));
	          for (String s : matches) {
	            if (!(matchesMain.contains(s))) {
	              matchesMain.add(s);
	            }
	          }
	        }
	      //}
	      // Generates white space matches
	      //if (whitespace) {
	        if (trie.whiteSpaceSplit(inputs[inputs.length - 1]) != null) {
	          matches = trie.whiteSpaceSplit(inputs[inputs.length - 1]);
	          for (String s : matches) {
	            if (!(matchesMain.contains(s))) {
	              matchesMain.add(s);
	            }
	          }
	        }
	      //}


	      Map<String, List<String>> var = new ImmutableMap.Builder().put("matches", matches).build();
	      return GSON.toJson(var);

	    }
	  }











}
