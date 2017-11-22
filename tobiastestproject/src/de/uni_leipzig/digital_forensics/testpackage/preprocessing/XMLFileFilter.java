package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.File;

public class XMLFileFilter {
	   public boolean accept(File pathname) {
		      return pathname.getName().toLowerCase().endsWith(".xml");
		   }
}
