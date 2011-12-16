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
		
		System.out.println("Starting loader app...");
		
		String filename;
		if (args.length == 0) {
			filename = "src/test/resources/testusers.txt";
		} else {
			filename = args[0];
		}
		
		Scanner scanner = new Scanner(new FileInputStream(filename));
		int sum=0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager.getConnection("jdbc:mysql://sfdcloadtesting.cbbmvnwhlhi8.us-east-1.rds.amazonaws.com:3306/sfdcloadtesting?user=sfdcloadtest&password=mule2demo");

			while (scanner.hasNextLine()) {
				sum = sum + insertUser(scanner.nextLine().split(","), connect);
				if (sum % 500 == 0) {
					System.out.println("Inserted: " + sum);
				}
			}
		} finally {
			scanner.close();
		}
		System.out.println("Total Final Rows inserted: " + sum);
	}
	
	private static int insertUser(String[] userinfo, Connection connect) throws Exception {
		
		PreparedStatement pstmt = connect.prepareStatement("INSERT INTO sfdcloadtesting.testusers (username, password) VALUES (?, ?)");
		pstmt.setString(1, userinfo[0].trim());
		pstmt.setString(2, userinfo[1].trim());
		// pstmt.setString(3, userinfo[2].trim());
		int i = pstmt.executeUpdate();
		
		return i;
	}

}
