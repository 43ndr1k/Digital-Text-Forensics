package de.uni_leipzig.digital_text_forensics.preprocessing;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.uni_leipzig.digital_text_forensics.preprocessing.Article;

/**
* ArticleHandler
* <p> Handler for Article XML-Files.
*  
* @author Tobias Wenzel
* 
*/
public class ArticleHandler extends DefaultHandler {

    private Article article= null;

    /**
     * 
     * @return
     */
    public Article getArticle() {
        return article;
    }

    boolean bMetaData = false;
    boolean bTextElements = false;
    boolean bFullText = false;
    boolean bFileName = false;
    boolean bFilePath = false;
    boolean bTitle = false;
    boolean bAuthors = false;
    boolean bPublicationDate = false;
    boolean bAbstract = false;
    boolean bParseDate = false;

    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("article")) {
            article = new Article();

        } else if (qName.equalsIgnoreCase("metaData")) {
            String docId = attributes.getValue("docId");
            article.setDoi(docId);
        	bMetaData = true;
        } else if (qName.equalsIgnoreCase("textElements")) {
        	bTextElements = true;
        } else if (qName.equalsIgnoreCase("fullText")) {
        	bFullText= true;
        } else if (qName.equalsIgnoreCase("fileName")) {
        	bFileName = true;
        }else if (qName.equalsIgnoreCase("filePath")) {
        	bFilePath = true;
        }else if (qName.equalsIgnoreCase("title")) {
            bTitle = true;
        }else if (qName.equalsIgnoreCase("authors")) {
            bAuthors = true;
        }else if (qName.equalsIgnoreCase("publicationDate")) {
            bPublicationDate = true;
        }else if (qName.equalsIgnoreCase("abstract")) {
            bAbstract = true;
        } else if (qName.equalsIgnoreCase("parseTime")){
        	bParseDate = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        if (qName.equalsIgnoreCase("article")) {
//            //add Employee object to list
//           return;
//        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bTitle) {
            //age element, set Employee age
            article.setTitle(new String(ch, start, length));
            bTitle = false;
        } else if (bAuthors) {
            article.setAuthors(new String(ch, start, length));
            bAuthors = false;
        } else if (bAbstract) {
            article.setMyAbstract(new String(ch, start, length));
            bAbstract = false;
        } else if (bPublicationDate) {
            article.setPublicationDate(new String(ch, start, length));
            bPublicationDate = false;
        } else if (bFilePath) {
            article.setFilePath(new String(ch, start, length));
            bFilePath = false;
        }else if (bFileName) {
            article.setFileName(new String(ch, start, length));
            bFileName = false;
        }else if (bFullText) {
            article.setFullText(new String(ch, start, length));
            bFullText = false;
        } else if (bParseDate) {
        	article.setParseDate(new String(ch, start, length));
        	bParseDate = false;
        }
    }
}