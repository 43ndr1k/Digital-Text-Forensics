package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.docear.pdf.PdfDataExtractor;

import com.google.common.base.Objects;

import de.uni_leipzig.digital_forensics.testpackage.pdfbox.MyNameGetter;

public class ConvertPdfXML {
	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	
	static int MIN_TITLE_LENGTH = 5;
	// could also be used for author.
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
	
	/**
	 * 
	 * @param pdftext
	 * @return
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
	private void writeToXML(Document doc, String outputFile) {
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
			System.out.println(outputFile);
		}
	}
	
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
	
	/**
	 * 
	 * @param file
	 * @param id
	 */
	public void run(File file, int id) throws IOException{
		DBLPDataAccessor da = new DBLPDataAccessor();
		try { 
			/*
			 * Initializing of XML- Elements/ Builder  
			 */
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docBuilder = docFactory.newDocumentBuilder();
			Document doc = this.docBuilder.newDocument();

			Element rootElement = doc.createElement("article");
			doc.appendChild(rootElement);
			Element metaData = doc.createElement("metaData");
			rootElement.appendChild(metaData);
			/*
			 * @todo insert real docid
			 */
			metaData.setAttribute("docId", String.valueOf(id));
			Element fileName = doc.createElement("fileName");
			fileName.appendChild(doc.createTextNode(file.getName()));
			String originalFilename = file.getName();
			String outputName = originalFilename.substring(0, originalFilename.length()-".pdf".length())+".xml";
			String outputPath = file.getParentFile().getParent()+"/xmlOutput/"+ outputName;

			metaData.appendChild(fileName);
			Element filePath = doc.createElement("filePath");
			filePath.appendChild(doc.createTextNode(file.getCanonicalPath()));
			metaData.appendChild(filePath);
			
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
			/*--------------------------------------------------------
			 * get & set the title
			 *--------------------------------------------------------*/
			String pdfbox_title = clean_field(myDoc.getDocumentInformation().getTitle());
			if (Objects.equal(pdfbox_title, NO_ENTRY) || (pdfbox_title.length()==0)) {
				title = clean_field(getFieldDocearStyle(file).trim());
				// titleLike!
				//title = NO_TITLE;
			} else {
				title = pdfbox_title;
			}
			Element titleElement = doc.createElement("title");
			titleElement.appendChild(doc.createTextNode(title));
			metaData.appendChild(titleElement);
			
			Article article = da.getArticleObj(title);
			
			if (article != null) {
				// wenn er was findet, dann nimm das auch: natuerlich fehleranfaellig.
				List<String> authors = article.getAuthors();
				StringBuilder sb = new StringBuilder();
				for (int i=0;i<authors.size();i++){
					sb.append(authors.get(i));
				}
				author = sb.toString();
				pubDateString = article.getYear();
				
			} else {
				/*--------------------------------------------------------
				 * wenn er keine results hat.
				 *--------------------------------------------------------*/
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
			Element authorElement = doc.createElement("authors");
			authorElement.appendChild(doc.createTextNode(author));
			metaData.appendChild(authorElement);

			Element pubDate = doc.createElement("publificationDate");
			pubDate.appendChild(doc.createTextNode(pubDateString));
			metaData.appendChild(pubDate);

			Element textElements = doc.createElement("textElements");
			rootElement.appendChild(textElements);
			Element articleAbstract = doc.createElement("abstract");
			articleAbstract.appendChild(doc.createTextNode("Dummy. Please Implement."));
			textElements.appendChild(articleAbstract);
			/*
			 * get & set the main text
			 */
			String fullText = null;
			try {
				stripper.setStartPage(0);
				stripper.setEndPage(Integer.MAX_VALUE);
				fullText =  stripper.getText(myDoc);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			//System.out.print(fullText);
			Element mainText = doc.createElement("article");
			mainText.appendChild(doc.createTextNode(fullText));
			textElements.appendChild(mainText);
			
			writeToXML(doc,outputPath);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (DOMException e1) {
			e1.printStackTrace();
		} 


	}
	
	
	
	

	
}
