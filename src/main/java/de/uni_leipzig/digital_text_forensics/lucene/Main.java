package de.uni_leipzig.digital_text_forensics.lucene;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		XMLFileIndexer xmlFileIndexer = null;
		try {
			xmlFileIndexer = new XMLFileIndexer("LuceneIndex");
			xmlFileIndexer.run();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
	}
	

}
