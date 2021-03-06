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
    int order_number; 
    Double order_sub_total;
    int is_overring;
    int deleted_items;
    ArrayList<Discount> discountsList = new ArrayList<>();
    Discount discounts;
    ArrayList<Product> productsList = new ArrayList<>();
    Product products;
    ArrayList<Valuemeal> valuemealsList = new ArrayList<>();
    Valuemeal valuemeals;
    ArrayList<Tender> tendersList = new ArrayList<>();
    Tender tenders;
    int lastTenderIndex;
    int discountSize;
    boolean skipTransaction = false;
    
    public void newDiscount(String discountName, int discountNum, int deleteStatus, double discountValue, String thirdPartyId)
    {
        if(deleteStatus>0)
            deleteStatus = 1;
        DBCoupon couponDB;
        couponDB = new DBCoupon();
        Boolean multiDiscount = couponDB.DBCoupon(discountNum);
        if(multiDiscount){
            discountValue = (double)Math.round(discountValue *10000/110.35);
            discountValue = discountValue/100;
            discounts = new Discount();
            discountsList.add(discounts);
            discountSize = discountsList.size()-1;
            discountsList.get(discountSize).discount_id = discountNum;
            discountsList.get(discountSize).discount_name = discountName;
            discountsList.get(discountSize).amount = discountValue;
            discountsList.get(discountSize).count = 1;
            discountsList.get(discountSize).mode = deleteStatus;
            discountsList.get(discountSize).third_party_id = thirdPartyId;
            
        }
        else{
            ArrayList<Integer> DBProdNum = couponDB.DBProdNum(discountNum);
            productloop:
            {
                for (Product productsListItem : productsList) {
                    for(Integer couponProductId : DBProdNum){
                        if(productsListItem.product_id == couponProductId && !productsListItem.hasDiscount){
                            productsListItem.newDiscount(discountName, discountNum, deleteStatus, discountValue, thirdPartyId);
                            if (deleteStatus == 0)
                                productsListItem.hasDiscount = true;
                            break productloop;
                        }
                    }
                }
                discountValue = (double)Math.round(discountValue *10000/110.35);
                discountValue = discountValue/100;
                discounts = new Discount();
                discountsList.add(discounts);
                discountSize = discountsList.size()-1;
                discountsList.get(discountSize).discount_id = discountNum;
                discountsList.get(discountSize).discount_name = discountName;
                discountsList.get(discountSize).amount = discountValue;
                discountsList.get(discountSize).count = 1;
                discountsList.get(discountSize).mode = deleteStatus;
                discountsList.get(discountSize).third_party_id = thirdPartyId;
            }
            valuemealloop:
            for (Valuemeal valuemealsListItem : valuemealsList) {
                for(Integer couponProductId : DBProdNum){
                    if(valuemealsListItem.valuemeal_id == couponProductId && !valuemealsListItem.hasDiscount){
                        valuemealsListItem.newDiscount(discountName, discountNum, deleteStatus, discountValue, thirdPartyId);
                        if (deleteStatus == 0)
                            valuemealsListItem.hasDiscount = true;
                        break valuemealloop;
                    }
                }
            }
        }
    }
    
    public void newProduct(int productNum, int deleteStatus, int productQuantity, double productPrice, String productName)
    {
        productPrice = (double)Math.round(productPrice *10000/110.35);
        productPrice = productPrice/100;
        products = new Product();
        productsList.add(products);
        productsList.get(productsList.size()-1).product_id = productNum;
        productsList.get(productsList.size()-1).mode = deleteStatus;
        productsList.get(productsList.size()-1).count = productQuantity;
        productsList.get(productsList.size()-1).amount = productPrice;
        productsList.get(productsList.size()-1).price = productPrice/productQuantity;
        productsList.get(productsList.size()-1).product_name = productName;
    }
    
    public void newTender(int tenderType, int cancelStatus, String tenderedAmount)
    {
        tenders = new Tender();
        tendersList.add(tenders);
        lastTenderIndex = tendersList.size()-1;
        switch(tenderType)
        {   case 45:
                tendersList.get(lastTenderIndex).is_change = "true"; 
                //no break here on purpose, 45 is otherwise the same as 40
            case 40: 
                tendersList.get(lastTenderIndex).tender_id = 12345678; //need to get this from OKR
                tendersList.get(lastTenderIndex).third_party_id = 7765;
                tendersList.get(lastTenderIndex).tender_name = "CASH";
                break;
            case 41: case 42:
                tendersList.get(lastTenderIndex).tender_id = 12345678; //need to get this from OKR
                tendersList.get(lastTenderIndex).third_party_id = 7798;
                tendersList.get(lastTenderIndex).tender_name = "others";
                break;
            case 51:
                tendersList.get(lastTenderIndex).tender_id = 12345678; //need to get this from OKR
                tendersList.get(lastTenderIndex).third_party_id = 7751;
                tendersList.get(lastTenderIndex).tender_name = "TAX";
                break;
        }
        switch(cancelStatus){
//          0-Normal, 1-Cancel, 2-Full Void, 3-Partial Void, 4-Internal Void,
//          5-Customer Refund, 6-Flushed DT, 7-Training
            case 0:
                tendersList.get(lastTenderIndex).amount = Double.parseDouble(tenderedAmount);
                tendersList.get(lastTenderIndex).mode = 0;
                break;
            case 1: case 6: case 7:
                tendersList.get(lastTenderIndex).amount = 0.00;
                tendersList.get(lastTenderIndex).mode = 1;
                break;
            case 2: case 3: case 4: case 5:
                if( tenderedAmount.charAt(0) != '-' && tenderType != 45)
                    tendersList.get(lastTenderIndex).amount = Double.parseDouble("-" + tenderedAmount);
                else if(tenderType == 45)
                    tendersList.get(lastTenderIndex).amount = Double.parseDouble(tenderedAmount) * -1;
                tendersList.get(lastTenderIndex).mode = 3;
                break;
        }
        tendersList.get(lastTenderIndex).count = 1;
    }
    
    public void newValuemeal(int productNum, int deleteStatus, int productQuantity, double productPrice, String productName, String restaurantNum)
    {
        productPrice = (double)Math.round(productPrice *10000/110.35);
        productPrice = productPrice/100;
        valuemeals = new Valuemeal();
        valuemealsList.add(valuemeals);
        if (deleteStatus==1)
        {
            productQuantity = productQuantity * -1;
            productPrice = productPrice * -1;
        }
        int valSize = valuemealsList.size()-1;
        valuemealsList.get(valSize).valuemeal_id = productNum;
        valuemealsList.get(valSize).valuemeal_name = productName;
        valuemealsList.get(valSize).count = productQuantity;
        valuemealsList.get(valSize).mode = deleteStatus;
        valuemealsList.get(valSize).amount = productPrice;
        
        DBAccess productDB;
        productDB = new DBAccess();
        int mainProdNum;
        String mainProdName;
        valuemealsList.get(valSize).newProduct();
        int prodSize = valuemealsList.get(valSize).productsList.size()-1;
        valuemealsList.get(valSize).productsList.get(prodSize).mode = deleteStatus;
        valuemealsList.get(valSize).productsList.get(prodSize).count = productQuantity;
        try{
            //kid's chicken nugget meal (productNum = 179) has 4 chicken nuggets in it, which doesn't follow the standard layout
            if (productNum == 179)
            {
                mainProdNum = 36;
            }
            else if (productNum == 409) //there's a euroking deal for this valuemeal but not the item on it's own.
            {
                mainProdNum = 413;
            }
            else
            {
                mainProdNum = Integer.parseInt(productDB.DBAccess("menu/package", productNum, "menuNo", restaurantNum, true));
            }
            mainProdName = productDB.DBAccess("menu/package", mainProdNum, "products", restaurantNum);
            valuemealsList.get(valSize).productsList.get(prodSize).product_id = mainProdNum;
            valuemealsList.get(valSize).productsList.get(prodSize).product_name = mainProdName;
        }
        catch(NumberFormatException e){
            System.out.println();
            System.out.println("Product Number: " + productNum);
            System.out.println("Product Name: " + productName);
            System.out.println();
        }
    }
    
    public String outputJSON(String restaurantNum){
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"sale_id\"" + ":\"").append(sale_id).append("\",");
        jsonStringBuilder.append("\"date\"" + ":\"").append(date).append("\",");
        jsonStringBuilder.append("\"transaction_start_datetime\":\"").append(transaction_start_datetime).append("\",");
        jsonStringBuilder.append("\"transaction_end_datetime\":\"").append(transaction_end_datetime).append("\",");
        jsonStringBuilder.append("\"destination\":\"").append(destination).append("\",");
        jsonStringBuilder.append("\"order_number\":\"").append(order_number).append("\",");
        jsonStringBuilder.append("\"order_sub_total\":\"").append((double)Math.round(order_sub_total*100)/100).append("\","); 
        jsonStringBuilder.append("\"is_overring\":\"").append(is_overring).append("\",");
        jsonStringBuilder.append("\"deleted_items\":\"").append(deleted_items).append("\",");
        int j = 0;
        if(!(productsList.isEmpty())){
            jsonStringBuilder.append("\"products\": [");
            for (Product productsListItem : productsList) {
                if (is_overring == 1)
                {
                    productsListItem.count = productsListItem.count * -1;
                    productsListItem.amount = productsListItem.amount * -1;
                    productsListItem.is_overring = 1;
                }
                if (j > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(productsListItem.outputJSON(restaurantNum));
                j++;
            }
            if(!(discountsList.isEmpty()) || !(valuemealsList.isEmpty()) || !(tendersList.isEmpty())){
                jsonStringBuilder.append("],");
            }
            else{
                jsonStringBuilder.append("]");
            }
        }
        j = 0;
        if(!(valuemealsList.isEmpty())){
            jsonStringBuilder.append("\"valuemeals\":[");
            for (Valuemeal valuemealsListItem : valuemealsList) {
                if (j > 0) jsonStringBuilder.append(',');
                if (is_overring == 1)
                {
                    valuemealsListItem.count = valuemealsListItem.count * -1;
                    valuemealsListItem.amount = valuemealsListItem.amount * -1;
                    valuemealsListItem.is_overring = 1;
                }
                jsonStringBuilder.append(valuemealsListItem.outputJSON(restaurantNum));
                j++;
            }
            if(!(discountsList.isEmpty()) || !(tendersList.isEmpty())){
                jsonStringBuilder.append("],");
            }
            else{
                jsonStringBuilder.append("]");
            }
        }
        j = 0;
        if(!(discountsList.isEmpty())){
            jsonStringBuilder.append("\"order_discounts\":[");
        
            for (Discount discountsListItem : discountsList) {
                if (j > 0) jsonStringBuilder.append(',');
                if (is_overring == 1)
                {
                    discountsListItem.count = discountsListItem.count * -1;
                    discountsListItem.amount = discountsListItem.amount * -1;
                }
                jsonStringBuilder.append(discountsListItem.outputJSON());
                j++;
            }
            if(!(tendersList.isEmpty())){
                jsonStringBuilder.append("],");
            }
            else{
                jsonStringBuilder.append("]");
            }
        }
        j = 0;
        if(!(tendersList.isEmpty())){
            jsonStringBuilder.append("\"tenders\":[");
        
            for (Tender tendersListItem : tendersList) {
                if (j > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(tendersListItem.outputJSON());
                j++;
            }
        
            jsonStringBuilder.append("]");
        }
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
