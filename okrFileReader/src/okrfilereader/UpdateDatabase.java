package okrfilereader;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author seosamh
 */
public class UpdateDatabase {
    public UpdateDatabase(String fileLocation, String restaurantNum){
        SimpleDateFormat folderNameFormat = new SimpleDateFormat("ddMMYYYY");
        String dateFolder = folderNameFormat.format(System.currentTimeMillis()-86400000);
        String filePath = new File("").getAbsolutePath();
        String fileName = "WORKS113.CDF";
        fileLocation = fileLocation.concat(fileName);
//        String fileLocation = System.getProperty("user.home") + "/Desktop/JSON project/WORKS113.CDF";
        StringBuilder results;
        File f = new File(fileLocation);
        if (!f.exists()) {
            System.out.print("File does not exist");
        }
        try {
            FileInputStream fStream = new FileInputStream(fileLocation);
            BufferedReader in = new BufferedReader(new InputStreamReader(fStream));
            String line = null;
            int productId;
            int filePrice;
            int dbPrice;
            DBAccess checkDB;
            checkDB = new DBAccess();
            String fileProductName;
            while ((line = in.readLine()) != null) {
                productId = Integer.parseInt(line.substring(20, 23));
                fileProductName = line.substring(24,39).trim();
                filePrice = Integer.parseInt(line.substring(40, 47));
                try{
                    dbPrice = Integer.parseInt(checkDB.DBAccess("menu/package", productId, "price", restaurantNum));
                    if (filePrice != dbPrice){
                        System.out.println("Price Change: ");
                        System.out.print(fileProductName);
                        System.out.println(checkDB.UpdatePrice(productId, filePrice, restaurantNum));
                    }
                }
                catch(Exception e){
                    System.out.println("Product: " + fileProductName + " was not found in database. Attempting insert:");
                    System.out.println(checkDB.addProduct(productId, filePrice, fileProductName, restaurantNum));
                }
                
            }
            in.close();
        }
        catch(Exception e){
            
        }
    }
}
