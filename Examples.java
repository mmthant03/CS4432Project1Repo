import simpledb.remote.SimpleDriver;

import javax.xml.transform.Result;
import java.sql.*;

public class Examples {
    public static void main (String []args) {
        Connection conn = null;
        try {
            Driver d = new SimpleDriver();
            conn = d.connect("jdbc:simpledb://localhost", null);
            Statement stmt = conn.createStatement();

            System.out.println("Creating Two Tables named EMPLOYEE and DEPARTMENT");

            String s1 = "create table EMPLOYEE(empID int, empName varchar(30), dID int)";
            System.out.println(s1);
            stmt.executeUpdate(s1);
            System.out.println("Table EMPLOYEE created.");

            String s2 = "create table DEPARTMENT(deptID int, deptName varchar(30))";
            System.out.println(s2);
            stmt.executeUpdate(s2);
            System.out.println("Table DEPARTMENT created.");

            System.out.println("\nInserting Data");

            String insertE = "insert into EMPLOYEE(empID, empName, dID) values ";
            String empVals[] = {
                    "(1, 'joe', 100)",
                    "(2, 'amy', 101)",
                    "(3, 'danya', 100)",
                    "(4, 'watson', 102)",
                    "(5, 'trump', 101)"
            };
            for (int i=0; i<empVals.length; i++) {
                String newInsert = insertE + empVals[i];
                System.out.println(newInsert);
                stmt.executeUpdate(newInsert);
            }
            System.out.println("EMPLOYEE data inserted.");

            String insertD = "insert into DEPARTMENT(deptID, deptName) values ";
            String deptVals[] = {
                    "(100, 'marketing')",
                    "(101, 'hr')",
                    "(102, 'finance')"
            };
            for (int i=0; i<deptVals.length; i++) {
                String newInsert1 = insertD + deptVals[i];
                System.out.println(newInsert1);
                stmt.executeUpdate(newInsert1);
            }
            System.out.println("DEPARTMENT data inserted.");

            System.out.println("\nSelecting all Employees");

            String selectAllEmp = "select empID, empName, dID from employee";
            System.out.println(selectAllEmp);
            ResultSet rsE = stmt.executeQuery(selectAllEmp);
            while(rsE.next()) {
                Integer empId = rsE.getInt("empID");
                String emp = rsE.getString("empName");
                Integer dId = rsE.getInt("dID");
                System.out.println(empId + "\t" + emp + "\t" + dId);
            }
            rsE.close();

            System.out.println("\nSelecting all Departments");

            String selectAllDept = "select deptID, deptName from department";
            System.out.println(selectAllDept);
            ResultSet rsD = stmt.executeQuery(selectAllDept);
            while(rsD.next()) {
                Integer deptId = rsD.getInt("deptID");
                String deptName = rsD.getString("deptName");
                System.out.println(deptId + "\t" + deptName);
            }
            rsD.close();

            System.out.println("\nSelecting Employee with empId=1");

            String selectByEmpId = "select empName from employee where empID = 1";
            System.out.println(selectByEmpId);
            ResultSet rs = stmt.executeQuery(selectByEmpId);
            while (rs.next()) {
                String empName = rs.getString("empname");
                System.out.println(empName);
            }
            rs.close();
            System.out.println("End of Result");

            System.out.println("\nSelecting Department with deptId=100");

            String selectByDeptId = "select deptName from department where deptID = 100";
            System.out.println(selectByDeptId);
            ResultSet rs1 = stmt.executeQuery(selectByDeptId);
            while (rs1.next()) {
                String deptName = rs1.getString("deptname");
                System.out.println(deptName);
            }
            rs1.close();
            System.out.println("End of Result");

            System.out.println("\nSelecting Employee with deptID=100");
            String selectEmpByDeptId = "select empName from employee, department where dID = deptID and deptID = 100";
            System.out.println(selectEmpByDeptId);
            ResultSet rs2 = stmt.executeQuery(selectEmpByDeptId);
            while (rs2.next()) {
                String empName1 = rs2.getString("empname");
                System.out.println(empName1);
            }
            rs2.close();
            System.out.println("End of Result");

            System.out.println("\nUpdating one employee to a new department");
            String updateEmp = "update employee set dID = 102 where empID = 3";
            System.out.println(updateEmp);
            stmt.executeUpdate(updateEmp);
            System.out.println("Employee record updated");
            System.out.println("Resulting EMPLOYEE Table");
            ResultSet rsE1 = stmt.executeQuery(selectAllEmp);
            while(rsE1.next()) {
                String emp1 = rsE1.getString("empname");
                Integer dId1 = rsE1.getInt("did");
                System.out.println(emp1 + "\t" + dId1);
            }
            rsE1.close();
            System.out.println("End of Result");
            
            System.out.println("\nDeleting an employee.");
            String deleteEmp = "delete from employee where dID = 100";
            System.out.println(deleteEmp);
            stmt.executeUpdate(deleteEmp);
            System.out.println("Employee records deleted");
            System.out.println("Resulting EMPLOYEE Table");
            ResultSet rsE2 = stmt.executeQuery(selectAllEmp);
            while(rsE2.next()) {
                String emp1 = rsE2.getString("empname");
                Integer dId1 = rsE2.getInt("did");
                System.out.println(emp1 + "\t" + dId1);
            }
            rsE2.close();
            System.out.println("End of Result");
            


        }
        catch (SQLException e) {
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
    
    /*
     * Expected output:
     * 
     * Creating Two Tables named EMPLOYEE and DEPARTMENT
create table EMPLOYEE(empID int, empName varchar(30), dID int)
Table EMPLOYEE created.
create table DEPARTMENT(deptID int, deptName varchar(30))
Table DEPARTMENT created.

Inserting Data
insert into EMPLOYEE(empID, empName, dID) values (1, 'joe', 100)
insert into EMPLOYEE(empID, empName, dID) values (2, 'amy', 101)
insert into EMPLOYEE(empID, empName, dID) values (3, 'danya', 100)
insert into EMPLOYEE(empID, empName, dID) values (4, 'watson', 102)
insert into EMPLOYEE(empID, empName, dID) values (5, 'trump', 101)
EMPLOYEE data inserted.
insert into DEPARTMENT(deptID, deptName) values (100, 'marketing')
insert into DEPARTMENT(deptID, deptName) values (101, 'hr')
insert into DEPARTMENT(deptID, deptName) values (102, 'finance')
DEPARTMENT data inserted.

Selecting all Employees
select empID, empName, dID from employee
1	joe	100
2	amy	101
3	danya	100
4	watson	102
5	trump	101

Selecting all Departments
select deptID, deptName from department
100	marketing
101	hr
102	finance

Selecting Employee with empId=1
select empName from employee where empID = 1
joe
End of Result

Selecting Department with deptId=100
select deptName from department where deptID = 100
marketing
End of Result

Selecting Employee with deptID=100
select empName from employee, department where dID = deptID and deptID = 100
joe
danya
End of Result

Updating one employee to a new department
update employee set dID = 102 where empID = 3
Employee record updated
Resulting EMPLOYEE Table
joe	100
amy	101
danya	102
watson	102
trump	101
End of Result

Deleting an employee.
delete from employee where dID = 100
Employee records deleted
Resulting EMPLOYEE Table
amy	101
danya	102
watson	102
trump	101
End of Result

     */




}
