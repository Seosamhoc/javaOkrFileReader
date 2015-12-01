package okrfilereader;

import java.util.ArrayList;

/**
 *
 * @author seosamh
 */
public class Valuemeal {
    int valuemeal_id;
    String third_party_id = "";
    int valuemeal_reference_uid;
    String valuemeal_name;
    int count;
    double amount;
    double savings;
    int mode;
    Product products;
    ArrayList<Product> productsList = new ArrayList<>();
    int discountSize;
    ArrayList<Discount> discountsList = new ArrayList<>();
    Discount discounts;
    Boolean hasDiscount = false;
    int is_overring = 0;
    
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
    public void newProduct()
    {
        products = new Product();
        products.price = 0.00;
        products.amount = 0.00;
        productsList.add(products);
    }
    public String outputJSON(String restaurantNum){
        DBAccess productDB;
        productDB = new DBAccess();
        valuemeal_name = productDB.DBAccess("menu/package", valuemeal_id, "products", restaurantNum);
        third_party_id = productDB.DBAccess("menu/package", valuemeal_id, "bkpnNo", restaurantNum);
        if(third_party_id.equals(""))
            third_party_id = "7592";
        
        if(!(productsList.isEmpty())){
            for (Product productsListItem : productsList) {
                if(productsListItem.mode ==0)
                    amount += productsListItem.amount;
            }
            amount = amount * 100;
            amount = (double)Math.round(amount);
            amount = amount/100;
        }
        savings = 0;
        for(int i=0; i<productsList.size(); i++)
        {
            if(count >0 && productsList.get(i).count > 0 || count <0 && productsList.get(i).count < 0 || is_overring == 1)
                savings += productsList.get(i).count * Double.parseDouble(productDB.DBAccess("menu/package", productsList.get(i).product_id, "price", restaurantNum));
        }
        savings = savings*100/110.35;
        savings = Math.abs(savings) - (Math.abs(amount)*100);
        if (is_overring == 0 && count>0)
            savings = savings * -1;
        else if (is_overring == 0 && count<0)
            mode = 1;
        savings = (double)Math.round(savings);
        savings = savings/100;
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"valuemeal_id\":\"").append(valuemeal_id).append("\",");
        jsonStringBuilder.append("\"third_party_id\":\"").append(third_party_id).append("\",");
        jsonStringBuilder.append("\"valuemeal_name\":\"").append(valuemeal_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"savings\":\"").append(savings).append("\",");
        jsonStringBuilder.append("\"mode\":\"").append(mode).append("\"");
        int j;
        if(!(discountsList.isEmpty())){
            jsonStringBuilder.append(",\"valuemeal_discounts\":[");
            j =0;
            for (Discount discountsListItem : discountsList){
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
        if(!(productsList.isEmpty())){
        jsonStringBuilder.append(",\"valuemeal_products\": [");
        int k = 0;
            for (Product productsListItem : productsList) {
                if (is_overring == 1)
                {
                    productsListItem.count = productsListItem.count * -1;
                    productsListItem.amount = productsListItem.amount * -1;
                    productsListItem.is_overring = 1;
                }
                if (k > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(productsListItem.outputJSON(restaurantNum));
                k++;
            }
            jsonStringBuilder.append("]");
        }
        else{
            //Empty valuemeals are possible right now before data comes from the database where meals are cancelled before a side and a drink are chosen.
            //This should no longer be a problem as orders cancelled that earlier aren't shown, (and shouldn't be).
        }
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
