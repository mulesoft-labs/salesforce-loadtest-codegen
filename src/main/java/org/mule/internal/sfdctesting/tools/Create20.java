package org.mule.internal.sfdctesting.tools;

import org.mule.internal.sfdctesting.tools.WriteSFDCConfigFile;

public class Create20 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int j=0;
		for (int i=1; i < 21; i++) {
			String[] params = new String[4];
			params[0] = new Integer(j + 1).toString();
			params[1] = new Integer(j + 1000).toString();
			params[2] = "flow" + i + ".template";
			params[3] = "mule-sfdc-config" + i;
			
			WriteSFDCConfigFile.main(params);
			
			j = j + 1000;
		}

	}

}
