package de.uni_leipzig.digital_forensics.testpackage.preprocessing;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	
	public static void main(String[] args) {
		DBLPDataAccessor da = new DBLPDataAccessor();
		Article article = da.getArticleObj("Comparing frequency and Style");
		System.out.println(article);

		
	
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