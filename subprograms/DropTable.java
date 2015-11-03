import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class DropTable {
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

    public static void dropTable(){
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
            stmt.close();
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
        dropTable();
        System.out.println("Debug: quiting main");
    }
}
