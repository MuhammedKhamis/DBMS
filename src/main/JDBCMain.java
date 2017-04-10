package main;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import eg.edu.alexu.csd.oop.jdbc.JavaDriver;
import eg.edu.alexu.csd.oop.jdbc.JavaResultset;

public class JDBCMain {

    public static void main(String[] args) throws SQLException {
        // TODO Auto-generated method stub

        // load Configurations

        Scanner input = new Scanner(System.in);
        Driver driver = new JavaDriver();
        ResultSet result;
        while (true) {
            System.out.println("Enter URL :");
            String url = input.nextLine();
            if (driver.acceptsURL(url)) {
                System.out.println("Enter Path:");
                String path = input.nextLine();
                Properties info = new Properties();
                File dbDir = new File(path);
                info.put("path", dbDir.getAbsoluteFile());
                Connection connection = driver.connect(url, info);
                Statement statment = connection.createStatement();
                while (true) {
                    try {
                        System.out.print("sql>");
                        String tmp = input.nextLine();
                        if (statment.execute(tmp)) {
                            result = statment.getResultSet();
                            ArrayList<ArrayList<String>> output = ((JavaResultset) result).getArrayList();
                            // Draw Here
                        }
                    } catch (Exception e) {
                        System.out.println("Error");
                    }
                }

            }
        }
    }

}
