package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ConvertPdfXMLTester {
	public static void main(String argv[]) throws IOException {
		String dataDirPath = "/home/tobias/Dokumente/authorship-material-stathis/";
		File[] files = new File(dataDirPath).listFiles();
		PdfFileFilter filter = new PdfFileFilter();
		ConvertPdfXML converter = new ConvertPdfXML();
		Writer output = new BufferedWriter(new FileWriter("missed.log", true));
	      int index = 0;
	      int fileNumber = files.length;
	      String anim= "|/-\\";

	      
	      for (File file : files) {
	         if(!file.isDirectory()
	            && !file.isHidden()
	            && file.exists()
	            && file.canRead()
	            && filter.accept(file)
	         ){
	 			String outputName = file.toString().substring(0, file.toString().length()-".pdf".length())+".xml";
				String outputPath = file.getParentFile().getParent()+"/xmlOutput/"+ outputName;
				// scheint nicht zu funktionieren.
				File f = new File(outputPath);
				if(f.exists() && !f.isDirectory()) { 
				    continue;
				}
				
	        	 String data = "\r" + anim.charAt(index % anim.length()) + " " + 100*(float)index/fileNumber;
			    System.out.println(data);
	            try {
					converter.run(file,index);
				} catch (IOException e) {
					e.printStackTrace();
					output.append("\t"+file.toString()+"\n");
				} 

	            index++;
	         }
	      }
		output.close();
	}
}
