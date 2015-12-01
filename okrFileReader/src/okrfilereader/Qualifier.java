package okrfilereader;

/**
 *
 * @author seosamh
 */
public class Qualifier {
    int modifier_id;
    int modifier_third_party_id;
    int modifier_reference_uid;
    String modifier_name;
    int qualifier_id;
    int qualifier_third_party_id;
    String qualifier_name;
    int count;
    double amount;
    double price;
    double a_la_carte_price;
    int mode;
    
    public String outputJSON(){
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"modifier_id\":\"").append(modifier_id).append("\",");
        jsonStringBuilder.append("\"modifier_third_party_id\":\"").append(modifier_third_party_id).append("\",");
        jsonStringBuilder.append("\"modifier_name\":\"").append(modifier_name).append("\",");
        jsonStringBuilder.append("\"qualifier_id\":\"").append(qualifier_id).append("\",");
        jsonStringBuilder.append("\"qualifier_third_party_id\":\"").append(qualifier_third_party_id).append("\",");
        jsonStringBuilder.append("\"qualifier_name\":\"").append(qualifier_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"price\":\"").append(price).append("\",");
        jsonStringBuilder.append("\"a_la_carte_price\":\"").append(a_la_carte_price).append("\",");
        jsonStringBuilder.append("\"mode\":\"").append(mode).append("\"");
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
