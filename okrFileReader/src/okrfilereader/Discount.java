package okrfilereader;

/**
 *
 * @author seosamh
 */
public class Discount {
    int discount_id;
    String third_party_id;
    int discount_reference_uid;
    String discount_name;
    int count;
    double amount;
    int mode;
    
    public String outputJSON(){
        if(mode==1)
        {
            count = count *-1;
            amount = amount *-1;
        }
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"discount_id\":\"").append(discount_id).append("\",");
        jsonStringBuilder.append("\"third_party_id\":\"").append(third_party_id).append("\",");
        jsonStringBuilder.append("\"discount_name\":\"").append(discount_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"mode\":\"").append(mode).append("\"");
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
