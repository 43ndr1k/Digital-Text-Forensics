package de.uni_leipzig.digital_text_forensics.preprocessing;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public class WordOperationClass {
	String ner_file = "res/en-ner-person.bin";
	String token_file = "res/en-token.bin";

	private InputStream inputStreamTokenizer;
	private TokenizerModel tokenModel;;
	private TokenizerME tokenizer;
	private PorterStemmer pt;

	private int minTitleLength = 4;
	private List<String> blockWords;

	/**
	 * Constructor
	 */
	public WordOperationClass() {
		inputStreamTokenizer = null;
		tokenModel = null;

		try {
			inputStreamTokenizer = new FileInputStream(token_file);
			tokenModel = new TokenizerModel(inputStreamTokenizer);
			pt = new PorterStemmer();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		tokenizer = new TokenizerME(tokenModel);

		readStopWords(new File(
				"/home/tobias/workspace/tobiastestproject/res/stopwords.txt"));
	}

	/**
	 * 
	 * @param stopWordFile
	 */
	private void readStopWords(File stopWordFile) {

		blockWords = new ArrayList<String>();
		try {

			@SuppressWarnings("resource")
			BufferedReader b = new BufferedReader(new FileReader(stopWordFile));
			String readLine = "";
			while ((readLine = b.readLine()) != null) {
				blockWords.add(readLine.toLowerCase());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param paragraph
	 * @throw s IOException
	 */
	public List<String> findName(String paragraph) throws IOException {
		InputStream inputStream = new FileInputStream(ner_file);

		TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
		NameFinderME nameFinder = new NameFinderME(model);
		String[] tokens = tokenizer.tokenize(paragraph);

		Span nameSpans[] = nameFinder.find(tokens);
		List<String> myNames = new ArrayList<String>();
		for (Span s : nameSpans) {
			try {
				String temp = tokens[s.getStart()] + " "
						+ tokens[s.getStart() + 1];
				myNames.add(temp);
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				myNames.add(tokens[s.getStart()]);
			}

		}
		return myNames;

	}

	/**
	 * 
	 * @param sentence
	 * @return
	 * @throws IOException
	 */
	public String[] tokenize(String sentence) throws IOException {
		return tokenizer.tokenize(sentence);
	}

	/**
	 * <p>
	 * Method to check if a given field field doesn't contain a set of tokens
	 * like links and has minimum length.
	 * 
	 * @param field_string
	 * @return cleaned Field as String
	 */
	public String clean_field(String field_string, Boolean titleInput) {
		String new_title = null;
		if ((field_string == null) || (field_string.length() == 0)) {
			return "";
		}
		if ((field_string.trim().length() > minTitleLength)
				|| (field_string.matches("[A-Za-z]+"))) {
			new_title = field_string;
		} else {
			return "";
		}
		String[] fieldStrings = null;
		try {
			fieldStrings = tokenize(new_title);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * einfach die wörter rausschmeissen/nicht aufnehmen!
		 */
		StringBuilder sb = new StringBuilder();
		for (String word : fieldStrings) {
			Boolean block = false;
			String lowerCaseWord = word.toLowerCase();
			if (titleInput) {
				lowerCaseWord.replaceAll("[\\W]|_", "");
			}
			for (String blockWord : blockWords) {
				// @todo equals
				if (lowerCaseWord.equals(blockWord)) {
					block = true;
				}
				// if (lowerCaseWord.contains(blockWord)){
				// block = true;
				// }
			}
			if (!block) {
				sb.append(WordUtils.capitalize(lowerCaseWord) + " ");
			}
		}
		String res = sb.toString().replaceAll("[^\\p{L}\\p{Z}]", "")
				.replaceAll("\\s+", " ").trim();
		if (res.length() != 0)
			return res;
		else {
			return "";
		}
	}

	/**
	 * Get n words as String.
	 * 
	 * @param paragraph
	 * @param n
	 * @return
	 */
	public String getNWords(String paragraph, int n) {
		String[] words = null;
		try {
			words = tokenize(paragraph);
		} catch (IOException e) {

		}
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String word : words) {
			if (i == n) {
				break;
			}
			sb.append(" ");
			sb.append(word.replaceAll("[\\W]|_", ""));
			i++;
		}

		return sb.toString();
	}

	/**
	 * Get n words as tokens.
	 * 
	 * @param paragraph
	 * @param n
	 * @return
	 */
	public List<String> getNWords(String paragraph, int n, boolean returnTokens) {
		String[] words = null;
		try {
			words = tokenize(paragraph);
		} catch (IOException e) {

		}
		List<String> nWords = new ArrayList<String>();

		int i = 0;
		for (String word : words) {
			if (i == n) {
				break;
			}

			nWords.add(word.replaceAll("[\\W]|_", ""));
			i++;
		}

		return nWords;
	}

	/**
	 * 
	 * @param nonStemmed
	 * @return
	 */
	public String getStemmedWords(String nonStemmed) {
		String tokens[] = null;
		try {
			tokens = tokenize(nonStemmed);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tokens != null) {
			List<String> stemmedTokens = new ArrayList<String>();

			for (String token : tokens) {
				stemmedTokens.add(this.pt.stem(token));
			}
			return String.join(" ", stemmedTokens);
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param nonStemmed
	 * @return
	 */
	public String getStemmedWordsAsString(String nonStemmed) {
		String tokens[] = null;
		try {
			tokens = tokenize(nonStemmed);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (tokens != null) {
			StringBuilder sb = new StringBuilder();
			for (String token : tokens) {
				sb.append(this.pt.stem(token) + " ");
			}
			return sb.toString();
		} else {
			return "";
		}
	}

}
