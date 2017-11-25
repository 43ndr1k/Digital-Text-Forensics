package de.uni_leipzig.digital_forensics.testpackage.docear;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import de.uni_leipzig.digital_forensics.testpackage.preprocessing.PdfFileFilter;

import org.apache.commons.lang.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.docear.pdf.PdfDataExtractor;
import org.docear.pdf.options.Configuration;
import org.docear.pdf.options.OptionParser;
import org.xml.sax.SAXException;

import com.google.common.base.Objects;

public class DocearTest {
	static int MIN_TITLE_LENGTH = 5;
	static String NO_TITLE = "No proper title found.";
	static String BLOCK_WORDS[] = {".pdf",".doc",".dvi","title","Chapter","rights reserved","Article", "http"};
	static String DATA_DIR = "/home/tobias/Dokumente/authorship-material-stathis/";
	
	/*
	 * @argument title
	 */
	private static String clean_field(String title){
		String new_title = null;
		// must consist of reasonable characters & length
		if(!title.matches("[A-Za-z0-9]+") && title.trim().length() > MIN_TITLE_LENGTH) {
		    new_title = title;
		} else {
			return NO_TITLE;
		}
		if (title.length()==0){
			return NO_TITLE;
		}
		// you are welcome to change this
		for (String word : BLOCK_WORDS) {
			if (title.contains(word)) {
				return NO_TITLE;
			}
		}
		// remove special characters like brakets
		String trimmed_new_title = new_title.replaceAll("[^\\p{L}\\p{Z}]","").trim();
		if (trimmed_new_title.length()==0){
			return NO_TITLE;
		} else {
			return trimmed_new_title.replaceAll("\\s+", " ");
		}
		
	}
	
	/*
	 * @argument file
	 */
	private static String getFieldPdfBoxStyle(File file) {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
		} catch(Exception e){
			e.printStackTrace();
		}  
		String title = doc.getDocumentInformation().getTitle();
		/*
		 * here we could also collect the author and other information.
		 */
		// String author = doc.getDocumentInformation().getAuthor();
		if (title != null) {
			return title;
		} else {
			return NO_TITLE;
		}
		
	}
	
	/*
	 * @argument file
	 */
	private static String getFieldTikaStyle(File file) {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File(file.toString()));
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
		}
		ParseContext pcontext = new ParseContext();
		PDFParser pdfparser = new PDFParser(); 

		try {
			pdfparser.parse(inputstream, handler, metadata,pcontext);
		} catch (SAXException e) {
			//e.printStackTrace();
		} catch (TikaException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		/*
		 * here we could also collect the author and other information.
		 */
		//String author = metadata.get("Author");
		String title = metadata.get("title");
		if (title != null) {
			return title;
		} else {
			return NO_TITLE;
		}
	}
	
	/*
	 * @argument file
	 */
	private static String getFieldDocearStyle(File file) {
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
				sb.append(NO_TITLE);
			}
			catch (de.intarsys.pdf.cos.COSSwapException e){
				sb.append(NO_TITLE);
			}
		}
		finally {
			extractor.close();
			extractor = null;
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) {

		File[] files = new File(DATA_DIR).listFiles();
		PdfFileFilter filter = new PdfFileFilter();

		for (File file : files) {
			if(!file.isDirectory()
					&& !file.isHidden()
					&& file.exists()
					&& file.canRead()
					&& filter.accept(file)
					){

				String res = getFieldDocearStyle(file).trim();
				/*
				 * if this didn't work try pdfbox or tika
				 */
				if (Objects.equal(res, NO_TITLE) || (res.length()==0)) {
					System.out.println(WordUtils.capitalize(clean_field(getFieldPdfBoxStyle(file).toLowerCase())));
					/*
					 * unfortunately there seem to be some (minor) problems, so please use pdfbox.
					 */
					//out.println(WordUtils.capitalize(clean_field(getFieldTikaStyle(file))));
				} else {
					System.out.println(WordUtils.capitalize(res.toLowerCase()));
				}
			}
		} // end of for loop


	} // end of function

}
