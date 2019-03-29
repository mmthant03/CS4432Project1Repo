// Directory = studentClient.simpledb.CreateEmployeeDB.java

import java.sql.*;
import simpledb.remote.SimpleDriver;

public class CreateEmployeeDB {
    public static void main(String[]args) {
        Connection conn = null;
        try {
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);
            Statement stmt = conn.createStatement();

            String s = "create table EMPLOYEE(empID int, empName varchar(30), deptID int)";
            stmt.executeUpdate(s);
            System.out.println("Table EMPLOYEE created.");

            s = "insert into EMPLOYEE(empID, empName, deptID) values ";
            String[] empvals = {"(1, 'joe', 01)",
                    "(2, 'amy', 01)",
                    "(3, 'max', 03)",
                    "(4, 'sue', 02)",
                    "(5, 'bob', 03)",
                    "(6, 'kim', 01)",
                    "(7, 'art', 02)",
                    "(8, 'pat', 04)",
                    "(9, 'lee', 03)"};
            for (int i=0; i<empvals.length; i++)
                stmt.executeUpdate(s + empvals[i]);
            System.out.println("EMPLOYEE records inserted.");

            s = "create table DEPT(deptID int, deptName varchar(20))";
            stmt.executeUpdate(s);
            System.out.println("Table Dept created.");

            s = "insert into DEPT(deptID, deptName) values ";
            String[] deptvals = {"(01, 'Marketing')",
                    "(02, 'HumanResource')",
                    "(03, 'Logistic')",
                    "(04, 'CustomerService')"};
            for (int i=0; i<deptvals.length; i++)
                stmt.executeUpdate(s + deptvals[i]);
            System.out.println("DEPT records inserted.");

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
