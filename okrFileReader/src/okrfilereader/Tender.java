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
        jsonStringBuilder.append("          {\n");
        jsonStringBuilder.append("            \"tender_id\": \"").append(tender_id).append("\",\n");
        jsonStringBuilder.append("            \"third_party_id\": \"").append(third_party_id).append("\",\n");
        jsonStringBuilder.append("            \"tender_name\": \"").append(tender_name).append("\",\n");
        jsonStringBuilder.append("            \"count\": \"").append(count).append("\",\n");
        jsonStringBuilder.append("            \"amount\": \"").append(amount).append("\",\n");
        jsonStringBuilder.append("            \"is_change\": \"").append(is_change).append("\",\n");
        jsonStringBuilder.append("            \"mode\": \"").append(mode).append("\"\n");
        if(comma){
            jsonStringBuilder.append("          },\n");
        }
        else
        {
            jsonStringBuilder.append("          }\n");
        }
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
