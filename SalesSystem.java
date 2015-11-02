import java.util.Scanner;
import java.io.*;
import java.sql.*;

public class SalesSystem {
    public static Scanner s = new Scanner(System.in);
    //Load JDBC Driver
    public static Connection conn;
    public static Statement stmt;

    public static int mainMenu(){
        System.out.println("\n-----Main menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
        return s.nextInt();
    }

    public static int adminMenu(){
        System.out.println("\n-----Operations for administrator menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delete all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show number of records in each table");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        return s.nextInt();
    }

    public static void createTable(){
        //use auto_increment for ID's
        //use default getdate() for dates
    }

    public static void deleteTable(){
    }

    public static void loadFromDatafile(){
        System.out.print("\nType in the Source Data Folder Path: ");
        String path = s.next();

        String[] tables = { "category", "manufacturer", "part", "salesperson", "transaction" };
        File[] file = new File[5];
        int i, j;
        for (i = 0; i < 5; i++)
            file[i] = new File(path + tables[i] + ".txt");
        String[] statements = {
            "INSERT INTO category VALUES (?,?)",
            "INSERT INTO manufacturer VALUES (?,?,?,?,?)",
            "INSERT INTO part VALUES (?,?,?,?,?,?)",
            "INSERT INTO salesperson VALUES (?,?,?,?,?)",
            "INSERT INTO transaction VALUES (?,?,?,?)"
            };
        BufferedReader dataBR;
        Scanner dataS;
        String tuple;
        PreparedStatement pstmt;

        try {
            for (i = 0; i < 5; i++){
                pstmt = conn.prepareStatement(statements[i]);
                dataBR = new BufferedReader(new FileReader(file[i]));
                while ((tuple = dataBR.readLine()) != null){
                    dataS = new Scanner(tuple).useDelimiter("\t");
                    j = 1;
                    while (dataS.hasNext())
                        pstmt.setString(j++, dataS.next());
                    pstmt.executeUpdate();
                    dataS.close();
                }
            }
        } catch (Exception e){
        };

        System.out.println("Processing...Done! Data is inputted to the database!");
    }

    public static void showRecordsNum(){
    }

    public static int salesMenu(){
        System.out.println("\n-----Operations for salesperson menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Search for parts");
        System.out.println("2. Sell a part");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        return s.nextInt();
    }

    public static void searchParts(){
    }

    public static void sellPart(){
        System.out.print("Enter the Part ID: ");
        int partID = s.nextInt();
        System.out.print("Enter the Salesperson ID: ");
        int saleID = s.nextInt();
        String partName;
        int partQuan;
        try {
            ResultSet partRS = stmt.executeQuery("SELECT pName, pAvailableQuentity FROM part WHERE pID = " + partID);
            partName = partRS.getString(1);
            partQuan = partRS.getInt(2);
            PreparedStatement pstmtP = conn.prepareStatement("UPDATE part SET pAvailableQuentity = pAvailable - 1 WHERE pID = " + partID);
            PreparedStatement pstmtT = conn.prepareStatement("INSERT INTO transaction (pID, sID) VALUES (?,?)");
            if (partQuan > 0){
                pstmtT.setInt(1, partID);
                pstmtT.setInt(2, saleID);
                pstmtP.executeUpdate();
                pstmtT.executeUpdate();
            } else {
                System.out.println("Requested part not available. Transaction failed.");
                return;
            }
        } catch (Exception e){
            System.out.println("Unknown error occured. Transaction failed.");
            return;
        };
        System.out.println("Product: " + partName + "(id: " + partID + ") Remaining Quantity: " + --partQuan);
    }

    public static int managerMenu(){
        System.out.println("\n-----Operations for manager menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Show the sales record of a salesperson within a period");
        System.out.println("2. Show the total sales value of each manufacturer");
        System.out.println("3. Show the N most popular part");
        System.out.println("4. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        return s.nextInt();
    }

    public static void showSalespersonRecord(){
    }

    public static void showManuSalesValue(){
        System.out.println("| Manufacturer ID | Manufacturer Name | Total Sales Value |");
        try {
            ResultSet manuRS = stmt.executeQuery(
                "SELECT M.mid, M.mName, SUM(ProductSales.pSales) AS mSales " +
                "FROM manufacturer M " +
                "RIGHT JOIN " +
                    "(SELECT P.mid, P.pid, (P.pPrice * TranPerP.pTranNum) AS pSales " +
                    "FROM Product P " +
                    "INNER JOIN " +
                        "(SELECT pid, COUNT(*) AS pTranNum FROM transactionGROUP BY pid) TranPerP " +
                    "ON P.pid = TranPerP.pid" +
                    ") ProductSales " +
                "ON M.mid = P.mid " +
                "GROUP BY M.mid " +
                "ORDER BY M.mSales DESC");

            while (manuRS.next())
                System.out.println("| " + manuRS.getInt("M..mid") + " | " + manuRS.getString("M.mName") + " | " + manuRS.getInt("mSales") + " |");
        } catch (Exception e){
        };
        System.out.println("End of Query");
    }

    public static void showPopularParts(){
    }

    public static void adminSystem(){
        int choice = 1;
        while(choice != 5){
            choice = adminMenu();
            switch(choice) {
                case 1: createTable();
                        break;
                case 2: deleteTable();
                        break;
                case 3: loadFromDatafile();
                        break;
                case 4: showRecordsNum();
                        break;
                case 5: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    public static void salesSystem(){
        int choice = 1;
        while(choice != 3){
            choice = salesMenu();
                switch(choice) {
                case 1: searchParts();
                        break;
                case 2: sellPart();
                        break;
                case 3: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    public static void managerSystem(){
        int choice = 1;
        while(choice != 4){
            choice = managerMenu();
            switch(choice) {
                case 1: showSalespersonRecord();
                        break;
                case 2: showManuSalesValue();
                        break;
                case 3: showPopularParts();
                        break;
                case 4: return;
                default: System.out.println("Invalid choice");
            }
        }
    }

    public static void main(String[] args){
        try{
            Class.forName(" oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
                "d075", "ieytlflx");
            stmt = conn.createStatement();
        } catch(Exception x) {
            System.out.println("Unable to load the driver class!");
        }
        System.out.println("Welcome to sales system!");
        int choice = 1;
        while(choice != 4){
            choice = mainMenu();
            switch(choice) {
                case 1: adminSystem();
                        break;
                case 2: salesSystem();
                        break;
                case 3: managerSystem();
                        break;
                case 4: break;
                default: System.out.println("Invalid choice");
            }
        }
    }
}
