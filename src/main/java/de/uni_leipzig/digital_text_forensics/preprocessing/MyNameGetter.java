package de.uni_leipzig.digital_text_forensics.preprocessing;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span; 

import java.io.FileInputStream;
import java.io.IOException; 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
/**
* MyNameGetter
* <p> Uses NE-Recognition to extract Authors.
*  
* @author Tobias Wenzel
* 
*/
public class MyNameGetter {
    @Value("classpath:preprocessing/en-token.bin")
    private Resource en_token_res;
    
    @Value("classpath:preprocessing/en-ner-person.bin")
    private Resource en_ner_person_res;
	
	String ner_file = "src/main/resources/preprocessing/en-ner-person.bin";
	String token_file = "src/main/resources/preprocessing/en-token.bin";

	/**
	 * 
	 * @param paragraph
	 * @throw	s IOException
	 */
	public List<String> findName(String paragraph) throws IOException {
		
		//TokenNameFinderModel model = new TokenNameFinderModel(en_ner_person_res.getInputStream());
		
		InputStream inputStream = new FileInputStream(ner_file);
		TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
		NameFinderME nameFinder = new NameFinderME(model);
		String[] tokens = tokenize(paragraph);

		Span nameSpans[] = nameFinder.find(tokens);
		 List<String> myNames = new ArrayList<String>();
		 for(Span s: nameSpans){
			 try{
	    	  String temp = tokens[s.getStart()]+ " "+ tokens[s.getStart()+1];
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
	 public String[] tokenize(String sentence) throws IOException{
		 InputStream inputStreamTokenizer = new FileInputStream(token_file);
		 TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);

		 //TokenizerModel tokenModel = new TokenizerModel(en_token_res.getInputStream());
		 TokenizerME tokenizer = new TokenizerME(tokenModel);
		 return tokenizer.tokenize(sentence);
		 
	 }
	

}
