/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package okrfilereader;

import java.sql.*;
/**
 *
 * @author seosamh
 */
public class DBAccess {
public static void main(String[] args) throws ClassNotFoundException
{
  DBAccess testDB;
  testDB = new DBAccess();
  String output = testDB.DBAccess("menu/package", 100, "products", true);
  System.out.println(output);
}
    
public String DBAccess(String type, int menuNo, String returnColumn){
    Connection prodDBconn = null;
    String returnValue = "";
    try {
      Class.forName("org.sqlite.JDBC");
      prodDBconn = DriverManager.getConnection("jdbc:sqlite:ProductDatabase.db");
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    
    String queryText;
    try
    {
        prodDBconn = DriverManager.getConnection("jdbc:sqlite:ProductDatabase.db");
        Statement statement = prodDBconn.createStatement();
        statement.setQueryTimeout(30);
        queryText = "SELECT " + returnColumn + " FROM productsOKR WHERE type = '" + type + "' AND menuNo = " + menuNo;
        ResultSet rs = statement.executeQuery(queryText);
        while(rs.next())
        {
          // read the result set
          returnValue = rs.getString(returnColumn);
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
        if(prodDBconn != null)
          prodDBconn.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e);
      }
    }
    return(returnValue);
}
public String DBAccess(String type, int menuNo, String returnColumn, Boolean isValuemeal){
    String returnValue = "";
    int itemMenuNo = -1;
    if(!isValuemeal)
    {
        DBAccess productDB;
        productDB = new DBAccess();
        returnValue = productDB.DBAccess(type, menuNo, returnColumn);
    }
    else
    {
        Connection prodDBconn = null;
        try {
          Class.forName("org.sqlite.JDBC");
          prodDBconn = DriverManager.getConnection("jdbc:sqlite:ProductDatabase.db");
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }

        String queryText;
        try
        {
            prodDBconn = DriverManager.getConnection("jdbc:sqlite:ProductDatabase.db");
            Statement statement = prodDBconn.createStatement();
            statement.setQueryTimeout(30);
            queryText = "SELECT menuNo FROM productsOKR WHERE type = '" + type + "' AND products = (SELECT CASE WHEN products LIKE '% Meal (Euro King)%' THEN rtrim(rtrim(products, '(Euro King)'), ' Meal') || ' (Euro King)' WHEN products LIKE '% Meal (Full Price)%' THEN rtrim(rtrim(products, '(Full Price)'), ' Meal') || ' (Full Price)' ELSE rtrim(products, ' Meal') END FROM productsOKR WHERE menuNo = "+ menuNo +" )" ;
            ResultSet rs = statement.executeQuery(queryText);
            while(rs.next())
            {
              // read the result set
              itemMenuNo = Integer.parseInt(rs.getString("menuNo"));
            }
            DBAccess productDB;
            productDB = new DBAccess();
            if (itemMenuNo != -1)
            {
                returnValue = productDB.DBAccess(type, itemMenuNo, returnColumn);
            }
            else
            {
                System.err.println("Query Failed to find itemMenuNo!");
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
            if(prodDBconn != null)
              prodDBconn.close();
          }
          catch(SQLException e)
          {
            // connection close failed.
            System.err.println(e);
          }
        }
    }
    return(returnValue);   
}
}
