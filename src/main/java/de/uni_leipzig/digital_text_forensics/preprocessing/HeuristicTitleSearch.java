package de.uni_leipzig.digital_text_forensics.preprocessing;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Component
public class HeuristicTitleSearch {
    @Value("classpath:preprocessing/relevant_journals.xml")
    private Resource relenvant_journals_res;
	
	private int correctTitleBoost;
	private int correctAuthorBoost;
	private int correctYearBoost;

	private String xmlInputPath;
	private String xmlOutputPath;

	private int rawWordCount; // number of words taken from raw data
	private String dblpFile;
	public int threshold;

	private List<String> currentAuthors;
	private List<String> currentRawTokens;

	private int bestMatchCount;
	private Article bestMatchArticle;

	private WordOperationClass wordOps;
	private ConvertPdfXML converter;

	private XMLInputFactory inputFactory;
	private XMLEventReader eventReader;

	
	public HeuristicTitleSearch() {
		this.xmlInputPath = "xmlFiles/";
		this.xmlOutputPath = "xmlFiles/";
		

		
		this.dblpFile = "src/main/resources/preprocessing/relevant_journals.xml";
		/*
		 * Constants
		 */
		// amount of words extracted from "raw" XML.
		this.rawWordCount = 100;
		// boost are multiplied by field-length.
		this.correctAuthorBoost = 20;
		this.correctTitleBoost = 50;
		this.correctYearBoost = 5;
		// accept only results over threshold.
		this.threshold = 100;
		/*
		 * Temporary variables.
		 */
		this.currentAuthors = new ArrayList<String>();
		this.bestMatchCount = 0;
		this.bestMatchArticle = new Article();

		this.wordOps = new WordOperationClass();
		this.converter = new ConvertPdfXML();

		this.inputFactory = XMLInputFactory.newInstance();
		this.eventReader = null;

	}

