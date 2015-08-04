package okrfilereader;

/**
 * 
 * @author seosamh
 */
public class Tender {
    int tender_id;
    int third_party_id;
    int tender_reference_uid;
    String tender_name;
    int count;
    double amount;
    String is_change = "false";
    int mode;
    
    public String outputJSON(Boolean comma){
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"tender_id\":\"").append(tender_id).append("\",");
        jsonStringBuilder.append("\"third_party_id\":\"").append(third_party_id).append("\",");
        jsonStringBuilder.append("\"tender_name\":\"").append(tender_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"is_change\":\"").append(is_change).append("\",");
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
