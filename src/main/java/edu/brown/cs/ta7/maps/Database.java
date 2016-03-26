package edu.brown.cs.rwdodd.maps;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbQuery {


  private Connection conn;
  /**
   * This constructor takes in a path to the db file
   * @param db Path to the db file
   * @throws ClassNotFoundException
   * @throws SQLException incase
   */
  public DbQuery(File db) throws ClassNotFoundException, SQLException {
    //TODO(1): Set up a connection
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + db;

    //TODO(2): Store the connection in a field
    conn = DriverManager.getConnection(urlToDB);
  }

  public List<Road> queryWays(Double latTL, Double longTL, Double latBR, Double longBR) throws SQLException {

    String query = "SELECT n1.latitude AS lat1, n1.longitude AS long1, way.name AS name, n2.latitude AS lat2, n2.longitude AS long2 "
        + "FROM node n1 "
        + "INNER JOIN way ON n1.id=way.start AND n1.latitude<=? AND n1.latitude>=? AND n1.longitude<=? AND n1.longitude>=? "
        + "INNER JOIN node n2 ON n2.id=way.end AND n2.latitude<=? AND n2.latitude>=? AND n2.longitude<=? AND n2.longitude>=?";

    //TODO(2): Create a PreparedStatement
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setDouble(1, latTL);
    prep.setDouble(2, latBR);
    prep.setDouble(3, longTL);
    prep.setDouble(4, longBR);
    prep.setDouble(5, latTL);
    prep.setDouble(6, latBR);
    prep.setDouble(7, longTL);
    prep.setDouble(8, longBR);


    //TODO(3): Execute the query and retrieve a ResultStatement
    ResultSet rs = prep.executeQuery();

    //TODO(4): Add the results to this list
    List<Road> toReturn = new ArrayList<Road>();
    while (rs.next()) {
      Double lat1 = rs.getDouble("lat1");
      Double long1 = rs.getDouble("long1");
      Double lat2 = rs.getDouble("lat2");
      Double long2 = rs.getDouble("long2");
      String name = rs.getString("name");

      Road road = new Road(name, lat1, long1, lat2, long2);
          toReturn.add(road);
        }
    //TODO(5): Close the ResultSet and the PreparedStatement
    rs.close();
    prep.close();
    return toReturn;
  }
}