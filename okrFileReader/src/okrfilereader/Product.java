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
    ArrayList<Qualifier> qualifiersList = new ArrayList();
    Boolean isValuemealQualifier;
    int qualifierSize;
    Discount product_discounts;
    
    public void newQualifer(int qualifierCount, String modifierName, int modifierId, int qualifierId, double productPrice){
        productPrice = (double)Math.round(productPrice * 89.65);
        productPrice = productPrice/100;
        qualifiers = new Qualifier();
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
        qualifiers.qualifier_name = qualifierDB.DBAccess("ingredient", qualifierId, "products").trim();
        try{
            qualifiers.qualifier_third_party_id = Integer.parseInt(qualifierDB.DBAccess("ingredient", qualifierId, "bkpnNo"));
        }
        catch(Exception e)
        {
            qualifiers.qualifier_third_party_id = -1;
        }
        qualifiersList.add(qualifiers);
    }
    
    public String outputJSON(){
        DBAccess productDB;
        productDB = new DBAccess();
        product_name = productDB.DBAccess("menu/package", product_id, "products");
        third_party_id = productDB.DBAccess("menu/package", product_id, "bkpnNo");
        centPrice = Double.parseDouble(productDB.DBAccess("menu/package", product_id, "price"));
        centPrice = (double)Math.round(centPrice *0.8965);
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
            int j =0;
            for (Qualifier qualifiersListItem : qualifiersList) {
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
