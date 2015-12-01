/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package okrfilereader;

import java.sql.*;
import java.util.ArrayList;
/**
 *
 * @author seosamh
 */
public class DBCoupon {

public static void main(String[] args) throws ClassNotFoundException
{
  DBCoupon testDB;
  testDB = new DBCoupon();
}

public Boolean DBCoupon(int couponid){
    Connection prodDBconn = null;
    Boolean returnValue = true;
    try {
      Class.forName("org.sqlite.JDBC");
      prodDBconn = DriverManager.getConnection("jdbc:sqlite:10079/ProductDatabase.db");
    } catch (Exception e ) {
      System.err.println( "DBAccess.java line[29]: " + e.getClass().getName() + ": " + e.getMessage()  );
//      System.exit(0);
    }
    
    String queryText;
    try
    {
        Statement statement = prodDBconn.createStatement();
        statement.setQueryTimeout(30);
        queryText = "SELECT multiDiscount FROM couponsOKR WHERE id = " + couponid;
        ResultSet rs = statement.executeQuery(queryText);
        while(rs.next())
        {
          // read the result set
          returnValue = rs.getBoolean("multiDiscount");
        }
    }
    catch(SQLException e){
      // if the error message is "out of memory", 
      // it probably means no database file is found
//      System.err.println("See DBAccess [line 51]");
//      System.err.println(e.getMessage());
//      DBCreate newDB;
//      newDB = new DBCreate();
      try
        {
            Statement statement = prodDBconn.createStatement();
            statement.setQueryTimeout(30);
            queryText = "SELECT multiDiscount FROM couponsOKR WHERE id = " + couponid;
            ResultSet rs = statement.executeQuery(queryText);
            while(rs.next())
            {
              // read the result set
              returnValue = rs.getBoolean("multiDiscount");
            }
        }
        catch(SQLException err)
        {
            System.err.println("See DBAccess [line 69]");
            System.err.println(err.getMessage());
            System.exit(0);
        }
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
        System.err.println("See DBAccess [line 84]");
        System.err.println(e);
        System.exit(0);
      }
    }
    return(returnValue);
}
public ArrayList<Integer> DBProdNum(int couponid){
    ArrayList<Integer> result = new ArrayList<>();
    Connection prodDBconn = null;
    Boolean returnValue = true;
    try {
      Class.forName("org.sqlite.JDBC");
      prodDBconn = DriverManager.getConnection("jdbc:sqlite:10079/ProductDatabase.db");
    } catch (Exception e ) {
      System.err.println( "DBAccess.java: line[99]" + e.getClass().getName() + ": " + e.getMessage()  );
      System.exit(0);
    }
    
    String queryText;
    try
    {
        Statement statement = prodDBconn.createStatement();
        statement.setQueryTimeout(30);
        queryText = "SELECT productid FROM couponProducts WHERE couponid = " + couponid;
        ResultSet rs = statement.executeQuery(queryText);
        while(rs.next())
        {
          // read the result set
          result.add(rs.getInt("productid"));
        }
    }
    catch(SQLException e)
    {
        try
          {
            if(prodDBconn != null)
              prodDBconn.close();
          }
        catch(SQLException err)
        {
          // connection close failed.
          System.err.println("See DBAccess [line 126]");
          System.err.println(err);
          System.exit(0);
        }
    }
    return result;
}
}
