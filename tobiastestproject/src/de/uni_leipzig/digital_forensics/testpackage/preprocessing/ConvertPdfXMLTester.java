package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.File;
import java.io.IOException;

public class ConvertPdfXMLTester {
	static String dataDirPath =  "/home/tobias/Dokumente/authorship-material-stathis/";
	static String xmlFilePath = "/home/tobias/mygits/Digital-Text-Forensics/xmlFiles";
	
	/**
	 * 
	 */
	public static void runPreproccessing() throws IOException {
		File[] files = new File(dataDirPath).listFiles();
		PdfFileFilter pdfFilter = new PdfFileFilter();
		DocFileFilter docFilter = new DocFileFilter();
		ConvertPdfXML converter = new ConvertPdfXML();
	      int index = 0;
	      int fileNumber = files.length;
	      String anim= "|/-\\";
	      for (File file : files) {
	         if(!file.isDirectory()
	            && !file.isHidden()
	            && file.exists()
	            && file.canRead()
	            && pdfFilter.accept(file) || docFilter.accept(file)
	         ){
	        	 // also works for doc and HTM
	 			String outputName = file.getName().toString().substring(0, file.getName().toString().length()-".pdf".length())+".xml";
				String outputPath = "/home/tobias/mygits/Digital-Text-Forensics/xmlFiles/"+ outputName;
				File f = new File(outputPath);
				index++;
				if(f.exists() && !f.isDirectory()) { 
				    continue;
				}
	        	 String data = "\r" + anim.charAt(index % anim.length()) + " " + 100*(float)index/fileNumber;
			    System.out.println(data);
	            try {
					//converter.run(file,index);
	            	converter.runWithTika(file, index);
				} catch (IOException e) {
					e.printStackTrace();
				} 

	         }
	      }
	}
	
	/**
	 * Can be used to modify xml data
	 */
	public static void fixNames() {

		File[] files = new File(xmlFilePath).listFiles();
		XMLFileFilter filter = new XMLFileFilter();
		ConvertPdfXML myconverter = new ConvertPdfXML();
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists()
					&& file.canRead() && filter.accept(file)) {
				Article article = null;

				article = myconverter.getArticleFromXML(file);

				if (article != null) {
					String newFilePath = article.getFilePath().replace(
							"/home/tobias/mygits/Digital-Text-Forensics/", "");
					article.setFilePath(newFilePath);
				}
				try {
					// and write it back
					myconverter.writeToXML(article, file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}// end of function
	
	public static void main(String argv[]) throws IOException {
		//fixNames();
		runPreproccessing();
//		ConvertPdfXML converter = new ConvertPdfXML();
//		File testFile = new File("/home/tobias/mygits/Digital-Text-Forensics/pdfDocs/meyer-2015.pdf");
//		converter.run(testFile, 0);
//		System.out.println("ready");
	}
}
