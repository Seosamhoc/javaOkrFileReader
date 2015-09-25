package okrfilereader;
import java.io.*;
import java.util.*;
/**
 *
 * @author seosamh
 */
public class OkrFileReader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        OkrFileReader thisProgram;
        thisProgram = new OkrFileReader();
        
    }
    public OkrFileReader(){
        
        //System.out.print(System.getProperty("user.home"));
        //String fileLocation = "C:\\Users\\blue16\\My Documents\\audit000.txt";
        String fileName = "audit000z";
        String fileLocation = System.getProperty("user.home") + "/Desktop/JSON project/" + fileName + ".txt";
        
        File f = new File(fileLocation); 
        if (f.exists()) {
            //System.out.println("File exists");
        } else {
            System.out.print("File does not exist");
        }
        try {
            FileInputStream fStream = new FileInputStream(fileLocation);
            BufferedReader in = new BufferedReader(new InputStreamReader(fStream));
            String line = null;
            String code;
            ArrayList<Transaction> transactions = new ArrayList();
            Transaction trans;
            String orderDate;
            String orderTime;
            String destination;
            int cancelStatus;
            int productNum;
            int deleteStatus;
            int productQuantity;
            int valSize;
            int prodSize;
            int qualifierCount;
            String modifierName;
            int modifierId;
            int qualifierId;
            int discountId;
            int qualSize;
            int modIdPosition;
            double productPrice;
            double discountValue;
            double taxTotal;
            String thirdPartyId;
            Boolean isValuemeal = null;
            String discountName;
            String productName;
            String tenderedAmount;
            String changeAmount = "0.00";
            Double orderSubtotal;
            int lastProductIndex;
            int lastValuemealIndex;
            int transNum = 0;
            int indexee =0;
            while ((line = in.readLine()) != null) {
                if (line.length() >= 42){
                    code = line.substring(40,42);
                    switch(code){
                        case "01":
                            trans = new Transaction();
                            transactions.add(transNum,trans);
                            transNum++;
                            break;
                        case "02":
                            transactions.get(transNum-1).sale_id = Integer.parseInt(line.substring(15,23));
                            break;
//                        10-Completed Order, 11-Cancelled Order, 12-Full Order Void, 13-Partial Order Void 
//                        14-Internal Void, 15-Customer Refund, 16-Flushed Drive-thru Order, 17-Training Order
                        case "10": case "12": case "13": case "14": case "15": case "16": 
                            orderDate = line.substring(43,47) + "-" + line.substring(47,49)+ "-" + line.substring(49,51);
                            orderTime = line.substring(52,54) + ":" + line.substring(54,56) + ":" + line.substring(56,58);
                            transactions.get(transNum-1).transaction_start_datetime = orderDate + " " + orderTime;
                            destination = line.substring(59,60);
                            switch(destination){
                                case "1":
                                    transactions.get(transNum-1).destination = "eat in";
                                    break;
                                case "2":
                                    transactions.get(transNum-1).destination = "take out";
                                    break;
                                case "3": case "4":
                                    transactions.get(transNum-1).destination = "drive through";
                                    break;
                            }
                            transactions.get(transNum-1).order_number = Integer.parseInt(line.substring(14,18).trim());
                            break;
                        case "11": case "17":
                            transactions.get(transNum-1).skipTransaction = true;
                        break;
                        case "20":
                            if (line.charAt(43) == ',')
                            {
                                
                                productQuantity = Integer.parseInt(line.substring(4,8).trim());
                                int productQuantityLength = line.substring(4,8).trim().length();
                                
//                                System.out.println(productQuantity + " : " + productQuantityLength);
                                
                                if(line.charAt(45) == ',')
                                {
                                    productNum = Integer.parseInt(line.substring(44,45));
                                    deleteStatus = Integer.parseInt(line.substring(46,47));
                                    cancelStatus = Integer.parseInt(line.substring(48,49));
                                    if(line.charAt(53 + productQuantityLength) == '0')
                                        isValuemeal = false;
                                    else
                                        isValuemeal = true;
                                }
                                else if(line.charAt(46) == ',')
                                {
                                    productNum = Integer.parseInt(line.substring(44,46));
                                    deleteStatus = Integer.parseInt(line.substring(47,48));
                                    cancelStatus = Integer.parseInt(line.substring(49,50));
                                    if (line.charAt(54 + productQuantityLength) == '0')
                                        isValuemeal = false;
                                    else
                                        isValuemeal = true;
                                }
                                else
                                {
                                    productNum = Integer.parseInt(line.substring(44,47));
                                    deleteStatus = Integer.parseInt(line.substring(48,49));
                                    cancelStatus = Integer.parseInt(line.substring(50,51));
                                    if (line.charAt(55 + productQuantityLength) == '0')
                                        isValuemeal = false;
                                    else
                                        isValuemeal = true;
                                }
                                
                                productName = line.substring(8,24).trim();
                                try
                                {
                                    productPrice = Double.parseDouble(line.substring(25,35).trim());
                                }
                                catch(Exception e)
                                {
                                    productPrice = 0.00;
                                }
                                if (deleteStatus==1 || cancelStatus > 0)
                                {
                                    productQuantity = productQuantity * -1;
                                    productPrice = productPrice * -1;
                                }
                                if (deleteStatus==1) transactions.get(transNum-1).deleted_items = 1;
                                
                                if (isValuemeal)
                                {
                                    valSize = transactions.get(transNum-1).valuemealsList.size()-1;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).newProduct();
                                    prodSize = transactions.get(transNum-1).valuemealsList.get(valSize).productsList.size()-1;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).product_id = productNum;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).mode = deleteStatus;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).count = productQuantity;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).product_name = productName;
                                }
                                else
                                {
                                    transactions.get(transNum-1).newProduct(productNum, deleteStatus, productQuantity, productPrice, productName);
                                }
                            }
                            else if(line.charAt(61) == '0')
                            {
                                productNum = Character.getNumericValue(line.charAt(51));
                                deleteStatus = Character.getNumericValue(line.charAt(53));
                                productPrice = Double.parseDouble(line.substring(25,35).trim());
                                productQuantity = Integer.parseInt(line.substring(4,8).trim());
                                productName = line.substring(8,24).trim();
                                transactions.get(transNum-1).newProduct(productNum, deleteStatus, productQuantity, productPrice, productName);   
                                isValuemeal = false;
                            }
                            else
                                {
                                    valSize = transactions.get(transNum-1).valuemealsList.size()-1;
                                    productNum = Character.getNumericValue(line.charAt(51));
                                    deleteStatus = Character.getNumericValue(line.charAt(53));
                                    productQuantity = Integer.parseInt(line.substring(4,8).trim());
                                    productName = line.substring(8,24).trim();
                                    
                                    transactions.get(transNum-1).valuemealsList.get(valSize).newProduct();
                                    prodSize = transactions.get(transNum-1).valuemealsList.get(valSize).productsList.size()-1;
                                    if(deleteStatus == 2)
                                    {
                                        deleteStatus = 3;
                                    }
                                    if (deleteStatus==1)
                                    {
                                        productQuantity = productQuantity * -1;
                                        transactions.get(transNum-1).deleted_items = 1;
                                    }
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).product_id = productNum;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).mode = deleteStatus;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).count = productQuantity;
                                    transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).product_name = productName;
                                    isValuemeal = true;
                                }
                            break;
                        case "21":
                            productPrice = Double.parseDouble(line.substring(25,35).trim());
                            productQuantity = Integer.parseInt(line.substring(4,8).trim());
                            productName = line.substring(8,24).trim();
                            if (line.charAt(43) == ',')
                            {
                                if(line.charAt(45) == ',')
                                {
                                    productNum = Integer.parseInt(line.substring(44,45));
                                    deleteStatus = Integer.parseInt(line.substring(46,47));
                                    transactions.get(transNum-1).newValuemeal(productNum, deleteStatus, productQuantity, productPrice, productName);
                                }
                                else if(line.charAt(46) == ',')
                                {
                                    productNum = Integer.parseInt(line.substring(44,46));
                                    deleteStatus = Integer.parseInt(line.substring(47,48));
                                    transactions.get(transNum-1).newValuemeal(productNum, deleteStatus, productQuantity, productPrice, productName);
                                    
                                }
                                else
                                {
                                    productNum = Integer.parseInt(line.substring(44,47));
                                    deleteStatus = Integer.parseInt(line.substring(48,49));
                                    transactions.get(transNum-1).newValuemeal(productNum, deleteStatus, productQuantity, productPrice, productName);
                                }
                            }
                            else
                            {
                                productNum = Character.getNumericValue(line.charAt(51));
                                deleteStatus = Character.getNumericValue(line.charAt(53));
                                transactions.get(transNum-1).newValuemeal(productNum, deleteStatus, productQuantity, productPrice, productName);
                                transactions.get(transNum-1).valuemealsList.get(transactions.get(transNum-1).valuemealsList.size()-1).newProduct();
                            }
                            isValuemeal = true;
                            break;
                        case "22":
                            try
                            {
                                qualifierCount = Integer.parseInt(line.substring(5, 9).trim());
                            }
                            catch(Exception e)
                            {
                                qualifierCount = 1;
                            }
                            modifierName = line.substring(9,16).trim();
                            modIdPosition = line.length()-2;
                            modifierId = Character.getNumericValue(line.charAt(modIdPosition));
                            
                            if (line.charAt(45)==',')
                                qualifierId = Character.getNumericValue(line.charAt(44));
                            else if (line.charAt(46)==',')
                                qualifierId = Integer.parseInt(line.substring(44, 45));
                            else
                                qualifierId = Integer.parseInt(line.substring(44, 46));
                            
                            try{
                                productPrice = Double.parseDouble(line.substring(25,35).trim());
                            }
                            catch (Exception e)
                            {
                                productPrice = 0.00;
                            }
                            if (isValuemeal)
                            {
                                valSize = transactions.get(transNum-1).valuemealsList.size()-1;
                                prodSize = transactions.get(transNum-1).valuemealsList.get(valSize).productsList.size()-1;
                                transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).newQualifer(qualifierCount, modifierName, modifierId, qualifierId, productPrice);
                                qualSize = transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).qualifiersList.size()-1;
                                transactions.get(transNum-1).valuemealsList.get(valSize).productsList.get(prodSize).isValuemealQualifier = isValuemeal;
                            }
                            else
                            {
                                prodSize = (transactions.get(transNum-1).productsList.size())-1;
                                transactions.get(transNum-1).productsList.get(prodSize).newQualifer(qualifierCount, modifierName, modifierId, qualifierId, productPrice);
                                qualSize = transactions.get(transNum-1).productsList.get(prodSize).qualifiersList.size()-1;
                                transactions.get(transNum-1).productsList.get(prodSize).isValuemealQualifier = isValuemeal;
                            }
                            break;
                        case "30":
                            deleteStatus = Character.getNumericValue(line.charAt(49));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            discountName = line.substring(8,24).trim();
                            if (line.charAt(45)==',')
                                discountId = Character.getNumericValue(line.charAt(44));
                            else if (line.charAt(46)==',')
                                discountId = Integer.parseInt(line.substring(44,46));
                            else
                                discountId = Integer.parseInt(line.substring(44,47));
                            
                            
                            thirdPartyId = "883398957725"; //Coupon offer savings is applied across one item
                            if (isValuemeal){
                                lastValuemealIndex = transactions.get(transNum-1).valuemealsList.size()-1;
                                lastProductIndex = transactions.get(transNum-1).valuemealsList.get(lastValuemealIndex).productsList.size()-1;
                                transactions.get(transNum-1).valuemealsList.get(lastValuemealIndex).productsList.get(lastProductIndex).newDiscount(discountName, discountId, deleteStatus, discountValue, thirdPartyId);
                            }
                            else
                            {
                                lastProductIndex = transactions.get(transNum-1).productsList.size()-1;
                                transactions.get(transNum-1).productsList.get(lastProductIndex).newDiscount(discountName, discountId, deleteStatus, discountValue, thirdPartyId);
                            }
                            break;
                        case "31":
                            deleteStatus = Character.getNumericValue(line.charAt(45));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            discountName = line.substring(8,24).trim();
                            //Percentage discount below. Used to determine if the order is free or not.
                            if (line.substring(49,52).equals("100") || line.substring(50,53).equals("100"))
                            {
                                thirdPartyId = "92553539";
                            }
                            else{
                                thirdPartyId = "95838532";
                            }
                            transactions.get(transNum-1).newDiscount(discountName, 31, deleteStatus, discountValue, thirdPartyId);
                            break;
                        case "32":
                            deleteStatus = Character.getNumericValue(line.charAt(45));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            //Percentage discount below. Used to determine if the order is free or not.
                            if (line.substring(49,52).equals("100"))
                            {
                                thirdPartyId = "92553539";
                            }
                            else{
                                thirdPartyId = "95838532";
                            }
                            transactions.get(transNum-1).newDiscount("Employee Discount", 32, deleteStatus, discountValue, thirdPartyId);
                            break;
                        case "33":
                            deleteStatus = Character.getNumericValue(line.charAt(45));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            //Percentage discount below. Used to determine if the order is free or not.
                            if (line.substring(47,50).equals("100"))
                            {
                                thirdPartyId = "92553539";
                            }
                            else{
                                thirdPartyId = "95838532";
                            }
                            transactions.get(transNum-1).newDiscount("Manager Discount", 33, deleteStatus, discountValue, thirdPartyId);
                            break;
                        case "34":
                            deleteStatus = Character.getNumericValue(line.charAt(45));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            thirdPartyId = "95838532"; //This assumes that a "cents off" discount will never equal the value of the order.
                            transactions.get(transNum-1).newDiscount("Cents Off", 34, deleteStatus, discountValue, thirdPartyId);
                            break;
                        case "35":
                            deleteStatus = Character.getNumericValue(line.charAt(45));
                            discountValue = Double.parseDouble(line.substring(25,35).trim());
                            discountName = line.substring(8,25).trim();
                            thirdPartyId = "61059522"; //Code for a discount applied to "one menu item" I'm assuming this covers various quantities of one "item"
                            lastProductIndex = transactions.get(transNum-1).productsList.size()-1;
                            transactions.get(transNum-1).productsList.get(lastProductIndex).newDiscount(discountName, 35, deleteStatus, discountValue, thirdPartyId);
                            break;
                        case "40":
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            tenderedAmount = line.substring(24,35).trim();
                            transactions.get(transNum-1).newTender(40, cancelStatus, tenderedAmount);
                            break;
                        case "41":
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            tenderedAmount = line.substring(24,35).trim();
                            transactions.get(transNum-1).newTender(41, cancelStatus, tenderedAmount);
                            break;
                        case "42":
