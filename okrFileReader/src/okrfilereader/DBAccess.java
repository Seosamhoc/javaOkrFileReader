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
  String output = testDB.DBAccess("menu/package", 100, "products", "10079", true);
  System.out.println(output);
}
    
public String DBAccess(String type, int menuNo, String returnColumn, String restaurantNum){
    Connection prodDBconn = null;
    String returnValue = "";
    try {
      Class.forName("org.sqlite.JDBC");
      prodDBconn = DriverManager.getConnection("jdbc:sqlite:".concat(restaurantNum).concat("/ProductDatabase.db"));
    } catch ( Exception e ) {
      System.err.println( "DBAccess.java ["+ new Exception().getStackTrace()[0].getLineNumber() +"]: " + e.getClass().getName() + ": " + e.getMessage()  );
      System.exit(0);
    }
    
    String queryText;
    try
    {
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
      try
        {
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
        catch(SQLException err)
        {
            System.err.println("See DBAccess ["+ new Exception().getStackTrace()[0].getLineNumber() +"]");
            System.err.println(err.getMessage());
//            System.exit(0);
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
        System.err.println("See DBAccess ["+ new Exception().getStackTrace()[0].getLineNumber() +"]");
        System.err.println(e);
        System.exit(0);
      }
    }
    return(returnValue);
}
public String DBAccess(String type, int menuNo, String returnColumn, String restaurantNum, Boolean isValuemeal){
    String returnValue = "";
    int itemMenuNo = -1;
    if(!isValuemeal)
    {
        DBAccess productDB;
        productDB = new DBAccess();
        returnValue = productDB.DBAccess(type, menuNo, returnColumn, restaurantNum);
    }
    else
    {
        Connection prodDBconn = null;
        try {
          Class.forName("org.sqlite.JDBC");
          prodDBconn = DriverManager.getConnection("jdbc:sqlite:".concat(restaurantNum).concat("/ProductDatabase.db"));
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + "["+ new Exception().getStackTrace()[0].getLineNumber() +"]: " + e.getMessage() );
          System.exit(0);
        }

        String queryText;
        try
        {
            Statement statement = prodDBconn.createStatement();
            statement.setQueryTimeout(30);
            queryText = "SELECT * FROM productsOKR WHERE type = 'menu/package' AND products LIKE ("; 
            queryText += "SELECT CASE "; 
            queryText += "WHEN products LIKE 'Large % Meal (Euro King)%' THEN substr(products, 7, length(products)-23) || ' (Euro King)'"; 
            queryText += "WHEN products LIKE 'Large % Meal (Full Price)%' THEN substr(products, 7, length(products)-24) || ' (Full Price)'"; 
            queryText += "WHEN products LIKE 'Large % Meal' THEN substr(products, 7, length(products)-11)"; 
            queryText += "WHEN products LIKE '% Meal (Euro King)%' THEN substr(products, 1, length(products)-17) || ' (Euro King)' "; 
            queryText += "WHEN products LIKE '% Meal (Full Price)%' THEN substr(products, 1, length(products)-18) || ' (Full Price)'"; 
            queryText += "WHEN products LIKE '% Meal' THEN substr(products, 1, length(products)-5) ELSE products END "; 
            queryText += "FROM productsOKR WHERE menuNo = ";
            queryText += menuNo;
            queryText += ")";
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
                returnValue = productDB.DBAccess(type, itemMenuNo, returnColumn, restaurantNum);
            }
            else
            {
                System.err.println("Query Failed to find itemMenuNo! " + itemMenuNo + " " + menuNo);
            }
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory", 
          // it probably means no database file is found
          System.err.println("DBAccess.java: ["+ new Exception().getStackTrace()[0].getLineNumber() +"] " + e.getMessage());
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
            System.err.println("DBAccess.java: ["+ new Exception().getStackTrace()[0].getLineNumber() +"] " + e);
          }
        }
    }
    return(returnValue);   
}
public String UpdatePrice(int productId, int filePrice, String restaurantNum){
    Connection prodDBconn = null;
    int updates;
    try {
        Class.forName("org.sqlite.JDBC");
        prodDBconn = DriverManager.getConnection("jdbc:sqlite:".concat(restaurantNum).concat("/ProductDatabase.db"));
        Statement statement = prodDBconn.createStatement();
        statement.setQueryTimeout(30);
        String queryText = "UPDATE productsOKR SET price =" + filePrice + ", lastUpdated = date('now') WHERE type = 'menu/package' AND menuNo =" + productId;
        updates = statement.executeUpdate(queryText);
        return "Sucessful updates: " + updates;
    } catch ( Exception e ) {
        System.err.println( "DBAccess.java ["+ new Exception().getStackTrace()[0].getLineNumber() +"]: " + e.getClass().getName() + ": " + e.getMessage()  );
        System.exit(0);
    }
    return "Price failed to update";
}
public String addProduct(int productId, int filePrice, String fileProductName, String restaurantNum){
    fileProductName = fileProductName.replace("'", "''");
    String queryText = "INSERT INTO productsOKR (type, menuNo, products, price, bkpnNo, lastUpdated) VALUES ('menu/package', " + productId + ", '"+ fileProductName +"', " + filePrice + ", '', date('now'))";
    Connection prodDBconn = null;
    int inserts;
    try{
        Class.forName("org.sqlite.JDBC");
        prodDBconn = DriverManager.getConnection("jdbc:sqlite:".concat(restaurantNum).concat("/ProductDatabase.db"));
        Statement statement = prodDBconn.createStatement();
        statement.setQueryTimeout(30);
        inserts = statement.executeUpdate(queryText);
        return "Successful inserts: " + inserts;
    }
    catch ( Exception e ) {
        System.err.println( "DBAccess.java ["+ new Exception().getStackTrace()[0].getLineNumber() +"]: " + e.getClass().getName() + ": " + e.getMessage()  );
        System.exit(0);
    }
    return "failed to add product";
}
}
