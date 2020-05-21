package parseMusicCollection;

import java.sql.*;

public class DatabaseWriter {
	
	Connection myConn;
	Statement myStatement;
	String sql;
	
	DatabaseWriter(String table){
		try {
			myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/collections","root","password");
			myStatement = myConn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public String fieldsToSQLStr(String[] fields) {
		StringBuilder fieldsStr = new StringBuilder();
		fieldsStr.append(fields[0]);
		for(int i = 1; i < fields.length; i++) {
			fieldsStr.append(", " + fields[i]);
		}
		return fieldsStr.toString();
	}
	
	public String entryToSQLStr(String[] entry) {
		StringBuilder entryStr = new StringBuilder();
		entryStr.append("'" + entry[0] + "'");
		for(int i = 1; i < entry.length; i++) {
			entryStr.append(", '" + entry[i] + "'");
		}
		return entryStr.toString();
	}
	
	public void writeData(String table, String fields, String data) {
		sql = "insert into " + table +" (" + fields +") values (" + data + ")";
		try {
			myStatement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		
//		try {
//			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/collections","root","password");
//			
//			Statement myStatement = myConn.createStatement();
//			
//			//create source table
////			String sql = "CREATE TABLE `collections`.`sources`(`collection_name` TEXT NULL, `source_number` TEXT NULL, `source_call_number` TEXT NULL,"
////					+ " `source_author` TEXT NULL, `source_title` TEXT NULL, `source_inscription` TEXT NULL, `source_description` TEXT NULL);";
//			
//			//create entries table
//			String sql = "CREATE TABLE `collections`.`entries`(`collection_name` TEXT NULL, `source_number` TEXT NULL, `entry_location` TEXT NULL,"
//					+ " `entry_title` TEXT NULL, `entry_credit` TEXT NULL, `entry_vocal_part` TEXT NULL, `entry_key` TEXT NULL, `entry_melodic_incipit` TEXT NULL,"
//					+ " `entry_text_incipit` TEXT NULL, `entry_is_secular` TEXT NULL);";
//			
//			myStatement.executeUpdate(sql);
//			
////			ResultSet myResults = myStatement.executeQuery("SELECT * FROM city");
////			
////			while(myResults.next()) {
////				System.out.println(myResults.getString("name") + "   " + myResults.getString("population"));
////			}
//			
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

}
