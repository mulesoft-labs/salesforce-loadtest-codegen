package org.mule.internal.sfdctesting.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

public class WriteSFDCConfigFile {
	
	public static final String TEMPLATE = "src/main/resources/config.template";
	public static final String FLOW = "src/main/resources/";
	private static final String NL = System.getProperty("line.separator");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("Config Writer starting up...");
		
		if (args.length < 3) {
			System.out.println("Usage: Parameters required are <Starting Number>, <Ending Number>, template <Template> and optional <Output Filename>");
			System.exit(-1);
		}
		
		int s = Integer.parseInt(args[0]);
		int e = Integer.parseInt(args[1]);
		
		String template = args[3];
		
		String filename;
		if (args.length < 4) {
			filename = "src/main/app/mule-sfdc-config.xml";
		} else {
			filename = args[3];
		}
		
		Scanner scanner = new Scanner(new FileInputStream(TEMPLATE));
		StringBuilder sb = new StringBuilder();
		String sline;
		
		try {
			while (scanner.hasNextLine()) {
				sline = scanner.nextLine();
				if (sline.indexOf("#[generated]") >= 0) {
					sb.append(createConfig(s,e, template));
					sb.append(NL);
				} else {
					sb.append(sline);
					sb.append(NL);
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
			System.out.println("Config Writer finished...");
		}
	}
	
	private static String createConfig(int s, int e, String template) throws Exception {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbflow = new StringBuilder();
		
		Scanner scanner = new Scanner(new FileInputStream(FLOW + template));
		while (scanner.hasNextLine()) {
			sbflow.append(scanner.nextLine());
			sbflow.append(NL);
		}
		String sflow = sbflow.toString();
		
		for (int i=s; i <= e; i++) {
			sb.append(NL);
			sb.append("<!-- Config for User # " + i + " -->" + NL);
			sb.append(sflow.replace("??", String.valueOf(i)));
		}
		
		return sb.toString();
	}
}
