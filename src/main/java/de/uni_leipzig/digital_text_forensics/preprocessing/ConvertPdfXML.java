package de.uni_leipzig.digital_text_forensics.preprocessing;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// might not be needed later.
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

/**
* ConvertPdfXML
* <p> Converts Pdf-Files to XML-Files and provides an Interface
* to load XML-Files into Article-Objects (see Article.java).
*  
* @author Tobias Wenzel
* 
*/
public class ConvertPdfXML {
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private WordOperationClass wordOps;
	private DateFormat formatter, snd_formatter;
	
	
	static String NO_ENTRY = "No proper data found.";

	public String outputPath;
	private int titleLikeLength;

	public int docear_i,pdfbox_i,titleLike_i, dblpTitle_i;
	
	/** <b> Constructor </b>
	 * 
	 * <p> It shouldn't be necessary to set the Output-Path since it's always the same.
	 * Never the less it's possible for testing purposes.
	 * 
	 */
	public ConvertPdfXML() {		
		setOutputPath("xmlFiles/");
		this.wordOps = new WordOperationClass();
		
		this.formatter = new SimpleDateFormat("E MMM dd HH:mm:ss 'CEST' yyyy",Locale.ENGLISH);
		this.snd_formatter = new SimpleDateFormat("E MMM dd HH:mm:ss 'CET' yyyy",Locale.ENGLISH);
		
		titleLikeLength = 50;
		docear_i = 0;
		pdfbox_i = 0;
		titleLike_i = 0;
		dblpTitle_i = 0;
	}
	
	public void setOutputPath(String outputPath){
		this.outputPath = outputPath;
	}
	
	/** <p> Extracts the first page of a Doc and runs a NE-recognition. 
	 *  with <b>pdfbox</b>
	 *  
	 * @param stripper to extract first page of doc
	 * @param doc with actual data
	 * @return String containing authors
	 * @throws IOException 
	 */
	private String extractAuthors(PDFTextStripper stripper, PDDocument doc) throws IOException{

		stripper.setStartPage(0);
		stripper.setEndPage(1);
		
		String firstPage= stripper.getText(doc);
		String pdfbeginning = wordOps.getNWords(firstPage, 300);
		
		
		WordOperationClass nameFinder = new WordOperationClass();
		try {
			List<String> myNames = nameFinder.findName(pdfbeginning);
			if (myNames.isEmpty()){
				return NO_ENTRY;
			}
			else{
				if (myNames.size() == 1){
					return myNames.get(0);
				} else {
					return StringUtils.join(myNames, ", ");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e){
			return NO_ENTRY;
		}
		return NO_ENTRY;

}
	
	/** <p> Writes Article Object to XML-File.
	 * 
	 * @param article with Data
	 * @param outputFile
	 */
	public void writeToXML(Article article, String outputFile) {
	    this.docFactory = DocumentBuilderFactory.newInstance();
		try {
			this.docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = this.docBuilder.newDocument();

		Element rootElement = doc.createElement("article");
		doc.appendChild(rootElement);
		Element metaDataElement = doc.createElement("metaData");
		rootElement.appendChild(metaDataElement);

		metaDataElement.setAttribute("docId", article.getDoi());
		Element parseTimeElement = doc.createElement("parseTime");
		parseTimeElement.appendChild(doc.createTextNode(article.getParseDate()));
		metaDataElement.appendChild(parseTimeElement);
		
		Element fileNameElement = doc.createElement("fileName");
		fileNameElement.appendChild(doc.createTextNode(article.getFileName()));
		metaDataElement.appendChild(fileNameElement);
		
		Element filePathElement = doc.createElement("filePath");
		filePathElement.appendChild(doc.createTextNode(article.getFilePath()));
		metaDataElement.appendChild(filePathElement);

		Element titleElement = doc.createElement("title");
		titleElement.appendChild(doc.createTextNode(article.getTitle()));
		metaDataElement.appendChild(titleElement);		
		
		Element authorElement = doc.createElement("authors");
		authorElement.appendChild(doc.createTextNode(article.getAuthorsAsString()));
		metaDataElement.appendChild(authorElement);
		
		Element refCountElement = doc.createElement("refCount");
		refCountElement.appendChild(doc.createTextNode(article.getRefCount()));
		metaDataElement.appendChild(refCountElement);

		Element pubDate = doc.createElement("publicationDate");
		pubDate.appendChild(doc.createTextNode(article.getPublicationDate()));
		metaDataElement.appendChild(pubDate);

		Element textElements = doc.createElement("textElements");
		rootElement.appendChild(textElements);
		Element articleAbstract = doc.createElement("abstract");
		articleAbstract.appendChild(doc.createCDATASection(article.getMyAbstract()));
		textElements.appendChild(articleAbstract);
		
		Element fullTextElement = doc.createElement("fullText");
		fullTextElement.appendChild(doc.createCDATASection(article.getFullText()));
		textElements.appendChild(fullTextElement);
		
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputFile));
			try{
			transformer.transform(source, result);
			}	catch (javax.xml.transform.TransformerException npr) {
				npr.printStackTrace();
			}
		}
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (java.lang.NullPointerException npr) {
			npr.printStackTrace();
		}
	} // end of function
	

