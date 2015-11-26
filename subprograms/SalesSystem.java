import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class SalesSystem {
    public static Scanner s = new Scanner(System.in);
    //Load JDBC Driver
    public static Connection conn;
    public static Statement stmt;
    public static PreparedStatement pstmt;
    public static Scanner dataS;

    static void localprintEx (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
    }

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
        boolean done = true;
        System.out.print("Processing...");
            try{

            // Category
            String sql = "CREATE TABLE Category " +
                         "(cid INTEGER," +
                         " cname VARCHAR(20) NOT NULL," +
                         " PRIMARY KEY(cid)," +
                         " CONSTRAINT cid_check CHECK (cid >0 AND cid <=9))";
            stmt.execute(sql);  
            //System.out.println("Debug: after create table category");
            
            // Manufacturer
            sql = "CREATE TABLE Manufacturer " +
                         "(mid INTEGER," +
                         " mname VARCHAR(20) NOT NULL," +
                         " maddress VARCHAR(50) NOT NULL," +     
                         " mphone INTEGER NOT NULL," +   
                         " mwarranty INTEGER NOT NULL," +
                         " PRIMARY KEY(mid)," +     
                         " CONSTRAINT mid_check CHECK (mid >0 AND mid <=99)," +
                         " CONSTRAINT mphone_check CHECK (mphone >=00000000 AND mphone <=99999999)," +
                         " CONSTRAINT mwarranty_check CHECK (mwarranty >=0 AND mwarranty <=9))"; 
            stmt.execute(sql);  
            //System.out.println("Debug: after create table manufacturer");

            // Part
            sql = "CREATE TABLE Part " +
                         "(pid INTEGER, " +
                         " pname VARCHAR(20) NOT NULL," +
                         " pprice INTEGER NOT NULL," +   
                         " cid INTEGER, " +
                         " mid INTEGER, " +
                         " CONSTRAINT fk_part_cid foreign key(cid) references Category(cid)," +
                         " CONSTRAINT fk_part_mid foreign key(mid) references Manufacturer(mid)," +
                         " pquantity INTEGER NOT NULL," +
                         " PRIMARY KEY(pid)," +
                         " CONSTRAINT pid_check CHECK (pid >0 AND pid <=999)," +
                         " CONSTRAINT pprice_check CHECK (pprice >0 AND pprice <=99999)," +
                         " CONSTRAINT pquantity_check CHECK (pquantity >=0 AND pquantity <=99))"; 
            stmt.execute(sql);    
            //System.out.println("Debug: after create table part");

            // Salesperson
            sql = "CREATE TABLE Salesperson " +
                         "(sid INTEGER," +
                         " sname VARCHAR(20) NOT NULL," +
                         " saddress VARCHAR(50) NOT NULL," +
                         " sphone INTEGER NOT NULL," +
                         " PRIMARY KEY(sid)," +
                         " CONSTRAINT sid_check CHECK (sid >0 AND sid <=99)," +
                         " CONSTRAINT sphone_check CHECK (sphone >=00000000 AND sphone <=99999999))";
            stmt.execute(sql); 
            //System.out.println("Debug: after create table salesperson");

            // Transaction
            sql = "CREATE TABLE Transaction " +
                         "(tid INTEGER," +
                         " pid INTEGER," +
                         " sid INTEGER," +
                         " CONSTRAINT fk_tran_pid foreign key(pid) references Part(pid)," +
                         " CONSTRAINT fk_tran_sid foreign key(sid) references Salesperson(sid)," +
                         " tdate DATE DEFAULT SYSDATE," +
                         " PRIMARY KEY(tid)," +
                         " CONSTRAINT tid_check CHECK (tid >0 AND tid <=9999))"; 
            stmt.execute(sql); 
            //System.out.println("Debug: after create table transaction");

        }catch(SQLException e){
            localprintEx(e);
            done = false;
        }
        if(done){
            System.out.println("Done! Database is initialized!");
        }else{
            System.out.println("Fail to initialize the database!");
        }
    }

    public static void deleteTable(){
        //System.out.println("Debug: enter function deleteTable()");
        boolean done = true;
        System.out.print("Processing...");
        try{
            stmt = conn.createStatement();
            String sql;

            sql = "DROP TABLE Transaction";
            stmt.executeUpdate(sql); 

            sql = "DROP TABLE Salesperson";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Part";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Manufacturer";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Category";
            stmt.executeUpdate(sql);
        }catch(SQLException e){
            localprintEx(e);
            done = false;
        }
        if (done){
            System.out.println("Done! Database is removed!");
        }else{
            System.out.println("Fail to delete the database!");
        }
        System.out.println("");
    }

    public static void loadFromDatafile(){
        System.out.print("\nType in the Source Data Folder Path: ");
        String path = s.next();

        String[] tables = { "category", "manufacturer", "part", "salesperson", "transaction" };
        File[] file = new File[5];
        int i, j;
        for (i = 0; i < 5; i++)
            file[i] = new File(path + "/" + tables[i] + ".txt");
        String[] statements = {
            "INSERT INTO category VALUES (?,?)",
            "INSERT INTO manufacturer VALUES (?,?,?,?,?)",
            "INSERT INTO part VALUES (?,?,?,?,?,?)",
            "INSERT INTO salesperson VALUES (?,?,?,?)",
            "INSERT INTO transaction VALUES (?,?,?,?)"
            };
        BufferedReader dataBR;
        String tuple;

        try {
            int count = 0;
            for (i = 0; i < 5; i++){
                count = 0;
                System.out.println("i = " + i);
                pstmt = conn.prepareStatement(statements[i]);
                dataBR = new BufferedReader(new FileReader(file[i]));
                while ((tuple = dataBR.readLine()) != null){
                    System.out.println("\tcount = " + count++);
                    dataS = new Scanner(tuple).useDelimiter("\t");
                    if (i==0){
                        try {
                            pstmt.setInt(1, Integer.parseInt(dataS.next()));
                            pstmt.setString(2, dataS.next());
                        } catch (Exception e){
                            localprintEx(e);
                        }
                    } else if (i==1){
                        try {
                            pstmt.setInt(1, Integer.parseInt(dataS.next()));
                            pstmt.setString(2, dataS.next());
                            pstmt.setString(3, dataS.next());
                            pstmt.setInt(4, Integer.parseInt(dataS.next()));
                            pstmt.setInt(5, Integer.parseInt(dataS.next()));
                        } catch (Exception e){
                            localprintEx(e);
                        }
                    } else if (i==2){
                        try {
                            pstmt.setInt(1, Integer.parseInt(dataS.next()));
                            pstmt.setString(2, dataS.next());
                            pstmt.setInt(3, Integer.parseInt(dataS.next()));
                            pstmt.setInt(4, Integer.parseInt(dataS.next()));
                            pstmt.setInt(5, Integer.parseInt(dataS.next()));
                            pstmt.setInt(6, Integer.parseInt(dataS.next()));
                        } catch (Exception e){
                            localprintEx(e);
                        }
                    } else if (i==3){
                        try {
                            pstmt.setInt(1, Integer.parseInt(dataS.next()));
                            pstmt.setString(2, dataS.next());
                            pstmt.setString(3, dataS.next());
                            pstmt.setInt(4, Integer.parseInt(dataS.next()));
                        } catch (Exception e){
                            localprintEx(e);
                        }
                    } else {
                        try {
                            pstmt.setInt(1, Integer.parseInt(dataS.next()));
                            pstmt.setInt(2, Integer.parseInt(dataS.next()));
                            pstmt.setInt(3, Integer.parseInt(dataS.next()));
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            java.util.Date dateU = df.parse(dataS.next());
                            java.sql.Date date = new Date(dateU.getTime());
                            pstmt.setDate(4, date); 
                        } catch (Exception e){
                            localprintEx(e);
                        }
                    }
                    pstmt.executeUpdate();
                    dataS.close();
                }
            }
            System.out.println("Processing...Done! Data is inputted to the database!");
        } catch (Exception e){
            localprintEx(e);
        };
    }

    public static void showRecordsNum(){
        System.out.println("Debug: enter function showRecordsNum()");
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
        System.out.println("Debug: enter function searchParts()");
    }

    public static void sellPart(){
        System.out.println("Debug: enter function sellPart()");
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
        System.out.println("Debug: enter function showSalespersonRecord()");
    }

    public static void showManuSalesValue(){
         System.out.println("Debug: enter function showManuSalesValue()");
    }

    public static void showPopularParts(){
        System.out.println("Debug: enter function showPopularParts()");
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
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
                "d075", "3170");
            stmt = conn.createStatement();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
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
        try {
            stmt.close();
        } catch (Exception e){
            localprintEx(e);
            System.out.println("Fail to close Statement stmt");
        };
    }
}
