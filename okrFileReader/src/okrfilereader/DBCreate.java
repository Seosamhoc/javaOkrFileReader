/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package okrfilereader;

import java.io.*;
import java.sql.*;

/**
 *
 * @author blue16
 */
public class DBCreate {
    public static void main(String[] args) throws ClassNotFoundException
  {
    // load the sqlite-JDBC driver using the current class loader
    Class.forName("org.sqlite.JDBC");

    Connection connection = null;
    try
    {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:ProductDatabase.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.

      statement.executeUpdate("drop table if exists productsOKR");
      statement.executeUpdate("drop table if exists standardBKC");

      String fileLocation = "/Users/seosamh/Desktop/JSON project/jsonProjDB.sql";
      File f = new File(fileLocation); 
        if (f.exists()) {
            System.out.println("File exists");
        } else {
            System.out.print("File does not exist");
        }
        try {
            FileInputStream fStream = new FileInputStream(fileLocation);
            BufferedReader in = new BufferedReader(new InputStreamReader(fStream));
            String line = null;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                statement.executeUpdate(line);
            }
            in.close();
        } 
        catch (IOException e) {
            System.out.print("File input error");
        }
      ResultSet rs = statement.executeQuery("select * from productsOKR");
      while(rs.next())
      {
        // read the result set
        System.out.println("product = " + rs.getString("products"));
        System.out.println("id = " + rs.getInt("id"));
      }
      ResultSet rs2 = statement.executeQuery("select * from standardBKC");
      while(rs2.next())
      {
        // read the result set
        System.out.println("product = " + rs2.getString("bkpnName"));
        System.out.println("id = " + rs2.getString("bkpnNo"));
      }
    }
    catch(SQLException e)
    {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e);
      }
    }
  }
}
