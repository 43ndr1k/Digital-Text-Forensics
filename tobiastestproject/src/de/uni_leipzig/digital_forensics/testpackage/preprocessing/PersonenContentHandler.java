package de.uni_leipzig.digital_forensics.testpackage.preprocessing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class PersonenContentHandler implements ContentHandler {

  private ArrayList<Person> allePersonen = new ArrayList<Person>();
  private String currentValue;
  private Person person;

  // Aktuelle Zeichen die gelesen werden, werden in eine Zwischenvariable
  // gespeichert
  public void characters(char[] ch, int start, int length)
      throws SAXException {
    currentValue = new String(ch, start, length);
  }

  // Methode wird aufgerufen wenn der Parser zu einem Start-Tag kommt
  public void startElement(String uri, String localName, String qName,
      Attributes atts) throws SAXException {
    if (localName.equals("person")) {
      // Neue Person erzeugen
      person = new Person();

      // Attribut id wird in einen Integer umgewandelt und dann zu der
      // jeweiligen Person gesetzt
      person.setId(Integer.parseInt(atts.getValue("id")));
    }
  }

  // Methode wird aufgerufen wenn der Parser zu einem End-Tag kommt
  public void endElement(String uri, String localName, String qName)
      throws SAXException {

    // Name setzen
    if (localName.equals("name")) {
      person.setName(currentValue);
    }

    // Vorname setzen
    if (localName.equals("vorname")) {
      person.setVorname(currentValue);
    }

    // Datum parsen und setzen
    if (localName.equals("geburtsdatum")) {
      SimpleDateFormat datumsformat = new SimpleDateFormat("dd.MM.yyyy");
      try {
        Date date = datumsformat.parse(currentValue);
        person.setGeburtsdatum(date);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    // Postleitzahl setzen
    if (localName.equals("postleitzahl")) {
      person.setPostleitzahl(currentValue);
    }

    // Ort setzen
    if (localName.equals("ort")) {
      person.setOrt(currentValue);
    }

    // Person in Personenliste abspeichern falls Person End-Tag erreicht
    // wurde.
    if (localName.equals("person")) {
      allePersonen.add(person);
      System.out.println(person);
    }
  }

  public void endDocument() throws SAXException {}
  public void endPrefixMapping(String prefix) throws SAXException {}
  public void ignorableWhitespace(char[] ch, int start, int length)
      throws SAXException {}
  public void processingInstruction(String target, String data)
      throws SAXException {}
  public void setDocumentLocator(Locator locator) {  }
  public void skippedEntity(String name) throws SAXException {}
  public void startDocument() throws SAXException {}
  public void startPrefixMapping(String prefix, String uri)
    throws SAXException {}
  
}