//                          This should have different options for credit card, debit card and 
//                          other but in the example file these aren't being used
                            cancelStatus = Integer.parseInt(line.substring(47,48));
                            tenderedAmount = line.substring(24,35).trim();
                            transactions.get(transNum-1).newTender(42, cancelStatus, tenderedAmount);
                            break;
                        case "45": 
                            changeAmount = line.substring(24,35).trim();
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            if(!(changeAmount.equals("0.00"))){
                                changeAmount = "-" + changeAmount;
                                transactions.get(transNum-1).newTender(45,cancelStatus, changeAmount);
                                transactions.get(transNum-1).tendersList.get(transactions.get(transNum-1).tendersList.size()-1).is_change = "true";
                            }
                            break;
                        case "50":
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            orderSubtotal = (double)Math.round(((Double.parseDouble(line.substring(24,35).trim())))*89.65);
                            
                            taxTotal = (double)Math.round(Double.parseDouble(line.substring(24,35).trim())*100) - orderSubtotal;
                            taxTotal = taxTotal/100;
                            orderSubtotal = orderSubtotal/100;
                            switch(cancelStatus){
//                                0-Normal, 1-Cancel, 2-Full Void, 3-Partial Void, 4-Internal Void,
//                                5-Customer Refund, 6-Flushed DT, 7-Training
                                case 0:
                                    transactions.get(transNum-1).order_sub_total = orderSubtotal;
                                    transactions.get(transNum-1).newTender(51, cancelStatus, String.valueOf(taxTotal));
                                    transactions.get(transNum-1).is_overring = 0;
                                    break;
                                case 1: case 6: case 7:
                                    transactions.get(transNum-1).order_sub_total = 0.00;
                                    transactions.get(transNum-1).is_overring = 0;
                                    transactions.get(transNum-1).deleted_items = 1;//An order is Either an overring or it has deleted items NOT both
                                    break;
                                case 2: case 3: case 4: case 5:
                                    transactions.get(transNum-1).order_sub_total = Double.parseDouble("-" + orderSubtotal) + Double.parseDouble(changeAmount);
                                    transactions.get(transNum-1).is_overring = 1;
                                    transactions.get(transNum-1).deleted_items = 0;//An order is Either an overring or it has deleted items NOT both
                                    break;
                            }
                            break;
                        /*
                            This is a line saying it's the tax but it's only a rough estimate
                            case "51":
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            tax = line.substring(24,35).trim();
                            transactions.get(transNum-1).newTender(51, cancelStatus, tax);
                            switch(cancelStatus){
//                                0-Normal, 1-Cancel, 2-Full Void, 3-Partial Void, 4-Internal Void,
//                                5-Customer Refund, 6-Flushed DT, 7-Training
                                case 0:
                                    transactions.get(transNum-1).order_sub_total = Double.parseDouble("-" + tax);
                                    break;
                                case 1: case 6: case 7:
                                    transactions.get(transNum-1).order_sub_total = 0.00;
                                    break;
                                case 2: case 3: case 4: case 5:
                                    transactions.get(transNum-1).order_sub_total = Double.parseDouble(tax);
                                    break;
                            }
                            break;*/
                        case "52":
                            orderDate = line.substring(43,47) + "-" + line.substring(47,49)+ "-" + line.substring(49,51);
                            orderTime = line.substring(52,54) + ":" + line.substring(54,56) + ":" + line.substring(56,58);
                            transactions.get(transNum-1).date = orderDate;
                            transactions.get(transNum-1).transaction_end_datetime = orderDate + " " + orderTime;
                            break;
                    }
                }
                indexee++;
            }
            long startTime = System.currentTimeMillis();
            StringBuilder jsonStringBuilder = new StringBuilder();
            jsonStringBuilder.append("{");
            jsonStringBuilder.append("\"api_version\":\"2.3\",");
            jsonStringBuilder.append("\"password\":\"password here\",");
            jsonStringBuilder.append("\"method\":\"addTlogs\",");
            jsonStringBuilder.append("\"provider\":\"okr\",");
            jsonStringBuilder.append("\"params\":{");
            jsonStringBuilder.append("\"restaurant_id\":\"7651\",");
            jsonStringBuilder.append("\"tlogs\":[");
            int j = 0;
            for (Transaction transaction : transactions) {
                if (!transaction.skipTransaction) {
                    if (j > 0) jsonStringBuilder.append(',');
                    jsonStringBuilder.append(transaction.outputJSON());
                    j++;
                }
            }
            jsonStringBuilder.append("]" );
            jsonStringBuilder.append("}" );
            jsonStringBuilder.append("}\n");
            String jsonString = jsonStringBuilder.toString();
            long endTime = System.currentTimeMillis();

//            System.out.println("That took " + (endTime - startTime) + " milliseconds");
//            System.out.print(jsonStringBuilder);
            in.close();
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(fileName + ".json"), "utf-8"))) {
                writer.write(jsonString);
            }
            catch (IOException e) {
                System.out.print("File output error");
            }
        } 
        catch (IOException e) {
            System.out.print("File input error");
        }
        
    }
}