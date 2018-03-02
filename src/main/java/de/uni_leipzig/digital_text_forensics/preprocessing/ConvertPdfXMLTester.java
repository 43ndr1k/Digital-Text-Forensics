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
public class ConvertPdfXMLTester {
	static String dataDirPath =  "pdfDocs/";
	static String xmlFilePath = "xmlFiles/";
	static int my_counter;
	private static ConvertPdfXML converter;
	
	/**
	 * <p> Function can be called to walk through a given Directory and convert
	 * Pdf-Files to XML-Files. If a File is already existing, skip it.
	 * @param skipExisting
	 * 
	 * @throws IOException
	 */
	public static void runPreproccessing(Boolean skipExisting) throws IOException {
        		
		File[] files = new File(dataDirPath).listFiles();
		PdfFileFilter filter = new PdfFileFilter();
		
		converter = new ConvertPdfXML();
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
	        	String data = "\r" + anim.charAt(index % anim.length()) + " " + 100*(float)index/fileNumber;
			    System.out.println(data);
	            try {
					converter.run(file,index); // conversion step.
				} catch (IOException e) {
					//e.printStackTrace();
				} 
	            //break;

	         }
	      }
	      
	      /**
	       * Print out some stats. Better: Logger.
	       */
	      System.out.println("dblp\t"+converter.dblpTitle_i);
	      System.out.println("titleLike\t"+converter.titleLike_i);
	      System.out.println("pdfbox\t"+converter.pdfbox_i);
	      System.out.println("docear\t"+converter.docear_i);

	}
	
	/**
	 * Can be used to modify or experiment on XML data. 
	 */
	public static void repairXML() {
		File[] files = new File(xmlFilePath).listFiles();
		XMLFileFilter filter = new XMLFileFilter();
		ConvertPdfXML myconverter = new ConvertPdfXML();
		WordOperationClass wordOps = new WordOperationClass();
		String outputFolder = "/home/tobias/Dokumente/Information Retrieval/xmlFiles-Versions/xmlNewTrial/";
		
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists()
					&& file.canRead() && filter.accept(file)) {
				
				Article article = null;
				article = myconverter.getArticleFromXML(file);

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
						myconverter.writeToXML(article, outputFile);
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
	public static void mergeRefCountsToXMLFiles (File file){
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
	    
	    ConvertPdfXML myconverter = new ConvertPdfXML();
	    for (RefCountObj rc: refCounts){
	    	Article article = myconverter.getArticleFromXML(new File("xmlFiles/"+rc.getFileName()));
	    	System.out.println(rc.getFileName()+" "+ rc.getCounter());
	    	if (article != null) {
	    		article.setRefCount(rc.getCounter());
				//myconverter.writeToXML(article, "xmlFiles/"+rc.getFileName());
	    	}
	    }
		
	} // end of getRefCounteFromXML
	
	public static void main(String argv[]) throws IOException {

		//mergeRefCountsToXMLFiles(new File("/home/tobias/Dokumente/Information Retrieval/final.xml"));
		System.out.println(xmlFilePath);
		my_counter = 0;
		
		repairXML();
		System.out.println(my_counter);
		//runPreproccessing(false); // param: skip existing
		
		
		System.out.println("ready");
	}
}
