package de.uni_leipzig.digital_forensics.testpackage.preprocessing;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Date;

public class Person {

  private int id;
  private String name;
  private String vorname;
  private Date geburtsdatum;
  private String postleitzahl;
  private String ort;

  public Person() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVorname() {
    return vorname;
  }

  public void setVorname(String vorname) {
    this.vorname = vorname;
  }

  public Date getGeburtsdatum() {
    return geburtsdatum;
  }

  public void setGeburtsdatum(Date geburtsdatum) {
    this.geburtsdatum = geburtsdatum;
  }

  public String getPostleitzahl() {
    return postleitzahl;
  }

  public void setPostleitzahl(String postleitzahl) {
    this.postleitzahl = postleitzahl;
  }

  public String getOrt() {
    return ort;
  }

  public void setOrt(String ort) {
    this.ort = ort;
  }

  @Override
  public String toString() {
    return "[[" + this.id + "] ["+ this.name + "] [" + this.vorname + "]" + " [" + this.ort
        + "] [" + this.postleitzahl + "] [" + this.geburtsdatum + " ]]";
  }
}


//
//public class Person { 
//	
//	private static Map<String, Person> personMap = new HashMap<String, Person>();
//	private String name; 
//	private String urlpt;
//	/**
//	 * 
//	 * @param name
//	 * @param urlpt
//	 */
//	private Person(String name, String urlpt) {
//		this.name = name;
//		this.urlpt = urlpt; 
//		personMap.put(name, this);
//		coauthorsLoaded = false; 
//		labelvalid = false;
//	}
//	/**
//	 * 
//	 * @param name
//	 * @param urlpt
//	 * @return
//	 */
//	static public Person create(String name, String urlpt) {
//		Person p;
//		p = searchPerson(name); 
//		if (p == null)
//			p = new Person(name, urlpt);
//		return p; 
//	}
//	/**
//	 * 
//	 * @param name
//	 * @return
//	 */
//	static public Person searchPerson(String name) { 
//		return personMap.get(name);
//	}
//
//	private boolean coauthorsLoaded; 
//	private Person coauthors[];
//	static private SAXParser coauthorParser; 
//	static private CAConfigHandler coauthorHandler;
//	static private List<Person> plist = new ArrayList<Person>();
//
//
//	static private class CAConfigHandler extends DefaultHandler {
//		
//		private String Value, urlpt; 
//		private boolean insideAuthor;
//
//		@SuppressWarnings("unused")
//		public void startElement(String namespaceURI, String localName,
//				String rawName, Attributes atts) throws SAXException {
//			if (insideAuthor = rawName.equals("author")) {
//				Value = "";
//			}
//			urlpt = atts.getValue("urlpt"); 
//		} 
//
//		public void endElement(String namespaceURI,
//				String localName, String rawName) throws SAXException {
//			if (rawName.equals("author") && Value.length() > 0) {
//				plist.add(create(Value, urlpt)); 
//			} 
//		}
//
//		public void characters(char[] ch, int start, int length) throws SAXException {
//			if (insideAuthor) 
//				Value += new String(ch, start, length);
//		}
//
//		public void warning(SAXParseException e) throws SAXException { 
//			System.out.println(e);
//		}
//		public void error(SAXParseException e) throws SAXException { 
//			System.out.println(e);
//		}
//		public void fatalError(SAXParseException e) throws SAXException { 
//			System.out.println(e);
//		}
//	} // end of Class CAConfigHandler
//
//	static { 
//		try { 
//			coauthorParser = SAXParserFactory. newInstance().newSAXParser();
//			coauthorHandler = new CAConfigHandler(); 
//			coauthorParser.getXMLReader().setFeature( "http://xml.org/sax/features/validation", false);
//		} 
//		catch (ParserConfigurationException e) { 
//			System.out.println(e);
//		} 
//		catch (SAXException e) { 
//			System.out.println(e);
//		}
//	}
//
//
//	private void loadCoauthors() {
//		if (coauthorsLoaded)
//			return; plist.clear();
//			try { 
//				URL u = new URL( "http://dblp.uni-trier.de/rec/pers/" +urlpt+"/xc"); 
//				coauthorParser.parse(
//						u.openStream(),
//						coauthorHandler);
//			} catch (IOException e) { 
//				System.out.println(e);
//			} catch (SAXException e) { 
//				System.out.println(e);
//			} 
//			coauthors = new Person[plist.size()];
//			coauthors = plist.toArray(coauthors);
//			coauthorsLoaded = true;
//	}
//	public Person[] getCoauthors() { 
//		if (!coauthorsLoaded) { 
//			loadCoauthors(); 
//		} 
//		return coauthors;
//	}
//
//	private int label;
//	private boolean labelvalid;
//	public int getLabel() {
//		return (!labelvalid)?0:label;
//	}
//	public void resetLabel() {
//		labelvalid = false; 
//	}
//
//	public boolean hasLabel() { 
//		return labelvalid;
//	}
//	public void setLabel(int label) {
//		this.label = label;
//		labelvalid = true; 
//	}
//	static public void resetAllLabels() { 
//		Iterator<Person> i = personMap.values().iterator();
//		while (i.hasNext()) { 
//			Person p = i.next();
//			p.labelvalid = false; 
//			p.label = 0;
//		} 
//	}
//	public String toString() { 
//		return name; 
//	}
//			
//	}  // end of Class Person





