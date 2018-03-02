package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RefCountEntryHandler extends DefaultHandler{
	private List<RefCountObj> refObjs= null;
	private RefCountObj currentObj = null;
    /**
     * 
     * @return
     */
    public List<RefCountObj> getRefCountObjs() {
        return refObjs;
    }

    boolean bEntry = false;
    boolean bFileName = false;
    boolean bCounter = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("entry")) {
        	if (refObjs == null) {
        		refObjs = new ArrayList<RefCountObj>();
        	}
        	currentObj = new RefCountObj();
            bEntry = false;
        } else if (qName.equalsIgnoreCase("counter")) {
        	bCounter= true;
        } else if (qName.equalsIgnoreCase("fileName")) {
        	bFileName = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("entry")) {
        	if (currentObj != null) {
        		refObjs.add(currentObj);  
        	}
        	currentObj = null;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bEntry) {
            bEntry = false;
        } else if (bCounter) {
        	currentObj.setCounter(new String(ch, start, length));
            bCounter = false;
        } else if (bFileName) {
        	currentObj.setFileName(new String(ch, start, length));
            bFileName = false;
        } 
    }
}