	/**
	 * Walk through XML and parse and save elements in currentArticles.
	 * Then call the getScore()-method.
	 * 
	 */
	private void compareAgainstDBLPFile() {

		
		try {
			eventReader = inputFactory
					//.createXMLEventReader(relenvant_journals_res.getInputStream());
					.createXMLEventReader(new FileInputStream(dblpFile));
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Article article = new Article();

		while (eventReader.hasNext()) {
			XMLEvent event = null;
			try {
				event = eventReader.nextEvent();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			try {
				event.isStartElement();
			} catch (java.lang.NullPointerException e) {
				return;
			}

			if (event.isStartElement()) {
				if ((event.asStartElement().getName().getLocalPart()
						.equals(DBLPXMLConstants.TITLE))) {
					try {
						String dblpTile = eventReader.getElementText();
						article.setAuthors(currentAuthors);
						article.setTitle(dblpTile);
					} catch (XMLStreamException e) {

					}
					continue;
				}

				if ((event.asStartElement().getName().getLocalPart()
						.equals(DBLPXMLConstants.AUTHOR))) {
					try {

						currentAuthors.add(eventReader.getElementText());
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
					continue;
				}

				if ((event.asStartElement().getName().getLocalPart()
						.equals(DBLPXMLConstants.YEAR))) {
					try {
						String year = eventReader.getElementText();
						article.setPublicationDate(year);
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
				}

				if ((event.asStartElement().getName().getLocalPart()
						.equals(DBLPXMLConstants.ARTICLE))) {
					currentAuthors.clear();
				}
			}
			if (event.isEndElement()) {
				if ((event.asEndElement().getName().getLocalPart()
						.equals(DBLPXMLConstants.ARTICLE))) {
					getScore(article);
				}
			}
		}
	}

	/**
	 * <p>
	 * Calculate a score for an article. Checks if tokens in
	 * <ul>
	 * <li>title,</li>
	 * <li>authors,</li>
	 * <li>publification date</li>
	 * </ul>
	 * <p>
	 * can be found in the current "raw tokens". Exact matches get a bonus.
	 * These boosts are initialized in the constructor.
	 * 
	 * 
	 * @param article
	 */
	private void getScore(Article article) {
		int count = 0;
		int titleCount = 0;
		int authorCount = 0;

		String authors = article.getAuthorsAsString();
		String title = article.getTitle();
		String pubDate = article.getPublicationDate();

		String[] authorTokens = null, titleTokens = null;
		try {
			authorTokens = wordOps.tokenize(authors);
			titleTokens = wordOps.tokenize(title);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e2) {

		}

		StringBuilder fullTextsb = new StringBuilder();
		for (String w : currentRawTokens) {
			fullTextsb.append(" " + w);
		}
		String fullText = fullTextsb.toString();

		Matcher m = null;
		Pattern MY_PATTERN = null;

		if (titleTokens != null) {
			for (String titleToken : titleTokens) {
				if (fullText.contains(titleToken)) {
					titleCount += 1;
				}
			}
			if (fullText.toLowerCase().contains(title.toLowerCase())) {
				if (!title.equals("Authorship Attribution")) {
					// better: stop-words!
					titleCount += titleTokens.length * correctTitleBoost;
				}
			}

		}

		if (currentAuthors.size() != 0) {
			for (String authorToken : article.getAuthors()) {
				if (fullText.contains(authorToken)) {
					authorCount += 2; // weight of author-tokens (!) is higher.
				}
			}
			MY_PATTERN = Pattern.compile(authors);
			m = MY_PATTERN.matcher(fullText);
			if (m.find()) {
				authorCount += authorTokens.length * correctAuthorBoost;
			}
		}

		MY_PATTERN = Pattern.compile(pubDate);
		m = MY_PATTERN.matcher(fullText);
		if (m.find()) {
			count += correctYearBoost;
		}

		count += titleCount;
		count += authorCount;

		if (count > bestMatchCount) {
			bestMatchCount = count;
			bestMatchArticle = new Article(article);
			bestMatchArticle.setAuthorsString(authors);
		}
	}
	
	/**
	 * 
	 *
	 */
	public void runOnFile(String my_filename) {
		File file= new File(my_filename);
		
		XMLFileFilter filter = new XMLFileFilter();
		if (filter.accept(file)) {

			Article article = null;
			article = converter.getArticleFromXML(file);
			if (article != null) {

				String filename = article.getFilePath().substring(
						"xmlFiles/".length(),
						article.getFilePath().length());

				this.currentRawTokens = wordOps.getNWords(
						article.getFullText(), rawWordCount, true);

				compareAgainstDBLPFile();

				try {
					if (bestMatchCount > threshold) {
						article.setTitle(bestMatchArticle.getTitle());
						article.setAuthors(bestMatchArticle.getAuthors());
						article.setPublicationDate(bestMatchArticle
								.getPublicationDate());
						converter.writeToXML(article, xmlOutputPath
								+ filename);
					}
				} catch (java.lang.NullPointerException npe) {
					npe.printStackTrace();
				}
			}
		}
	}
			
	/**
	 * <p>
	 * Main method of this class. Extracts a defined number of tokens of a given
	 * XML-file and calls the compareAgainstDBLPFile- method on these
	 * "raw tokens". This is done consecutively for all XML-files in the
	 * xmlFilePath.
	 */
	public void run() {

		File[] files = new File(xmlInputPath).listFiles();
		XMLFileFilter filter = new XMLFileFilter();

		// String line = new String(new char[100]).replace('\0', '-');

		int i = 0;
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists()
					&& file.canRead() && filter.accept(file)) {

				Article article = null;
				article = converter.getArticleFromXML(file);
				if (article != null) {

					String filename = article.getFilePath().substring(
							"xmlFiles/".length(),
							article.getFilePath().length());

					this.currentRawTokens = wordOps.getNWords(
							article.getFullText(), rawWordCount, true);

					compareAgainstDBLPFile();

					try {
						if (bestMatchCount > threshold) {
							// System.out.println("\n" + line);
							// System.out.print("Found:\n");
							// System.out.print("__title__\t"
							// + this.bestMatchArticle.getTitle() + "\n");
							//
							// System.out.println("*best count* "
							// + Integer.toString(bestMatchCount));
							// System.out.print("\nraw string:\n");
							// for (String s : currentRawTokens) {
							// System.out.print(s + " ");
							// }
							//
							// System.out.println("\n" + line);
							// System.out.println("wrote to \n" + outputFolder +
							// filename);

							article.setTitle(bestMatchArticle.getTitle());
							article.setAuthors(bestMatchArticle.getAuthors());
							article.setPublicationDate(bestMatchArticle
									.getPublicationDate());
							converter.writeToXML(article, xmlOutputPath
									+ filename);
						}
					} catch (java.lang.NullPointerException npe) {
						npe.printStackTrace();
					}
				}
				this.bestMatchArticle = null;
				this.bestMatchCount = 0;

				if (i % 10 == 0)
					System.out.println((float) 100 * i / files.length + "%");
				i++;
			}
		}
	}// end of function

	public Article getBestMatchArticle() {
		return bestMatchArticle;
	}

	public void setBestMatchArticle(Article bestMatchArticle) {
		this.bestMatchArticle = bestMatchArticle;
	}
}