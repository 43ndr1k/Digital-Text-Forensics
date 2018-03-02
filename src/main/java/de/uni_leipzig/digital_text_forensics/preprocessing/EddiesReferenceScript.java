package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.IOException;

public class EddiesReferenceScript {
	
	public static void main(String[] args)  {
		try {
			Runtime.getRuntime().exec("perl D:\\PerlProgrammierung\\6UnitedComparer.pl");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}



    
