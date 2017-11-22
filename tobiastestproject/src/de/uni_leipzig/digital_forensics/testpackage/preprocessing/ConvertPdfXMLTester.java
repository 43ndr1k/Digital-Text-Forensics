package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ConvertPdfXMLTester {
	static String dataDirPath =  "/home/tobias/Dokumente/authorship-material-stathis/";
	
	
	/**
	 * 
	 */
	public static void runPreproccessing() throws IOException {
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
				String outputPath = "~/Dokumente/xmlOutput/"+ outputName;
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
	
	/**
	 * @TODO if statement to check if title equals "No proper data found."
	 */
	public void fixNames() {
		ConvertPdfXML converter = new ConvertPdfXML();

		File[] files = new File(dataDirPath).listFiles();
		XMLFileFilter filter = new XMLFileFilter();
		ConvertPdfXML myconverter = new ConvertPdfXML();
	      for (File file : files) {
	         if(!file.isDirectory()
	            && !file.isHidden()
	            && file.exists()
	            && file.canRead()
	            && filter.accept(file)
	         ){
	        	 Article article = myconverter.getArticleFromXML(file);
	        	 // check if title ok
	        	 // maybe change it
	        	 //..
	        	 try {
		        	 // and write it back
					converter.writeToXML(article,file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
	         }
	      }
	}// end of function
	
	public static void main(String argv[]) throws IOException {
		//runPreproccessing();
		ConvertPdfXML converter = new ConvertPdfXML();
		File testFile = new File("/home/tobias/Dokumente/xmlOutput/aldebei-diarization-2015.xml");
		converter.insertIndexerDOCID("newDoi", testFile);
	}
}
