import java.util.Scanner;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class LoadData {
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
    public static Scanner dataS;

    public static void loadData(){
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

    public static void main(String[] args){
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
                "d075", "3170");
            stmt = conn.createStatement();
            loadData();
        } catch(Exception e) {
            localprintEx(e);
            System.out.println("Unable to load the driver class!");
        }
        System.out.println("Debug: quiting main");
    }
}
