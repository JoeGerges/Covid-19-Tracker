import java.sql.*;
import java.util.ArrayList;

public class SQLManager {
	private Connection conn;
	
	public SQLManager(String fileName) {	
		try {
			String url = "jdbc:sqlite:" + fileName;
			conn= DriverManager.getConnection(url);
			System.out.println("Connection establishment succeeded");
		}
		catch ( Exception e) {
			System.out.println("Connection establishment failed");
		}
	}
	
	public void CreatePatientTable() {
		try {
			System.out.println("Creating Patients Table ....");
			String sql="create table IF NOT EXISTS Patients (\r\n"
					+ "		  Name text NOT NULL,\r\n"
					+ "		  MacAddress varchar(50) NOT NULL, \r\n"
					+ "		  PhoneNumber varchar(50) NOT NULL,\r\n"
					+ "		  primary key (MacAddress)\r\n"
					+ "		);";
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		}
		catch(Exception e) {
			System.out.println("Creating Patient table failed");
		}
	}
	
	public ArrayList<Patient> GetAllPatients() {
		try {
			System.out.println("Finding All Patients.....:");
			String sql  = "SELECT *\r\n"
						+ "  FROM Patients \r\n";
			Statement stmt             = conn.createStatement();
			ResultSet results          = stmt.executeQuery(sql);
			ArrayList<Patient>patients = new ArrayList<Patient>();
			
			while(results.next()) {
				Patient p = new Patient(results.getString("Name"), 
						results.getString("PhoneNumber"),
						results.getString("MacAddress") );
				patients.add(p);
			}
			
			return patients;
		}
		catch(Exception e) {
			System.out.println("getting all patients failed");
			return null;
		}
	} 	
	
	public void InsertPatient(Patient p) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO Patients VALUES (?, ?, ?)");
			ps.setString(1, p.getName());
			ps.setString(2, p.getMac());
			ps.setString(3, p.getPhone());
			ps.execute();
		}
		
		catch (Exception e) {
			System.out.println("Inserting Patient Failed");
		}
	}
}
