package org.mule.internal.sfdctesting.tools;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UsersPropertiesWriter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String filename;
		if (args.length == 0) {
			filename = "src/test/resources/test.users.properties";
		} else {
			filename = args[0];
		}
		
		write(new OutputStreamWriter(new FileOutputStream(filename)));
	}

	private static void write(Writer out) throws Exception {
		String NL = System.getProperty("line.separator");
		
	
		try {
			out.write("#SFDC users to be used for testing" + NL);
			out.write(NL);

			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager
					.getConnection("jdbc:mysql://sfdcloadtesting.cbbmvnwhlhi8.us-east-1.rds.amazonaws.com:3306/sfdcloadtesting?user=sfdcloadtest&password=mule2demo");
			PreparedStatement pstmt = connect
					.prepareStatement("SELECT username, password FROM sfdcloadtesting.testusers");
			System.out.println("Prepared statement...");
			ResultSet rs = pstmt.executeQuery();
			System.out.println("Results returned...");
			
			int i = 0;
			while (rs.next()) {
				i++;
				out.write("#User" + i + NL);

				String username = rs.getString("username");
				out.write("sfdc." + i + ".username=" + username + NL);
				String password = rs.getString("password");
				out.write("sfdc." + i + ".password=" + password + NL);
				//String securitytoken = rs.getString("securitytoken");
				//out.write("sfdc." + i + ".securityToken=" + securitytoken + NL);

				out.write(NL);
			}
		} finally {
			out.close();
		}
		
	}
	
}
