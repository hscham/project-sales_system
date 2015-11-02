import java.util.Scanner;
import java.io.*;
import java.sql.*;

public class SalesSystem {
    public static Scanner s = new Scanner(System.in);
    //Load JDBC Driver
    public static int mainMenu(){
        System.out.println("-----Main menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Operations for administrator");
        System.out.println("2. Operations for salesperson");
        System.out.println("3. Operations for manager");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
        return s.nextInt();
    }

    public static int adminMenu(){
        System.out.println("-----Operations for administrator menu----");
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
    }

    public static void deleteTable(){
    }

    public static void loadFromDatafile(){
    }

    public static void showRecordsNum(){
    }

    public static int salesMenu(){
        System.out.println("-----Operations for salesperson menu----");
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
        int partId = s.nextInt();
        System.out.print("Enter the Salesperson ID: ");
        int saleID = s.nextInt();
    }

    public static int managerMenu(){
        System.out.println("-----Operations for manager menu----");
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
            case 5: break;
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
            case 3: break;
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
            case 4: break;
            }
        }
    }

    public static void main(String[] args){
        try{
            Class.forName(" oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@db12.cse.cuhk.edu.hk:1521:db12",
                "d075", "ieytlflx");
            Statement stmt = conn.createStatement();
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
            }
        }
    }
}
