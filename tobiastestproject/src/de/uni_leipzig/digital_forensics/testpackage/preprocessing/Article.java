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

import org.json.JSONArray;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Date;

public class Article {

  private String doi;
  private String title;
  private String year;
  private String key;
  private String score;
  private List<String> authors;

  public Article() {
	  this.authors = new ArrayList<String>();
  }

public String getScore() {
	return score;
}

public void setScore(String string) {
	this.score = string;
}

public String getDoi() {
	return doi;
}

public void setDoi(String doi) {
	this.doi = doi;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getYear() {
	return year;
}

public void setYear(String year) {
	this.year = year;
}

public String getKey() {
	return key;
}

public void setKey(String key) {
	this.key = key;
}

public List<String> getAuthors() {
	return authors;
}

public void setAuthors(JSONArray authors) {
	for (int i=0; i<authors.length(); i++) {
		String author = (String) authors.get(i);
		//System.out.println(author);
		this.authors.add(author);
	}
}
public void setAuthors(String author) {
	this.authors.add(author);
}


  @Override
  public String toString() {
	 StringBuilder stringBuilder = new StringBuilder();
	 stringBuilder.append(this.year+", ");
	 stringBuilder.append(this.title+"\n");
	 stringBuilder.append("authors:\t");
	 
	for (int i=0;i<this.authors.size();i++) {
		stringBuilder.append(authors.get(i)+" ");
	}
	 stringBuilder.append("score\t "+this.score+"\n");

    return stringBuilder.toString();
  }


}

