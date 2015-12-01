 package okrfilereader;


import java.util.ArrayList;

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
    Qualifier qualifiers;
    ArrayList<Qualifier> qualifiersList = new ArrayList<>();
    Boolean isValuemealQualifier;
    int qualifierSize;
    int discountSize;
    ArrayList<Discount> discountsList = new ArrayList<>();
    Discount discounts;
    Boolean hasDiscount = false;
    int is_overring =0;
    
    public void newDiscount(String discountName, int discountNum, int deleteStatus, double discountValue, String thirdPartyId)
    {
        discountValue = (double)Math.round(discountValue*10000/110.35);//price in rounded to nearest cent and tax taken from it.
        discountValue = discountValue/100;//price back to euro
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
    
    public void newQualifer(int qualifierCount, String modifierName, int modifierId, int qualifierId, double productPrice, int deleteStatus, String restaurantNum){
        productPrice = (double)Math.round(productPrice*100/1.1035);//price in rounded to nearest cent and tax taken from it.
        productPrice = productPrice/100;//price back to euro
        qualifiers = new Qualifier();
        qualifiers.mode = deleteStatus;
        qualifiers.count = qualifierCount;
        qualifiers.modifier_name = modifierName;
        qualifiers.modifier_id = modifierId;
        if (modifierId == 1)
        {
            qualifiers.modifier_third_party_id = 7714;
        }
        else if(modifierId == 2)
        {
            qualifiers.modifier_third_party_id = 7715;
        }
        qualifiers.qualifier_id = qualifierId;
        qualifiers.amount = productPrice;
        qualifiers.price = productPrice/qualifierCount;
        DBAccess qualifierDB;
        qualifierDB = new DBAccess();
        qualifiers.a_la_carte_price = qualifiers.price;
        qualifiers.qualifier_name = qualifierDB.DBAccess("ingredient", qualifierId, "products", restaurantNum).trim();
        try{
            qualifiers.qualifier_third_party_id = Integer.parseInt(qualifierDB.DBAccess("ingredient", qualifierId, "bkpnNo", restaurantNum));
        }
        catch(Exception e)
        {
            qualifiers.qualifier_third_party_id = 7414;
        }
        qualifiersList.add(qualifiers);
    }
    
    public String outputJSON(String restaurantNum){
        DBAccess productDB;
        productDB = new DBAccess();
        product_name = productDB.DBAccess("menu/package", product_id, "products", restaurantNum);
        third_party_id = productDB.DBAccess("menu/package", product_id, "bkpnNo", restaurantNum);
        if(third_party_id.equals("")){
            third_party_id = "7414"; //this is the code for food items. 
            //Non-food items will need to be in the database
        }
        try{
            centPrice = Double.parseDouble(productDB.DBAccess("menu/package", product_id, "price", restaurantNum));
        }
        catch(Exception e)
        {
            System.out.println("Price not found for " + product_id);
        }
        centPrice = (double)Math.round(centPrice*100/110.35);
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
        int j;
        if(!(discountsList.isEmpty()))
        {
            jsonStringBuilder.append(",\"product_discounts\":[");
            j =0;
            for (Discount discountsListItem : discountsList)
            {
                if (is_overring == 1)
                {
                    discountsListItem.count = discountsListItem.count * -1;
                    discountsListItem.amount = discountsListItem.amount * -1;
                }
                if (j > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(discountsListItem.outputJSON());
                j++;
            }
            jsonStringBuilder.append("]");
        }
        if( !(qualifiersList.isEmpty()))
        {
            if (isValuemealQualifier)
            {
                jsonStringBuilder.append(",\"valuemeal_product_qualifiers\":[");
            }
            else
            {
                jsonStringBuilder.append(",\"product_qualifiers\":[");
            }
            j =0;
            for (Qualifier qualifiersListItem : qualifiersList) {
                if (is_overring == 1)
                {
                    qualifiersListItem.count = qualifiersListItem.count * -1;
                    qualifiersListItem.amount = qualifiersListItem.amount * -1;
                }
                if (j > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(qualifiersListItem.outputJSON());
                j++;
            }
            jsonStringBuilder.append("]");
        }
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
