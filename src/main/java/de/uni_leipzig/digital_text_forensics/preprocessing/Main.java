package de.uni_leipzig.digital_text_forensics.preprocessing;
import java.io.IOException;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import de.uni_leipzig.digital_text_forensics.preprocessing.Article;
import de.uni_leipzig.digital_text_forensics.preprocessing.ArticleHandler;


public class Main {

	
	public static void main(String[] args) {

			
		    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		    try {
		        SAXParser saxParser = saxParserFactory.newSAXParser();
		        ArticleHandler handler = new ArticleHandler();
		        saxParser.parse(new File("/home/tobias/Dokumente/xmlOutput/5917-29748-1-pb-1-.xml"), handler);
		        Article article = handler.getArticle();
		        //System.out.println(article+ article.getDoi());
		        /*
		         * @TODO i don't get fullText! (just first line)
		         */
		        System.out.print(article.getFullText());

		    } catch (ParserConfigurationException pce){
		    	pce.printStackTrace();
		    } catch(SAXException sae) {
		    	sae.printStackTrace();

		    }catch ( IOException ioe) {
		    	ioe.printStackTrace();

		    }
//		    

		
		
//		DBLPDataAccessor da = new DBLPDataAccessor();
//		Article article = da.getArticleObj("Comparing frequency and Style");
//		System.out.println(article);
	}

	

//    try {
//      // XMLReader erzeugen
//      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
//      
//      // Pfad zur XML Datei
//      FileReader reader = new FileReader("res/test.xml");
//      InputSource inputSource = new InputSource(reader);
//
//      // DTD kann optional übergeben werden
//      // inputSource.setSystemId("X:\\personen.dtd");
//
//      // PersonenContentHandler wird übergeben
//      PersonenContentHandler contentHandler = new PersonenContentHandler();
//      xmlReader.setContentHandler(contentHandler);
//
//      // Parsen wird gestartet
//      xmlReader.parse(inputSource);
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (SAXException e) {
//      e.printStackTrace();
//    }
  }