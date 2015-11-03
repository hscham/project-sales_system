import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class CreateTable {
    static void localprintEx (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
    }

    public static Scanner s = new Scanner(System.in);
    //Load JDBC Driver
    public static Connection conn;
    public static Statement stmt;

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

    public static void main(String[] args){
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
                "d075", "3170");
            stmt = conn.createStatement();
        } catch(Exception e) {
            localprintEx(e);
            System.out.println("Unable to load the driver class!");
        }
        createTable();
        System.out.println("Debug: quiting main");
    }
}