	public void run_from_controller(String filename) {
		try {
			this.run(new File(filename), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** <p>Converts PDF file to XML-data
	 *  <li> First try to extract meta-data with <b>pdfbox</b>. If this fails use name-entity-recognition
	 *  and similar methods.
	 *  <li> writing in writeToXML
	 *  
	 *  
	 * @param file
	 * @param id as dummy-data. Can also be real docid. 
	 */
	public void run(File file, int id) throws IOException{

		DBLPDataAccessor da = new DBLPDataAccessor();
		Article article = null;
		LocalDateTime now = LocalDateTime.now();	
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String parseTime = now.format(formatter);
		String originalFilename = file.getName();
		String outputFileName = originalFilename.substring(0, originalFilename.length()-".pdf".length())+".xml";
		String outputFilePath = this.outputPath+ outputFileName;	
		
		
		try { 
			
			String title = null;
			String author = null;
			String pubDateString = null;
			String pdfbox_title = null;
			String pdfbox_author = null;
			String namedEntityRecAuthor = null;
			String fullText = null;

			Boolean success = false;
			
			PDDocument myDoc = null;
			PDFTextStripper stripper = null;
			try { 
				/*--------------------------------------------------------
				 * get doc with pdfbox
				 *--------------------------------------------------------*/
				myDoc = PDDocument.load(file);
				stripper = new PDFTextStripper();

				pdfbox_title = wordOps.clean_field(myDoc.getDocumentInformation().getTitle(), true);

				pdfbox_author = wordOps.clean_field(myDoc.getDocumentInformation()
						.getAuthor(),false);
				pubDateString = myDoc.getDocumentInformation()
						.getCreationDate().getTime().toString();
				stripper.setStartPage(0);
				stripper.setEndPage(Integer.MAX_VALUE);
				fullText =  stripper.getText(myDoc);
				fullText = stripNonValidXMLCharacters(fullText);
				
				namedEntityRecAuthor = wordOps.clean_field(extractAuthors(stripper, myDoc),false);
			}
				catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (java.lang.NullPointerException npe) {
				pubDateString = NO_ENTRY;
			} finally {
				if( myDoc != null )
				   myDoc.close();
			}

			if ((pdfbox_title.length()==0)) {
				/*----------------------------------------------
				 * CHECK TITLE
				 *----------------------------------------------*/
				title = NO_ENTRY;
				//					String docearTitle = wordOps.clean_field(getFieldDocearStyle(file).trim(), true);
				//					if (!docearTitle.equals(NO_ENTRY)) {
				//						title = docearTitle;
				//						success = true;
				//						docear_i++;
				//					}
			} else {
				title = pdfbox_title;
				pdfbox_i++;
				success = true;
			}
			if (success) {
				/*----------------------------------------------
				 * only use dblp if we have an ok title
				 *----------------------------------------------*/
				try{
					article = da.getArticleObj(title);
				} catch(java.net.UnknownHostException uhe){
				}
			} else {
				title = wordOps.clean_field(wordOps.getNWords(fullText, titleLikeLength),true);
				titleLike_i++;
			}
			
			if (article != null) {
				List<String> authors = article.getAuthors();
				StringBuilder sb = new StringBuilder();
				for (int i=0;i<authors.size();i++){
					sb.append(authors.get(i));
				}
				author = sb.toString();
				pubDateString = article.getPublicationDate();
				dblpTitle_i++;
				
			} else {
				article = new Article();
				/*----------------------------------------------
				 * CHECK AUTHORS
				 *----------------------------------------------*/
				if ((pdfbox_author.length()==0)) {
					
					if ((namedEntityRecAuthor.length()==0)) {
						author = NO_ENTRY;
					} else {
						author = namedEntityRecAuthor;
					}
				} else {
					author = pdfbox_author;
				}
			}
			
			if (!(title.length()==0)) {
				article.setTitle(title);
			} else {
				article.setTitle(wordOps.clean_field(wordOps.getNWords(fullText, titleLikeLength),true));
				titleLike_i++;
			}

			
			pubDateString = this.changeDataFormat(pubDateString);
			String firstLowerCaseWords = wordOps.getNWords(fullText,500).toLowerCase();
			
			String result = null;
			try {
				result = firstLowerCaseWords.substring(firstLowerCaseWords.indexOf("abstract") + 8,
						firstLowerCaseWords.indexOf("introduction"));
			} catch(java.lang.StringIndexOutOfBoundsException e){
				
			}
			if (result != null){
				article.setMyAbstract(wordOps.getNWords(result,150));
			} 
			
			
			article.setAuthorsString(author);
			article.setPublicationDate(pubDateString);
			article.setFileName(originalFilename);
			article.setFilePath(outputFilePath);
			article.setDoi(String.valueOf(id));
			article.setParseDate(parseTime.replace(" ", "T"));
			article.setFullText(fullText);

			

		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (java.lang.NullPointerException npe){
			//npe.printStackTrace();
			//System.out.println(file.getName());
		} 
		
		if (article != null) {
			// finally write the article to xml.
			writeToXML(article,outputFilePath);
		} else {
			// throw some exeption!
		}


	} // end of function
	
	/** Simplify date string to year or month and year. 
	 * 
	 * @param dateString
	 * @return
	 */
	public String changeDataFormat(String dateString){
		Date date = null;
		try {
			date = (Date)formatter.parse(dateString);
		} catch (java.lang.NullPointerException npe){

		} catch (java.text.ParseException e) {
			try {
				date = (Date)snd_formatter.parse(dateString);
			} catch (java.text.ParseException e1) {
				return dateString;
			}
			
		}
		if (date != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
			return month+ " " + cal.get(Calendar.YEAR);
		} else {
			return null;
		}
	}
	
	/** <p>There are some characters which should not occur in a cdata section
	 *  these are filtered out in this function. Probably there is a more efficient
	 *  solution. <br> 
	 *  Copied from {@link http://blog.mark-mclaren.info/2007/02/invalid-xml-characters-when-valid-utf8_5873.html}
	 * 
	 * @param in String with Text to be cleaned.
	 * @return
	 */
	public String stripNonValidXMLCharacters(String in) {
	    StringBuffer out = new StringBuffer(); // Used to hold the output.
	    char current; // Used to reference the current character.

	    if (in == null || ("".equals(in))) return ""; // vacancy test.
	    for (int i = 0; i < in.length(); i++) {
	        current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
	        if ((current == 0x9) ||
	            (current == 0xA) ||
	            (current == 0xD) ||
	            ((current >= 0x20) && (current <= 0xD7FF)) ||
	            ((current >= 0xE000) && (current <= 0xFFFD)) ||
	            ((current >= 0x10000) && (current <= 0x10FFFF)))
	            out.append(current);
	    }
	    return out.toString();
	}  
	
	/**
	 * <p> Gets the index created in the indexing process and corresponding file,
	 * i.e. xmlFiles/document.xml. then
	 * <li> read xml with sax-parser -> maybe i can do this without building a
	 * new xml-file. 
	 * <li> alter doi 
	 * <li> write doc back.
	 * 
	 * 
	 * @param docId
	 * @param file
	 */
	public void insertIndexerDOCID(String docId, File file) {
		Article article = getArticleFromXML(file);
		article.setDoi(docId);
		writeToXML(article,file.toString());
		
	} // end of insertIndexerDOCID
	
	/** Reads XML-Data with Sax-Parser.
	 * 
	 * @param file
	 * @return Article Object
	 */
	public Article getArticleFromXML (File file){
		Article article = null;
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	    try {
	        SAXParser saxParser = saxParserFactory.newSAXParser();
	        ArticleHandler handler = new ArticleHandler();
	        saxParser.parse(file, handler);
	        article = handler.getArticle();
	    } catch (ParserConfigurationException pce){
	    	pce.printStackTrace();
	    } catch(SAXException sae) {
	    	sae.printStackTrace();
	    }catch ( IOException ioe) {
	    	ioe.printStackTrace();
	    } 
		return article;
	} // end of getArticleFromXML
	
	
//	/**
//	 * 
//	 * @param file
//	 * @return
//	 */
//	private String getFieldDocearStyle(File file)  {
//		boolean empty = true;
//		StringBuilder sb = new StringBuilder();
//		PdfDataExtractor extractor = new PdfDataExtractor(file);
//		try {
//			if (!empty) {
//				sb.append("|");
//			}
//			try {
//
//				String title = extractor.extractTitle();
//				if (title != null) {
//					sb.append(wordOps.clean_field(title,true));
//					empty = false;
//				}
//			}
//			catch (IOException e) {
//				sb.append(NO_ENTRY);
//			} 
//			catch (de.intarsys.pdf.cos.COSSwapException cose) {
//				System.out.println("cos ex"+ file.toString());
//			}
//		}
//		finally {
//
//			extractor.close();
//			extractor = null;
//		}
//		return sb.toString();
//	}
	
} // end of class