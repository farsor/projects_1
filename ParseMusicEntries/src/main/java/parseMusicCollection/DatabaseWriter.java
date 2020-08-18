package parseMusicCollection;

import java.sql.*;

public class DatabaseWriter {
	
	Connection myConn;
	Statement myStatement;
	String sql;
	
	//construct database writer with table name as parameter
	DatabaseWriter(String table){
		try {
			myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/collections_2","root","password");
			myStatement = myConn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//construct sql string with field 
	//@param fields - string array with names of fields
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
	
//	//create tables
//	public static void main(String[] args) {
//		
//		try {
//			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/collections_2","root","password");
//			
//			Statement myStatement = myConn.createStatement();
//			
//			//create source table
//			//make sure to change schema below***
//			String sql = "CREATE TABLE `collections_2`.`sources`(`source_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL`source_number` TEXT NOT NULL, `source_call_number` TEXT NOT NULL,"
//					+ " `source_author` TEXT NOT NULL, `source_title` TEXT NOT NULL, `source_inscription` TEXT NOT NULL, `source_description` TEXT NOT NULL, PRIMARY KEY (`source_id`));";
//			
//////			//create entries table
////			String sql = "CREATE TABLE `collections_2`.`entries`(`entry_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL, `source_number` TEXT NOT NULL, `entry_location` TEXT NOT NULL,"
////					+ " `entry_title` TEXT NOT NULL, `entry_credit` TEXT NOT NULL, `entry_vocal_part` TEXT NOT NULL, `entry_key` TEXT NOT NULL, `entry_melodic_incipit` TEXT NOT NULL,"
////					+ " `entry_text_incipit` TEXT NOT NULL, `entry_is_secular` TEXT NOT NULL, PRIMARY KEY (`entry_id`));";
//						
////			//create collections table		
////			String sql = "CREATE TABLE `collections_2`.`collections`(`collection_id` INT NOT NULL AUTO_INCREMENT, `collection_name` TEXT NOT NULL, `collection_description` TEXT NOT NULL, PRIMARY KEY (`collection_id`));";
//			
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
