package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.File;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.xmlbeans.impl.soap.Node;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.docear.pdf.PdfDataExtractor;

import com.google.common.base.Objects;

import de.uni_leipzig.digital_forensics.testpackage.MyNameGetter.MyNameGetter;

public class ConvertPdfXML {
	
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	static int MIN_TITLE_LENGTH = 3;
	static int MAX_TITLE_LENGTH = 500;
	static String NO_ENTRY = "No proper data found.";
	// -> List!
	static String BLOCK_WORDS[] = {".pdf",".doc",".dvi","title","Chapter","rights reserved","Article", "http"};

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
	 * 
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
		//System.out.println(article.getFullText());
		
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
			System.out.println("hey"+outputFile);
		}
	} // end of function
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private String getFieldDocearStyle(File file) {
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
			catch (de.intarsys.pdf.cos.COSSwapException e){
				sb.append(NO_ENTRY);
			}
		}
		finally {
			extractor.close();
			extractor = null;
		}
		return sb.toString();
	}
	
	/** converts pdf file to xml-data
	 *  writing in writeToXML
	 *  @todo: also accept doc and html
	 *  
	 * @param file
	 * @param id
	 */
	public void run(File file, int id) throws IOException{
		DBLPDataAccessor da = new DBLPDataAccessor();
		try { 
	        // could also be called by outer function
			LocalDateTime now = LocalDateTime.now();	
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        String parseTime = now.format(formatter);
			String originalFilename = file.getName();
			String outputName = originalFilename.substring(0, originalFilename.length()-".pdf".length())+".xml";
			String outputPath = "xmlFiles/"+ outputName;			
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
			if (Objects.equal(pdfbox_title, NO_ENTRY) || (pdfbox_title.length()==0)) {
				title = clean_field(getFieldDocearStyle(file).trim());
				// titleLike!
				//title = NO_TITLE;
			} else {
				title = pdfbox_title;
				success = true;
			}

			Article article = null;
			if (success) {
				/*----------------------------------------------
				 * only use dblp if we have an ok title
				 *----------------------------------------------*/
				article = da.getArticleObj(title);
			}
			if (article != null) {
				// wenn er was findet, dann nimm das auch: natuerlich fehleranfaellig.
				// idee: nimm das mit dem hoechsten score.
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
				if (Objects.equal(first_try_author, NO_ENTRY)
						|| (first_try_author.length()==0)) {
					// try to extract name with NE in upper text
					String second_try_author = clean_field(extractAuthors(stripper, myDoc));
					if (Objects.equal(second_try_author, NO_ENTRY) 
							|| (first_try_author.length()==0)) {
						// try to extract name with NE
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
			article.setAuthors(author);
			article.setPublicationDate(pubDateString);
			article.setMyAbstract("Dummy. Please Implement.");
			article.setFileName(originalFilename);
			article.setFilePath(outputPath);
			article.setDoi(String.valueOf(id));
			article.setParseDate(parseTime.replace(" ", "T"));
			


			String fullText = null;
			try {
				/*----------------------------------------------
				 * get & set the main text
				 *----------------------------------------------*/
				stripper.setStartPage(0);
				stripper.setEndPage(Integer.MAX_VALUE);
				fullText =  stripper.getText(myDoc);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			article.setFullText(fullText);
			// finally write the article to xml.
			writeToXML(article,outputPath);

		} catch (DOMException e1) {
			e1.printStackTrace();
		} 


	} // end of function
	
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
		writeToXML(article,"/home/tobias/Dokumente/blibla.xml");
		
	} // end of insertIndexerDOCID
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public Article getArticleFromXML(File file){
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
