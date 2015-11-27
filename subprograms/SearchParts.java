import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class SearchParts {
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
    public static PreparedStatement pstmt;

    public static void searchParts(){
        boolean valid = true;
        boolean done = true;
        int count = 0;
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
                localprintEx(e);
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
                System.out.println("| " + pid + " | " +  ppname.replaceAll("\\s+", " ") + " | " + mname.replaceAll("\\s+", " ") + cname.replaceAll("\\s+", " ") + quantity + " | " + warranty + " | " + price + " |");      
                //System.out.format("|%6d|%20s|%20s|%20s|%20d|%n", pid, p_name.replaceAll("\\s+", " "),  c_name.replaceAll("\\s+", " "),  m_name.replaceAll("\\s+", " "),price);      
            }
            rs.close();
            prestmt.close();

        }catch(SQLException e){
            localprintEx(e);
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
        searchParts();
        System.out.println("Debug: quiting main");
    }
}
