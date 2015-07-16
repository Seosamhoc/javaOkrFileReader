package okrfilereader;

import java.util.ArrayList;

/**
 *
 * @author seosamh
 */
public class Transaction {
    int sale_id;
    String date;
    String transaction_start_datetime;
    String transaction_end_datetime;
    String destination;
    Double order_number; //documentation says this is decimal but shouldn't it be int?
    Double order_sub_total;
    int is_overring;
    int deleted_items;
    ArrayList<Product> productsList = new ArrayList();
    Product products;
    Valuemeal valuemeals;
    ArrayList<Tender> tendersList = new ArrayList();
    Tender tenders;
    
    public void newTender(int tenderType, int cancelStatus, String tenderedAmount)
    {
        tenders = new Tender();
        tendersList.add(tenders);
        switch(tenderType)
        {
            case 40:
                tendersList.get(tendersList.size()-1).tender_id = 12345678; //need to get this from OKR
                tendersList.get(tendersList.size()-1).third_party_id = 7765;
                tendersList.get(tendersList.size()-1).tender_name = "CASH";
                break;
            case 41: case 42:
                tendersList.get(tendersList.size()-1).tender_id = 12345678; //need to get this from OKR
                tendersList.get(tendersList.size()-1).third_party_id = 7798;
                tendersList.get(tendersList.size()-1).tender_name = "others";
                break;
        }
        switch(cancelStatus){
//          0-Normal, 1-Cancel, 2-Full Void, 3-Partial Void, 4-Internal Void,
//          5-Customer Refund, 6-Flushed DT, 7-Training
            case 0:
                tendersList.get(tendersList.size()-1).amount = Double.parseDouble(tenderedAmount);
                tendersList.get(tendersList.size()-1).count = 1;
                tendersList.get(tendersList.size()-1).mode = 0;
                break;
            case 1: case 6: case 7:
                tendersList.get(tendersList.size()-1).amount = 0.00;
                tendersList.get(tendersList.size()-1).count = 0;
                tendersList.get(tendersList.size()-1).mode = 1;
                break;
            case 2: case 3: case 4: case 5:
                tendersList.get(tendersList.size()-1).amount = Double.parseDouble("-" + tenderedAmount);
                tendersList.get(tendersList.size()-1).count = -1;
                tendersList.get(tendersList.size()-1).mode = 3;
                break;
        }
        
    }
    
    public String outputJSON(Boolean comma){
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("      {\n");
        jsonStringBuilder.append("        \"sale_id\"" + ": \"").append(sale_id).append("\",\n");
        jsonStringBuilder.append("        \"date\"" + ": \"").append(date).append("\",\n");
        jsonStringBuilder.append("        \"transaction_start_datetime\": \"").append(transaction_start_datetime).append("\",\n");
        jsonStringBuilder.append("        \"transaction_end_datetime\": \"").append(transaction_end_datetime).append("\",\n");
        jsonStringBuilder.append("        \"destination\": \"").append(destination).append("\",\n");
        jsonStringBuilder.append("        \"order_number\": \"").append(order_number).append("\",\n");
        jsonStringBuilder.append("        \"order_sub_total\": \"").append(order_sub_total).append("\",\n"); 
        jsonStringBuilder.append("        \"is_overring\": \"").append(is_overring).append("\",\n");
        jsonStringBuilder.append("        \"deleted_items\": \"").append(deleted_items).append("\",\n");
//        jsonStringBuilder.append("        \"products\": [\n");
        
//        jsonStringBuilder.append("        ],\n");
//        jsonStringBuilder.append("        \"valuemeals\": [\n");
        
//        jsonStringBuilder.append("        ],\n");
        jsonStringBuilder.append("        \"tenders\": [\n");
        for(int i=0; i<tendersList.size()-1; i++){
            jsonStringBuilder.append(tendersList.get(i).outputJSON(true));
        }
        if(tendersList.isEmpty()){
            tenders = new Tender();
            tenders.mode = 1;
            jsonStringBuilder.append(tenders.outputJSON(false));
        }
        else{
            jsonStringBuilder.append(tendersList.get(tendersList.size()-1).outputJSON(false));
        }
        jsonStringBuilder.append("        ]\n");
        if(comma){
            jsonStringBuilder.append("      },");
        }
        else
        {
            jsonStringBuilder.append("      }");
        }
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}