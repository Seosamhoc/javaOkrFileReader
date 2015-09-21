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
    
    public String outputJSON(Boolean comma){
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
        savings = savings - amount*100;
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
            for(int i=0; i<productsList.size()-1; i++)
            {
                jsonStringBuilder.append(productsList.get(i).outputJSON(true));
            }
            jsonStringBuilder.append(productsList.get(productsList.size()-1).outputJSON(false));
            
            jsonStringBuilder.append("]");
        }
        else
        {
            //Empty valuemeals are possible right now before data comes from the database where meals are cancelled before a side and a drink are chosen.
        }
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
