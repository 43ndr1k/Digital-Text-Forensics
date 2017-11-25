package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.File;

public class XMLFileFilter {
	   public boolean accept(File pathname) {
		      return pathname.getName().toLowerCase().endsWith(".xml");
		   }
}
