package pdfboxtest;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span; 

import java.io.FileInputStream;
import java.io.IOException; 
import java.io.InputStream;

public class MyNameGetter {
	String ner_file = "/home/tobias/Downloads/en-ner-person.bin";
	String token_file = "/home/tobias/Downloads/en-token.bin";

	
	/**
	 * 
	 * @param paragraph
	 * @throws IOException
	 */
	public void findName(String paragraph) throws IOException {
		InputStream inputStream = new FileInputStream(ner_file);
		
		TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
		NameFinderME nameFinder = new NameFinderME(model);
		String[] tokens = tokenize(paragraph);

		Span nameSpans[] = nameFinder.find(tokens);
	      for(Span s: nameSpans) 
	    	  System.out.println(s.toString()+"  "+tokens[s.getStart()]+ " "+ tokens[s.getStart()+1]);

		}
	/**
	 * 
	 * @param sentence
	 * @return
	 * @throws IOException
	 */
	 public String[] tokenize(String sentence) throws IOException{

		 InputStream inputStreamTokenizer = new FileInputStream(token_file);
		 TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
		 TokenizerME tokenizer = new TokenizerME(tokenModel);
		 return tokenizer.tokenize(sentence);
		 
	 }
	

}
