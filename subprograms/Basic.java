import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class Basic {
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
        System.out.println("Debug: quiting main");
    }
}
