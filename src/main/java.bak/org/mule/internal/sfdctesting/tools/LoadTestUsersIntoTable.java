package org.mule.internal.sfdctesting.tools;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class LoadTestUsersIntoTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String filename;
		if (args.length == 0) {
			filename = "src/test/resources/testusers.txt";
		} else {
			filename = args[0];
		}
		
		Scanner scanner = new Scanner(new FileInputStream(filename));
		
		try {
			while (scanner.hasNextLine()) {
				insertUser(scanner.nextLine().split(","));
			}
		} finally {
			scanner.close();
		}
		
	}
	
	private static void insertUser(String[] userinfo) throws Exception {
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:33061/sfdcloadtesting?user=sfdcloadtest&password=mule2demo");
		PreparedStatement pstmt = connect.prepareStatement("INSERT INTO sfdcloadtesting.testusers (username, password) VALUES (?, ?)");
		pstmt.setString(1, userinfo[0].trim());
		pstmt.setString(2, userinfo[1].trim());
		// pstmt.setString(3, userinfo[2].trim());
		int i = pstmt.executeUpdate();
		
		System.out.println(i + " row(s) updated.");
	}

}
