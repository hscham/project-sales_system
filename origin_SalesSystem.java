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
        System.out.println("Debug: after sql for create table category");
		stmt.execute(sql);	
        System.out.println("Debug: after create table category");
		
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
        System.out.println("Debug: after create table manufacturer");

        // Part
        sql = "CREATE TABLE Part " +
                     "(pid INTEGER, " +
                     " cid INTEGER, " +
                     " mid INTEGER, " +
                     " pname VARCHAR(20) NOT NULL," +
                     " pprice INTEGER NOT NULL," +   
                     " CONSTRAINT fk_part_cid foreign key(cid) references Category," +
                     " CONSTRAINT fk_part_mid foreign key(mid) references Manufacturer," +
                     " pquantity INTEGER NOT NULL," +
                     " PRIMARY KEY(pid)," +
                     " CONSTRAINT pid_check CHECK (pid >0 AND pid <=999)," +
                     " CONSTRAINT pprice_check CHECK (pprice >0 AND pprice <=99999)," +
                     " CONSTRAINT pquantity_check CHECK (pquantity >=0 AND pquantity <=99))"; 
        stmt.execute(sql);    
        System.out.println("Debug: after create table part");

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
        System.out.println("Debug: after create table salesperson");

        // Transaction
        sql = "CREATE TABLE Transaction " +
                     "(tid INTEGER AUTO_INCREMENT," +
                     " pid INTEGER," +
                     " sid INTEGER," +
                     " CONSTRAINT fk_tran_pid foreign key(pid) references Part," +
                     " CONSTRAINT fk_tran_sid foreign key(sid) references Salesperson," +
                     " tdate DATE NOT NULL DEFAULT GETDATE()," +
                     " PRIMARY KEY(tid)," +
                     " CONSTRAINT tid_check CHECK (tid >0 AND tid <=9999))"; 
        stmt.execute(sql); 
        System.out.println("Debug: after create table transaction");

		}catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
			done = false;
		}
		if(done){
			System.out.println("Done! Database is initialized!");
		}else{
			System.out.println("Fail to initialize the database!");
		}
    }

    public static void deleteTable(){
        boolean done = true;
        System.out.print("Processing...");
        try{
            stmt = conn.createStatement();
            String sql;

            sql = "ALTER TABLE Part DROP FOREIGN KEY fk_part_cid";
            stmt.executeUpdate(sql); 
            sql = "ALTER TABLE Part DROP FOREIGN KEY fk_part_mid";
            stmt.executeUpdate(sql); 
            sql = "ALTER TABLE Transaction DROP FOREIGN KEY fk_tran_pid";
            stmt.executeUpdate(sql); 
            sql = "ALTER TABLE Transaction DROP FOREIGN KEY fk_tran_sid";
            stmt.executeUpdate(sql); 

            sql = "DROP TABLE Category";
            stmt.executeUpdate(sql); 

            sql = "DROP TABLE Manufacturer";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Part";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Salesperson";
            stmt.executeUpdate(sql);

            sql = "DROP TABLE Transaction";
            stmt.executeUpdate(sql);
            stmt.close();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
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
            System.out.println("Processing...Done! Data is inputted to the database!");
        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };

    }

    public static void showRecordsNum(){
		int numOfRecord;
		String selectSQL = "Select count(*) as total from ?";
		System.out.printf("Number of records on each table:\n");
        try {
            PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            pstmt.setString(1, "category");
		    ResultSet rs = pstmt.executeQuery(selectSQL);
		    numOfRecord = rs.getInt("total");
		    System.out.printf("category:%d\n",numOfRecord);
		    
		    pstmt.setString(1, "manufacturer");
		    rs = pstmt.executeQuery(selectSQL);
		    numOfRecord = rs.getInt("total");
		    System.out.printf("manufacturer:%d\n", numOfRecord);
		    
		    pstmt.setString(1, "part");
		    rs = pstmt.executeQuery(selectSQL);
		    numOfRecord = rs.getInt("total");
		    System.out.printf("part:%d\n", numOfRecord);
		    

		    pstmt.setString(1, "salesperson");
		    rs = pstmt.executeQuery(selectSQL);
		    numOfRecord = rs.getInt("total");
		    System.out.printf("salesperson:%d\n", numOfRecord);
		    

		    pstmt.setString(1, "transaction");
		    rs = pstmt.executeQuery(selectSQL);
		    numOfRecord = rs.getInt("total");
		    System.out.printf("transaction:%d\n", numOfRecord);
        } catch (SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };
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
        boolean valid = true;
        boolean done = true;
        int count = 0;
        PreparedStatement prestmt = null;
        int s_choice = 0;

        do{
            valid = true;
            try{
                System.out.println("Choose the search criterion: ");    
                System.out.println("1. Part Name");
                System.out.println("2. Manufacturer Name");
                System.out.print("Choose the search criterion: ");
                s_choice = s.nextInt();
            }catch(NumberFormatException e){
                System.out.println(e.getMessage());
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getCause());
                System.out.println(Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
                valid = false;  //if error is caught, then valid is false
            }

            if (s_choice < 1 || s_choice > 2){
                 valid = false;     
            }
            if(!valid){
                  System.out.println("Invalid input! Please try it again");
            }
        }while(!valid);

        System.out.println("");

        System.out.print("Type in the Search Keyword: "); 
        String keyword = s.nextLine().replaceAll("\\s+", " ");      //remove all duplicated space in the input

        String sql = "";
        switch(s_choice){
            case 1: sql = "SELECT P.pid, P.pname, M.mname, C.cname, P.pquantity, M.mwarranty, P.pprice " + 
                           "FROM Part P, Manufacturer M, Category C " +
                           "WHERE P.mid = M.mid AND P.cid = C.cid AND P.pquantity > 0 AND P.pname LIKE '%'" +keyword+ "'%'";  //or '%"+keyword+"%'"
                           break;   
            case 2: sql = "SELECT P.pid, P.pname, M.mname, C.cname, P.pquantity, M.mwarranty, P.pprice " +
                           "FROM Part P, Manufacturer M, Category C " +
                           "WHERE P.mid = M.mid AND P.cid = C.cid AND P.pquantity > 0 AND M.mname LIKE '%'" +keyword+ "'%'";  //or '%"+keyword+"%'";
                           break;                

        } 

        try{

            prestmt = conn.prepareStatement(sql);
            ResultSet rs = prestmt.executeQuery();
            System.out.println("|  ID  |  Name  |  Manufacturer  |  Category  | Quantity  |  Warranty  |  Price  |");

            while(rs.next()){
                int pid = rs.getInt(1);
                String ppname = rs.getString(2);
                String mname = rs.getString(3);
                String cnmae = rs.getString(4);
                int quantity = rs.getInt(5);
                int warranty = rs.getInt(6);
                int price = rs.getInt(7);
                count++;
            //    System.out.format("|%6d|%20s|%20s|%20s|%20d|%n", pid, p_name.replaceAll("\\s+", " "),  c_name.replaceAll("\\s+", " "),  m_name.replaceAll("\\s+", " "),price);      
            }
            rs.close();
            prestmt.close();

        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            done = false;
        }

        if (done && (count == 0)){
            System.out.println("No Result Found!");
        }
        else if (done &&(count > 0 )){
            System.out.println("End of the search");
        }
        else {
            System.out.println("Fail to show the result");
        }

        System.out.println("");
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
        } catch (SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
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
		System.out.printf("Enter The Salesperson ID : ");
		int sID = s.nextInt();
		System.out.printf("Type in the starting date [dd/mm/yyyy] : ");
		String startDate = s.next();
		System.out.printf("Type in the ending date [dd/mm/yyyy] : ");
		String endDate = s.next();
		
		int tID, pID,pPrice;
		String pName, mName, date;
		
        try {
		    String selectSQL = "SELECT t.tID, p.pID, p.pName, m.mName, p.pName, t.tDate "
		    		+ "FROM transaction t, part p , manufacturer m, salesperson s " 
		    		+ "WHERE sID = ? AND s.sID = t.sID AND t.pID = p.pID AND p.mID = m.mID AND t.tDate <= ? AND t.tDate>=?"
		    		+ "ORDER BY t.tDate DESC";
		    PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date dateU = sdf.parse(startDate);
            java.sql.Date sdate = new Date(dateU.getTime());
            dateU = sdf.parse(endDate);
            java.sql.Date edate = new Date(dateU.getTime());
		    pstmt.setInt(1, sID);
		    pstmt.setDate(2, sdate);
		    pstmt.setDate(3, edate);
		    ResultSet rs = pstmt.executeQuery(selectSQL);
		    System.out.printf("Transaction Record:\n");
		    System.out.printf("| ID | Part ID | Part Name | Manufacturer | Price | Date |\n");
		    
		    while(rs.next()){
		    	tID = rs.getInt("tID");
		    	pID = rs.getInt("pID");
		    	pName = rs.getString("pName");
		    	mName = rs.getString("mName");
		    	pPrice = rs.getInt("pPrice");
		    	date = rs.getDate("tDate").toString();
		    	System.out.printf("| %d | %d | %s | %s | %d | %s |\n", tID, pID, pName, mName, pPrice, date);
		    
		    }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };
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
        } catch (SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };
        System.out.println("End of Query");
    }

    public static void showPopularParts(){
		System.out.printf("Type in the number of parts: ");
		int numOfPart = s.nextInt();
		int pID, total;
		String pName;
		
        try {
		String selectSQL = "SELECT p.pID, p.pName, count(*) as total"
				+"from part p, transaction t"
				+"where p.pID = t.pID"
				+"group by p.pID"
				+"order by total DESC"
				+"limit ?";
		PreparedStatement pstmt = conn.prepareStatement(selectSQL);
		pstmt.setInt(1, numOfPart);
		ResultSet rs = pstmt.executeQuery(selectSQL);
		System.out.printf("| Part ID | Part Name | No. if Transaction |\n");
		while(rs.next()){
			pID = rs.getInt("pID");
			pName = rs.getString("pName");
			total = rs.getInt("total");
			System.out.printf("| %d | %s | %s |\n", pID, pName, total);
		}
        } catch (SQLException e){
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };
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
                "d075", "ieytlflx");
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
            System.out.println("Fail to close Statement stmt");
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        };
    }
}
