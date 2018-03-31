package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.util.ArrayList;

import java.util.List;
import org.json.JSONArray;

public class Article {

	private String doi;
	private String title;
	private String key;
	private String score;
	private String fullText;
	private String myAbstract;
	private String fileName;
	private String filePath;
	private String publicationDate;
	private String parseDate;
	private String refCount;
	private String journal;

	private List<String> authors;
	private String ee;

	/**
	 * constructor
	 */
	public Article() {
		this.authors = new ArrayList<String>();
		this.score = "0";
		this.refCount = "0";
	}

	/**
	 * Copy constructor
	 * 
	 * @param article
	 */
	public Article(Article article) {
		this.fileName = article.fileName;
		this.doi = article.doi;
		this.ee = article.ee;
		this.filePath = article.filePath;
		this.fullText = article.fullText;
		this.journal = article.journal;
		this.key = article.key;
		this.myAbstract = article.myAbstract;
		this.parseDate = article.parseDate;
		this.publicationDate = article.publicationDate;
		this.refCount = article.refCount;
		this.score = article.score;
		this.title = article.title;

	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getMyAbstract() {
		return myAbstract;
	}

	public void setMyAbstract(String myAbstract) {
		if (myAbstract != null)
			this.myAbstract = new String(myAbstract);
		else
			this.myAbstract = "";
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publificationDate) {
		this.publicationDate = publificationDate;
	}

	public String getParseDate() {
		return parseDate;
	}

	public void setParseDate(String parseDate) {
		this.parseDate = parseDate;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String string) {
		this.score = string;
	}

	public String getDoi() {
		if (doi != null)
			return doi;
		else
			return "doi";
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public List<String> getAuthors() {
		return authors;
	}


	
	public String getAuthorsAsString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < this.authors.size(); i++) {
			stringBuilder.append(authors.get(i) + ", ");
		}
		return stringBuilder.toString().replaceAll("\\s+$", "").replaceAll(",+$", "");

	}

	public void setAuthors(JSONArray authors) {
		this.authors.clear();
		for (int i = 0; i < authors.length(); i++) {
			String author = (String) authors.get(i);
			this.authors.add(author);
		}
	}

	public void setAuthorsList(List<String> authors) {
		if (this.authors != null)
			this.authors.clear();
		this.authors.addAll(authors);
	}
	
	public void setAuthorsString(String authors) {
		String[] articleAuthors = authors.split(",");
		if (this.authors != null) {
			this.authors.clear();
			
		} else{
			this.authors = new ArrayList<String>();
		}
		for (String author : articleAuthors) {
			this.authors.add(author);
		}
	}
	
	public void addAuthor(String author) {
		this.authors.add(author);
	}
	
	

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.publicationDate + ", ");
		stringBuilder.append(this.title + "\n");
		stringBuilder.append("authors:\t");

		for (int i = 0; i < this.authors.size(); i++) {
			stringBuilder.append(authors.get(i) + " ");
		}
		stringBuilder.append("score\t " + this.score + "\n");

		return stringBuilder.toString();
	}

	public String getRefCount() {
		return refCount;
	}

	public void setRefCount(String refCount) {
		this.refCount = refCount;
	}

	public String getEeLink() {
		return ee;
	}

	public void setEeLink(String eeLink) {
		this.ee = eeLink;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

}

