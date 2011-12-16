package org.mule.internal.sfdctesting.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class WriteSFDCConfigFile {
	
	public static final String TEMPLATE = "src/main/resources/config.template";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		if (args.length < 2) {
			System.out.println("Usage: Parameters required are <Starting Number>, <Ending Number>, and optional <Output Filename>");
			System.exit(-1);
		}
		
		int s = Integer.parseInt(args[0]);
		int e = Integer.parseInt(args[1]);
		
		String filename;
		if (args.length < 3) {
			filename = "src/main/resources/mule-sfdc-generated.xml";
		} else {
			filename = args[2];
		}
		
		Scanner scanner = new Scanner(new FileInputStream(TEMPLATE));
		StringBuilder sb = new StringBuilder();
		String sline;
		
		try {
			while (scanner.hasNextLine()) {
				sline = scanner.nextLine();
				if (sline.indexOf("#[generated]") >= 0) {
					
				} else {
					sb.append(sline);
				}
			}
		} finally {
			scanner.close();
		}
		
		// now write out
		Writer out = new OutputStreamWriter(new FileOutputStream(filename));
		try {
			out.write(sb.toString());
		} finally {
			out.close();
		}
	}
	
	private String createConfig(int s, int e) {
		StringBuilder sb = new StringBuilder();
		
		for (int i=s; i <= e; i++) {
			
		}
		
		return sb.toString();
	}
}
