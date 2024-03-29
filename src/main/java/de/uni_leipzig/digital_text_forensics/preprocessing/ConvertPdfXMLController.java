package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


/**
* ConvertPdfXMLTester Model
* <p> To run preprocessing.
*  
* @author Tobias Wenzel
* 
*/
public class ConvertPdfXMLController {
	static String dataDirPath =  "pdfDocs/";
	static String xmlFilePath = "xmlFiles/";
	static int my_counter;
	private static ConvertPdfXML converter;
	


	
	
	public ConvertPdfXMLController() {
		converter = new ConvertPdfXML();
		
		File dataDir = new File(dataDirPath);
		File xmlFiles = new File(xmlFilePath);
		if (!dataDir.exists()) {
			dataDir.mkdir();
			System.out.println("Create Folder: "+ dataDirPath);
		}
		
		if (!xmlFiles.exists()) {
			xmlFiles.mkdir();
			System.out.println("Create Folder:"+ xmlFilePath);
		} 
		
		
	}
	/**
	 * <p> Function can be called to walk through a given Directory and convert
	 * Pdf-Files to XML-Files. If a File is already existing, skip it.
	 * @param skipExisting
	 * 
	 * @throws IOException
	 */
	public void runPreproccessing(Boolean skipExisting) throws IOException {
        		
		File[] files = new File(dataDirPath).listFiles();
		PdfFileFilter filter = new PdfFileFilter();
		
		
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
	 			String outputFileName = file.getName().toString().substring(0, file.getName().toString().length()-".pdf".length())+".xml";
				String outputFilePath = xmlFilePath+ outputFileName;
				File f = new File(outputFilePath);
				index++;
				
				if (skipExisting) {
					if(f.exists() && !f.isDirectory()) { 
					    continue;
					}
				}
	        	//	String data = "\r" + anim.charAt(index % anim.length()) + " " + 100*(float)index/fileNumber;
			    //System.out.println(data);
	            try {
					converter.run(file,index); // conversion step.
				} catch (IOException e) {
					//e.printStackTrace();
				} 
	            //break;

	         }
	      }
	      


	}
	
	/**
	 * Can be used to modify or experiment on XML data. 
	 */
	public void repairXML() {
		File[] files = new File(xmlFilePath).listFiles();
		XMLFileFilter filter = new XMLFileFilter();
		WordOperationClass wordOps = new WordOperationClass();
		String outputFolder = "~/Dokumente/Information Retrieval/xmlFiles-Versions/xmlNewTrial/";
		
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists()
					&& file.canRead() && filter.accept(file)) {
				
				Article article = null;
				article = converter.getArticleFromXML(file);

				if (article != null) {
		 			String filename = article.getFilePath().substring("xmlFiles/".length(),
 					article.getFilePath().length());
		 			String outputFile = new String(outputFolder+filename);

		 			/**
		 			 * Counting non-existing titles.
		 			 */
					//		 			String title = article.getTitle().trim();
					//		 			if (title.trim().equals("") == true){
					//		 				title = "No proper data found";
					//		 				my_counter++;
					//		 				//continue;
					//		 			}
		 			
		 			/**
		 			 * Clean title-string. 
		 			 */
					//		 			if (title != null){
					//		 				title = wordOps.clean_field(title, true).trim();
					//		 				article.setTitle(title);
					//		 			}
		 			
		 			/**
		 			 * Authors.
		 			 */
		 			/**
		 			 * add points after single characters.
		 			 */
					String authors = article.getAuthorsAsString();
					//		 			String[] tokens = authors.split("(?=\\p{Lu})");//.split("\\s+");
					//		 			StringBuilder ab = new StringBuilder();
					//		 			for (String token : tokens){
					//		 				if (token.trim().length()==1){
					//		 					ab.append(token.trim()+". ");
					//		 					continue;
					//		 				}
					//		 				ab.append(token.trim()+" ");
					//		 			}
					//		 			
					//		 			System.out.println(ab.toString());
		 			
		 			//article.setAuthors(ab.toString());

					//					if (!authors.trim().equals("No proper data found")){
					//						article.setAuthors("No proper data found.");
					//					}
		 			
		 			/**
		 			 * Add stemmed text.
		 			 */
					//					String stemmedText = wordOps.getStemmedWords(wordOps
					//							.clean_field(article.getFullText(),true)).toLowerCase();
					//					article.setFullText(article.getFullText()+"\n"+stemmedText);
		 			
		 			/**
		 			 * Adjust references count.
		 			 */
					//					String rc = article.getRefCount();
					//					article.setRefCount(rc);
		 			

		 			/**
		 			 * Change date-fomat
		 			 */
					String pubDate = article.getPublicationDate();
					if (pubDate.equals("No proper data found.")){
						String possibleYear = article.getFileName().replaceAll("\\D+","");
						if (possibleYear.length() == 4){
							article.setPublicationDate(possibleYear);
						} else {
							article.setPublicationDate("");
						}
						/**
						 * Write back.
						 */
						converter.writeToXML(article, outputFile);
						System.out.println(article.getPublicationDate());
					}
					//		 			String newDate = converter.changeDataFormat(pubDate);
					//		 			if (newDate != null)
					//		 				article.setPublicationDate(newDate);
		 			

					/**
					 * Abstract.
					 */
					//					String my_abstract = article.getMyAbstract().replaceAll("\\s+$", "");
					//					if (my_abstract.equals("") || my_abstract == null){
					//						my_counter++;
					//						//System.out.println(my_counter);
					//					}
					
					/**
					 * Get Abstract from fulltext.
					 */
					//					String fullText = article.getFullText();
					//					String firstLowerCaseWords = wordOps.getNWords(fullText,500).toLowerCase();
					//					String result = null;
					//					try {
					//						result = firstLowerCaseWords.substring(firstLowerCaseWords.indexOf("abstract") + 9,
					//								firstLowerCaseWords.indexOf("introduction"));
					//					} catch(java.lang.StringIndexOutOfBoundsException e){
					//						
					//					}
					//					if (result != null){
					//						//System.out.println(result);
					//						article.setMyAbstract(wordOps.getNWords(result,150));
					//					}

					

				}
			}
		}
	}// end of function
	
	/**
	 * Reads xml-file with reference counts corresponding to
	 * article objects and inserts them.
	 * 
	 * @param file
	 */
	private void mergeRefCountsToXMLFiles (File file){
		List<RefCountObj> refCounts = null;
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	    try {
	        SAXParser saxParser = saxParserFactory.newSAXParser();
	        RefCountEntryHandler handler = new RefCountEntryHandler();
	        saxParser.parse(file, handler);
	        refCounts = handler.getRefCountObjs();
	    } catch (ParserConfigurationException pce){
	    	pce.printStackTrace();
	    } catch(SAXException sae) {
	    	sae.printStackTrace();
	    }catch ( IOException ioe) {
	    	ioe.printStackTrace();
	    } 
	    
	    if (refCounts != null) {
		    for (RefCountObj rc: refCounts){
		    	Article article = converter.getArticleFromXML(new File("xmlFiles/"+rc.getFileName()));
		    	//System.out.println(rc.getFileName()+" "+ rc.getCounter());
		    	if (article != null) {
		    		article.setRefCount(rc.getCounter());
					converter.writeToXML(article, "xmlFiles/"+rc.getFileName());
		    	}
		    }
	    }
		
	} // end of getRefCounteFromXML
	
	/**
	 * Runs script in perl to determine the amount each paper is cited.
	 */
	private void runPerlScript() {
		Process process;
		try {
			process = Runtime.getRuntime().exec(new String[] {"perl", System.getProperty("user.dir")+"/Skripte/refCountScript.pl"});		    
		    process.waitFor();
		    if(process.exitValue() == 0) {
		        System.out.println("Command Successful");
		    } else {
		        System.out.println("Command Failure");
		    }
		} catch(Exception e) {
		    System.out.println("Exception: "+ e.toString());
		}
		
	}
	
	/**
	 * Calls perl script and merges the output with the existing XML-files.
	 */
	public void runRefCountAnalysis() {
		this.runPerlScript();
		this.mergeRefCountsToXMLFiles(new File(System.getProperty("user.dir")+"/Skripte/output/final.xml"));
	}
	
	

}
