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
        
        String fileLocation = "C:\\Users\\blue16\\My Documents\\audit000.txt";
        //String fileLocation = "/Users/seosamh/Desktop/JSON project/audit000.txt";
        long startTime = System.currentTimeMillis();
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
            String tenderedAmount;
            String changeAmount;
            String orderSubtotal;
            int transNum = 0;
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
                        case "10": case "11": case "12": case "13": case "14": case "15": case "16": case "17":
//                            10-Completed Order, 11-Cancelled Order, 12-Full Order Void, 13-Partial Order Void 
//                            14-Internal Void, 15-Customer Refund, 16-Flushed Drive-thru Order, 17-Training Order
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
                            transactions.get(transNum-1).order_number = Double.parseDouble(line.substring(14,18).trim());
                            break;
                        case "20":
                            if(line.charAt(45) == ',')
                            {
                                productNum = Integer.parseInt(line.substring(44,45));
                            }
                            else if(line.charAt(46) == ',')
                            {
                                productNum = Integer.parseInt(line.substring(44,46));
                            }
                            else
                            {
                                productNum = Integer.parseInt(line.substring(44,47));
                            }
                            transactions.get(transNum-1).newProduct(productNum);
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
                            if(!(changeAmount.equals("0.00"))){
                                transactions.get(transNum-1).tendersList.get(transactions.get(transNum-1).tendersList.size()-1).is_change = "true";
                            }
                            break;
                        case "50":
                            cancelStatus = Integer.parseInt(line.substring(43,44));
                            orderSubtotal = line.substring(24,35).trim();
                            switch(cancelStatus){
//                                0-Normal, 1-Cancel, 2-Full Void, 3-Partial Void, 4-Internal Void,
//                                5-Customer Refund, 6-Flushed DT, 7-Training
                                case 0:
                                    transactions.get(transNum-1).order_sub_total = Double.parseDouble(orderSubtotal);
                                    transactions.get(transNum-1).is_overring = 0;
                                    transactions.get(transNum-1).deleted_items = 0;
                                    break;
                                case 1: case 6: case 7:
                                    transactions.get(transNum-1).order_sub_total = 0.00;
                                    transactions.get(transNum-1).is_overring = 2;
                                    transactions.get(transNum-1).deleted_items = 1;
                                    break;
                                case 2: case 3: case 4: case 5:
                                    transactions.get(transNum-1).order_sub_total = Double.parseDouble("-" + orderSubtotal);
                                    transactions.get(transNum-1).is_overring = 1;
                                    transactions.get(transNum-1).deleted_items = 1;
                                    break;
                            }
                            break;
                        case "52":
                            orderDate = line.substring(43,47) + "-" + line.substring(47,49)+ "-" + line.substring(49,51);
                            orderTime = line.substring(52,54) + ":" + line.substring(54,56) + ":" + line.substring(56,58);
                            transactions.get(transNum-1).date = orderDate;
                            transactions.get(transNum-1).transaction_end_datetime = orderDate + " " + orderTime;
                            break;
                    }
                }
            }
            System.out.print("{");
            System.out.print("\"api_version\":\"2.3\",");
            System.out.print("\"password\":\"password here\",");
            System.out.print("\"method\":\"addTlogs\",");
            System.out.print("\"provider\":\"okr\",");
            System.out.print("\"params\":{");
            System.out.print("\"restaurant_id\":\"7651\",");
            System.out.print("\"tlogs\":[");
            for(int i=0;i<transactions.size()-1; i++)
            {
                System.out.print(transactions.get(i).outputJSON(true));
            }
            System.out.print(transactions.get(transactions.size()-1).outputJSON(false));
            System.out.print("]" );
            System.out.print("}" );
            System.out.print("}\n");
            in.close();
        } 
        catch (IOException e) {
            System.out.print("File input error");
        }
        //System.out.print(System.getProperty("user.home"));
        long endTime = System.currentTimeMillis();

        //System.out.print("That took " + (endTime - startTime) + " milliseconds");
    }
}