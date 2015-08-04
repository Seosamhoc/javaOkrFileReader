 package okrfilereader;

/**
 *
 * @author seosamh
 */
public class Product {
    int product_id;
    String third_party_id ="<blank>";
    int product_reference_uid;
    String product_name;
    int count;
    double amount;
    double price;
    double a_la_carte_price;
    int mode;
    Qualifier product_qualifiers;
    Discount product_discounts;
    
    public String outputJSON(Boolean comma){
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
