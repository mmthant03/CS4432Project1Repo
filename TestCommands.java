import java.sql.*;
import simpledb.remote.SimpleDriver;

public class TestCommands {
	public static void main(String[] args) {
		
		Connection conn = null;
		try {
			
			// Step 1: connect to database server
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			
			//setup and run query
			Statement stmt = conn.createStatement();
			
			String qry = "select SName from STUDENT where SId = 3";
			ResultSet rs = stmt.executeQuery(qry);
			
			// loop over result set
			while (rs.next()) {
				String sName = rs.getString("SName");
				System.out.println(sName);
			}
			rs.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			// Step 4: close the connection
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
