package testpackage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;

import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.metadata.Metadata;




public class TestTikaClass {

	
	
	public static void main(String[] args) {
		//String filename = "/home/tobias/Downloads/authorship-material-stathis/kuo-2010.pdf";
		String filename = "/home/tobias/Downloads/authorship-material-stathis/mikros-2015.doc";

		try {
			testTikaMetaContent(filename);
			//checkOurFiles("/home/tobias/Downloads/authorship-material-stathis");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
	}
	
	private static void checkOurFiles(String dataDirPath) throws IOException {

		/*
		 * walks through dir and checks if files have some meta-data
		 */
		
		
	      File[] files = new File(dataDirPath).listFiles();
	      PdfFileFilter filter = new PdfFileFilter();
	      int index = 0;
	      
	      for (File file : files) {
	         if(!file.isDirectory()
	            && !file.isHidden()
	            && file.exists()
	            && file.canRead()
	            && filter.accept(file)
	         ){
	   	      BodyContentHandler handler = new BodyContentHandler();
		      Metadata metadata = new Metadata();
		      FileInputStream inputstream = new FileInputStream(new File(file.toString()));
		      ParseContext pcontext = new ParseContext();
		      PDFParser pdfparser = new PDFParser(); 
		      // besser außerhalb.
		      Writer output = new BufferedWriter(new FileWriter("my_pdf_log.txt", true));
		      
		      try {
				pdfparser.parse(inputstream, handler, metadata,pcontext);
		      } catch (SAXException e) {
				//e.printStackTrace();
				output.append("saxexc\t"+file.toString()+"\n");
				index++;
		      } catch (TikaException e) {
		    	  output.append("tikaexc\t"+file.toString()+"\n");
				index++;
				//e.printStackTrace();
			}
		      output.close();
		      
		      System.out.println("Author"+ " : " + metadata.get("Author"));
		      System.out.println("created"+ " : " + metadata.get("created"));
		      System.out.println("title"+ " : " + metadata.get("title"));

	         }
	         
	      }
	      System.out.println(String.valueOf(index)+"files had errors");
	      
	}
	
	@SuppressWarnings("deprecation")
	private static void testTikaMetaContent(String filename) throws IOException,TikaException{
	      BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      FileInputStream inputstream = new FileInputStream(new File(filename));
	      ParseContext pcontext = new ParseContext();
	      
	      //PDFParser pdfparser = new PDFParser(); 
	      
	      OfficeParser officeParser = new OfficeParser();
	      
	      try {
			// pdfparser.parse(inputstream, handler, metadata,pcontext);
			officeParser.parse(inputstream, handler, metadata);
	      } catch (SAXException e) {
			e.printStackTrace();
	      }
	      
	      //getting the content of the document

	      System.out.println("Contents of the PDF :" + handler.toString());
	      
	      // eher nicht.
	      //String pdftext = handler.toString();
	      //System.out.println(pdftext.replaceAll("(\\r|\\n)", ""));
	      
	      // deprecated?
//	      LanguageIdentifier object = new LanguageIdentifier(handler.toString());
//	      System.out.println("Language name :" + object.getLanguage());
	      
	      //getting metadata of the document
	      System.out.println("Metadata of the PDF:");
	      String[] metadataNames = metadata.names();
	      
	      System.out.println("Author"+ " : " + metadata.get("Author"));
	      System.out.println("created"+ " : " + metadata.get("created"));
	      System.out.println("title"+ " : " + metadata.get("title"));

	      
//	      for(String name : metadataNames) {
//	         System.out.println(name+ " : " + metadata.get(name));
//	      }
	   }
	      
	}

