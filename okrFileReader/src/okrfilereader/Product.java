 package okrfilereader;

import java.math.BigDecimal;
 import java.text.*;

/**
 *
 * @author seosamh
 */
public class Product {
    int product_id;
    String third_party_id ="";
    int product_reference_uid;
    String product_name;
    int count;
    double amount;
    double price;
    double centPrice;
    String a_la_carte_price;
    int mode;
    Qualifier product_qualifiers;
    Discount product_discounts;
    
    public String outputJSON(Boolean comma){
        DBAccess productDB;
        productDB = new DBAccess();
        product_name = productDB.DBAccess("menu/package", product_id, "products");
        third_party_id = productDB.DBAccess("menu/package", product_id, "bkpnNo");
//        System.out.println();
//        System.out.println(product_id);
//        System.out.println();
        centPrice = Double.parseDouble(productDB.DBAccess("menu/package", product_id, "price"));
        centPrice = centPrice/100;
        a_la_carte_price = String.valueOf(centPrice);
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"product_id\":\"").append(product_id).append("\",");
        jsonStringBuilder.append("\"third_party_id\":\"").append(third_party_id).append("\",");
        jsonStringBuilder.append("\"product_name\":\"").append(product_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"price\":\"").append(price).append("\",");
        jsonStringBuilder.append("\"a_la_carte_price\":\"").append(a_la_carte_price).append("\",");
        jsonStringBuilder.append("\"mode\":\"").append(mode).append("\"");
        if(comma){
            jsonStringBuilder.append("},");
        }
        else
        {
            jsonStringBuilder.append("}");
        }
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
