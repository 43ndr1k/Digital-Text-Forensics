package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
// import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.docear.pdf.PdfDataExtractor;


import de.uni_leipzig.digital_text_forensics.preprocessing.MyNameGetter;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ConvertPdfXML {
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	static int MIN_TITLE_LENGTH = 3;
	static int MAX_TITLE_LENGTH = 500;
	static int TITLE_LIKE_LENGTH = 200;
	static int AUTHOUR_SEARCH_LENGTH = 500;
	static String NO_ENTRY = "No proper data found.";
	// -> List!
	static String BLOCK_WORDS[] = {".pdf",".doc",".dvi","title","Chapter","rights reserved","Article", "http"};
	public String outputPath;
	
	public ConvertPdfXML(String outputPath) {
		this.outputPath = outputPath;
	}
	/**
	 * 
	 * @param field_string
	 * @return
	 */
	private String clean_field(String field_string){
		String new_title = null;
		if (field_string == null) {
			return NO_ENTRY;
		}
		if(!field_string.matches("[A-Za-z0-9]+") && field_string.trim().length() > MIN_TITLE_LENGTH) {
		    new_title = field_string;
		} else {
			return NO_ENTRY;
		}
		if (field_string.length()==0){
			return NO_ENTRY;
		}
		for (String word : BLOCK_WORDS) {
			if (field_string.contains(word)) {
				return NO_ENTRY;
			}
		}
		String trimmed_new_title = new_title.replaceAll("[^\\p{L}\\p{Z}]","").trim();
		if (trimmed_new_title.length()==0){
			return NO_ENTRY;
		} else {
			return trimmed_new_title.replaceAll("\\s+", " ");
		}
		
	}
	
	/** extracts first page and runs a NE-recognition. 
	 *  with pdfbox
	 * @param stripper to extract first page of doc
	 * @param doc 
	 * @return String authors
	 * @throws IOException 
	 */
	private String extractAuthors(PDFTextStripper stripper, PDDocument doc) throws IOException{

		stripper.setStartPage(0);
		stripper.setEndPage(1);
		
		String firstPage= stripper.getText(doc);
		String pdfbeginning = firstPage.substring(0, Math.min(firstPage.length(), 500));
		MyNameGetter nameFinder = new MyNameGetter();
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
	
	/**
	 * 
	 * @param doc
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

		Element pubDate = doc.createElement("publicationDate");
		pubDate.appendChild(doc.createTextNode(article.getPublicationDate()));
		metaDataElement.appendChild(pubDate);

		Element textElements = doc.createElement("textElements");
		rootElement.appendChild(textElements);
		Element articleAbstract = doc.createElement("abstract");
		articleAbstract.appendChild(doc.createTextNode("Dummy. Please Implement."));
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
				System.out.println(outputFile);
			}
		}
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (java.lang.NullPointerException npr) {
			npr.printStackTrace();
		}
	} // end of function
	
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String getFieldDocearStyle(File file)  {
		boolean empty = true;
		StringBuilder sb = new StringBuilder();
		PdfDataExtractor extractor = new PdfDataExtractor(file);
		try {
			if (!empty) {
				sb.append("|");
			}
			try {

				String title = extractor.extractTitle();
				// also possible here.
				//System.out.println(extractor.extractPlainText());
				if (title != null) {
					sb.append(clean_field(title));
					empty = false;
				}
			}
			catch (IOException e) {
				sb.append(NO_ENTRY);
			}
		}
		finally {

			extractor.close();
			extractor = null;
		}
		return sb.toString();
	}
	

	/**extracts first page and runs a NE-recognition. 
	 * => tika
	 * 
	 * @param handler
	 * @return
	 * @throws IOException
	 */
	private String extractAuthors(BodyContentHandler handler) throws IOException{

		String pdftext = handler.toString();
	    String pdfbeginning = pdftext.substring(0, Math.min(pdftext.length(), AUTHOUR_SEARCH_LENGTH));

		MyNameGetter nameFinder = new MyNameGetter();
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
	
	/** Converts PDF file to XML-data
	 *  - First try to extract meta-data with pdfbox. If this fails use name-entity-recognition
	 *  and similar methods.
	 *  - writing in writeToXML
	 *  
	 *  TODO: take result with highest score. (DBLP)
	 * 
	 * @param file
	 * @param docId
	 * @throws IOException
	 */
	public void runWithTika(File file, int docId) throws IOException{
		DBLPDataAccessor da = new DBLPDataAccessor();
		
        LocalDateTime now = LocalDateTime.now();	
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String parseTime = now.format(formatter);
        
		try { 

			String originalFilename = file.getName();
			String outputFileName = originalFilename.substring(0, originalFilename.length()-".pdf".length())+".xml";
			String outputFilePath = this.outputPath+ outputFileName;			
			String title = null;
			String author = null;
			String pubDateString = null;
			
			/*--------------------------------------------------------
			 * get doc with tika
			 *--------------------------------------------------------*/
		      BodyContentHandler handler = new BodyContentHandler(-1);  // -1 ~ disable limit
		      Metadata metadata = new Metadata();
		      FileInputStream inputstream = new FileInputStream(file);
		      ParseContext pcontext = new ParseContext();
		      
		      PDFParser pdfParser = new PDFParser(); 
			try {
				pdfParser.parse(inputstream, handler, metadata, pcontext);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (TikaException e) {
				e.printStackTrace();
			}

			
			/*----------------------------------------------
			 * get & set the title
			 *----------------------------------------------*/
			String tikaTitle = clean_field(metadata.get("title"));
			Boolean success = false;
			if (tikaTitle.equals(NO_ENTRY) || (tikaTitle.length()==0)) {
				success = false;
				// try again with docear. if this fails success is left to false
				// and we take the titleLike (first characters as defined in 
				// TITLE_LIKE_LENGTH
				String secondTryTitle = clean_field(getFieldDocearStyle(file).trim());
				if (!secondTryTitle.equals(NO_ENTRY)) {
					title = secondTryTitle;
					success = true;
				} // else is handled like discussed with "titleLike"
				
			} else {
				title = tikaTitle;
				success = true;
			}
			Article article = null;
			if (success) {
				/*----------------------------------------------
				 * only use DBLP if we have an OK title
				 *----------------------------------------------*/
				try{
					article = da.getArticleObj(title);
				} catch(java.net.UnknownHostException uhe){
					System.out.println(file.getName());
				}
			}
			if (article != null) {
				// wenn er was findet, dann nimm das auch: natuerlich fehleranfaellig.
				// idee: nimm das mit dem hoechsten score. TODO
				List<String> authors = article.getAuthors();
				StringBuilder sb = new StringBuilder();
				for (int i=0;i<authors.size();i++){
					sb.append(authors.get(i));
				}
				author = sb.toString();
				pubDateString = article.getPublicationDate();
				
			} else {
				article = new Article();
				/*----------------------------------------------
				 * if there aren't any results:
				 *----------------------------------------------*/
				 
				String first_try_author = clean_field(metadata.get("Author"));
				if (first_try_author.equals(NO_ENTRY)
						|| (first_try_author.length()==0)) {
					// try to extract name with NE in first section.
					String second_try_author = clean_field(extractAuthors(handler));
					
					if (second_try_author.equals(NO_ENTRY) 
							|| (first_try_author.length()==0)) {
						author = NO_ENTRY;
					} else {
						author = second_try_author;
					}
				} else {
					author = first_try_author;
				}
				
				try{
					pubDateString = metadata.get("created");
				}
				catch (java.lang.NullPointerException npe) {
					pubDateString = NO_ENTRY;
				}
			}
			
			String fullText = handler.toString();
			inputstream.close();
			
			//\"§$%&/()=\\ß{}[]€@]
			// \\p{Punct}
			//fullText = Normalizer.normalize(fullText, Normalizer.Form.NFD);
			//fullText = fullText.replaceAll("[^A-Za-z0-9] ","").replace("\\s+", "+");//p{Cntrl}
			fullText = stripNonValidXMLCharacters(fullText);
			fullText = fullText.replaceAll("\\p{C}", " ");
			if (fullText.trim().length() == 0) {
				fullText = "Incompatible Encoding";
			}
			/**
			 * If we want to add stemmed words.
			 */
			//String stemmedWords = getDistinctStemmedWords(handler);

			if (!success){
				title = fullText.substring(0, Math.min(fullText.length(), 200));
			}
			article.setTitle(title);
			article.setAuthors(author.trim());
			article.setPublicationDate(pubDateString);
			article.setMyAbstract("Dummy. Please Implement.");
			article.setFileName(originalFilename);
			article.setFilePath(outputFilePath);
			article.setDoi(String.valueOf(docId));
			article.setParseDate(parseTime.replace(" ", "T"));
			article.setFullText(fullText);
			
			writeToXML(article,outputFilePath);

		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (java.lang.NullPointerException npe){
			System.out.println(file.getName());
		}
	} // end of run-tika-function
	
	/** to add stemmed words to fulltext or seperate tag.
	 * 
	 * @param handler
	 * @return
	 */
	private String getDistinctStemmedWords(BodyContentHandler handler) {
		 SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;  
	     String tokens[] = simpleTokenizer.tokenize(handler.toString());  
		PorterStemmer pt = new PorterStemmer();
		Set<String> stemmedTokens = new HashSet<String>();
		for (String token : tokens) {
			// only take the set of stemmed words 
			// don't take numbers, signs etc.
			stemmedTokens.add(pt.stem(token));
		}
		 
		return  String.join(" ", stemmedTokens);
	}
	/** Converts PDF file to XML-data
	 *  - First try to extract meta-data with pdfbox. If this fails use name-entity-recognition
	 *  and similar methods.
	 *  - writing in writeToXML
	 *  TODO: take result with highest score. (DBLP)
	 *  
	 * @param file
	 * @param id
	 */
	public void run(File file, int id) throws IOException{
		DBLPDataAccessor da = new DBLPDataAccessor();
		try { 
			LocalDateTime now = LocalDateTime.now();	
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        String parseTime = now.format(formatter);
			String originalFilename = file.getName();
			String outputFileName = originalFilename.substring(0, originalFilename.length()-".pdf".length())+".xml";
			String outputFilePath = this.outputPath+ outputFileName;			
			String title = null;
			String author = null;
			String pubDateString = null;
			
			/*--------------------------------------------------------
			 * get doc with pdfbox
			 *--------------------------------------------------------*/
			PDDocument myDoc = null;
			try {
				myDoc = PDDocument.load(file);
			} catch(Exception e){
				e.printStackTrace();
			}  
			PDFTextStripper stripper = null;
			try {
				stripper = new PDFTextStripper();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*----------------------------------------------
			 * get & set the title
			 *----------------------------------------------*/
			String pdfbox_title = clean_field(myDoc.getDocumentInformation().getTitle());
			Boolean success = false;

			if (pdfbox_title.equals(NO_ENTRY) || (pdfbox_title.length()==0)) {
				String secondTryTitle = clean_field(getFieldDocearStyle(file).trim());
				success = false;
				if (!pdfbox_title.equals(NO_ENTRY)) {
					title = secondTryTitle;
					success = true;
				} // else is handled like discussed with "titleLike"
			
			} else {
				title = pdfbox_title;
				success = true;
			}

			Article article = null;
			if (success) {
				/*----------------------------------------------
				 * only use dblp if we have an ok title
				 *----------------------------------------------*/
				try{
					article = da.getArticleObj(title);
				} catch(java.net.UnknownHostException uhe){
					System.out.println(file.getName());
				}
				
			}
			if (article != null) {
				// wenn er was findet, dann nimm das auch: natuerlich fehleranfaellig.
				// idee: nimm das mit dem hoechsten score. TODO
				List<String> authors = article.getAuthors();
				StringBuilder sb = new StringBuilder();
				for (int i=0;i<authors.size();i++){
					sb.append(authors.get(i));
				}
				author = sb.toString();
				pubDateString = article.getPublicationDate();
				
			} else {
				article = new Article();
				/*----------------------------------------------
				 * if there aren't any results:
				 *----------------------------------------------*/
				String first_try_author = clean_field(myDoc.getDocumentInformation()
						.getAuthor());
				if (first_try_author.equals(NO_ENTRY)
						|| (first_try_author.length()==0)) {
					// try to extract name with NE in first section.
					String second_try_author = clean_field(extractAuthors(stripper, myDoc));
					
					if (second_try_author.equals(NO_ENTRY) 
							|| (first_try_author.length()==0)) {
						author = NO_ENTRY;
					} else {
						author = second_try_author;
					}
				} else {
					author = first_try_author;
				}
				
				try{
					pubDateString = myDoc.getDocumentInformation()
							.getCreationDate().getTime().toString();
				}
				catch (java.lang.NullPointerException npe) {
					pubDateString = NO_ENTRY;
				}
			}

			
			String fullText = null;
			try {
				/*----------------------------------------------
				 * get & set the main text
				 *----------------------------------------------*/
				stripper.setStartPage(0);
				stripper.setEndPage(Integer.MAX_VALUE);
				fullText =  stripper.getText(myDoc);
			} catch (IOException e) {
				System.out.println(file.getName());
			} catch (java.lang.NullPointerException npe){
				System.out.println(file.getName());
			}
			myDoc.close(); // close doc
			
			//\"§$%&/()=\\ß{}[]€@]
			// \\p{Punct}
			// This is where I try to remove/fix special characters
			//fullText = Normalizer.normalize(fullText, Normalizer.Form.NFD);
			//fullText = fullText.replaceAll("\\p{C}", " ");
			//fullText = fullText.replaceAll("[^A-Za-z0-9] ","").replace("\\s+", "+");//p{Cntrl}
			fullText = stripNonValidXMLCharacters(fullText);
			if (!success){
				title = fullText.substring(0, Math.min(fullText.length(), 200));
			}
			article.setTitle(title);
			article.setAuthors(author);
			article.setPublicationDate(pubDateString);
			article.setMyAbstract("Dummy. Please Implement.");
			article.setFileName(originalFilename);
			article.setFilePath(outputFilePath);
			article.setDoi(String.valueOf(id));
			article.setParseDate(parseTime.replace(" ", "T"));
			article.setFullText(fullText);
			
			// finally write the article to xml.
			writeToXML(article,outputFilePath);

		} catch (DOMException e1) {
			e1.printStackTrace();
		} catch (java.lang.NullPointerException npe){
			System.out.println(file.getName());
		}



	} // end of function
	
	/** There are some characters which should not occur in a cdata section
	 *  these are filtered out in this function. Probably there is a more efficient
	 *  solution.
	 *  http://blog.mark-mclaren.info/2007/02/invalid-xml-characters-when-valid-utf8_5873.html
	 * @param in
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
	 * gets the index created in the indexing process and corresponding file,
	 * i.e. xmlFiles/document.xml. then
	 * 1) read xml with sax-parser -> maybe i can do this without building a
	 * new xml-file. 
	 * 2) alter doi 
	 * 3) write doc back.
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
	
	/**
	 * 
	 * @param file
	 * @return
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
	
} // end of class
