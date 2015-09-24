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
    ArrayList<Product> productsList = new ArrayList();
    
    public void newProduct()
    {
        products = new Product();
        products.price = 0.00;
        products.amount = 0.00;
        productsList.add(products);
    }
    public String outputJSON(){
        DBAccess productDB;
        productDB = new DBAccess();
        valuemeal_name = productDB.DBAccess("menu/package", valuemeal_id, "products");
        third_party_id = productDB.DBAccess("menu/package", valuemeal_id, "bkpnNo");
        savings = 0;
        
        for(int i=0; i<productsList.size(); i++)
        {
            savings += Double.parseDouble(productDB.DBAccess("menu/package", productsList.get(i).product_id, "price"));
        }
        savings = (double)Math.round(savings *0.8965);
        savings = savings - Math.abs(amount)*100;
        savings = savings/100;
        
        StringBuilder jsonStringBuilder = new StringBuilder();
        jsonStringBuilder.append("{");
        jsonStringBuilder.append("\"valuemeal_id\":\"").append(valuemeal_id).append("\",");
        jsonStringBuilder.append("\"third_party_id\":\"").append(third_party_id).append("\",");
        jsonStringBuilder.append("\"product_name\":\"").append(valuemeal_name).append("\",");
        jsonStringBuilder.append("\"count\":\"").append(count).append("\",");
        jsonStringBuilder.append("\"amount\":\"").append(amount).append("\",");
        jsonStringBuilder.append("\"savings\":\"").append(savings).append("\",");
        jsonStringBuilder.append("\"mode\":\"").append(mode).append("\",");
        if(!(productsList.isEmpty()))
        {
        jsonStringBuilder.append("\"valuemeal_products\": [");
        int j = 0;
            for (Product productsListItem : productsList) {
                if (j > 0) jsonStringBuilder.append(',');
                jsonStringBuilder.append(productsListItem.outputJSON());
                j++;
            }
            jsonStringBuilder.append("]");
        }
        else
        {
            //Empty valuemeals are possible right now before data comes from the database where meals are cancelled before a side and a drink are chosen.
            //This should no longer be a problem as orders cancelled that earlier aren't shown, (and shouldn't be).
        }
        jsonStringBuilder.append("}");
        String jsonString = jsonStringBuilder.toString();
        return jsonString;
    }
}
