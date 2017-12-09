package de.uni_leipzig.digital_forensics.testpackage.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

import de.uni_leipzig.digital_forensics.testpackage.preprocessing.PdfFileFilter;

public class LuceneTester {
	
   String indexDir = "/home/tobias/Dokumente/test_index_dir";
   String dataDir = "/home/tobias/Dokumente/authorship-material-stathis/";
   Indexer indexer;
   Searcher searcher;

   	/**
   	 * 
   	 * @param args
   	 */
   public static void main(String[] args) {
      LuceneTester tester;
      try {
         tester = new LuceneTester();
         tester.createIndex();
         //tester.search("Computational");
      } 
      catch (IOException e) {
         e.printStackTrace();
      } 
//      catch (ParseException e) {
//		e.printStackTrace();
//      } 
   }

   /**
    * 
    * @throws IOException
    */
   private void createIndex() throws IOException {
      indexer = new Indexer(indexDir);
      int numIndexed;
      long startTime = System.currentTimeMillis();	
      numIndexed = indexer.createIndex(dataDir, new PdfFileFilter());
      long endTime = System.currentTimeMillis();
      indexer.close();
      System.out.println(numIndexed+" File indexed, time taken: "
         +(endTime-startTime)+" ms");		
   }
   /**
    * 
    * @param searchQuery
    * @throws IOException
    * @throws ParseException
    */
   private void search(String searchQuery) throws IOException, ParseException {
	      searcher = new Searcher(indexDir);
	      TopDocs hits = searcher.search(searchQuery);
	   
	      System.out.println(hits.totalHits +
	         " documents found.");
	      for(ScoreDoc scoreDoc : hits.scoreDocs) {
	         Document doc = searcher.getDocument(scoreDoc);
//	            System.out.println("File: "
//	            + doc.get(LuceneConstants.FILE_PATH));
	            System.out.println(doc.get(LuceneConstants.TITLE));
	      }
	      searcher.close();
	   }
}