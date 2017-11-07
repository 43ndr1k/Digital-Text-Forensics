package testpackage;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.queryparser.classic.ParseException;

public class LuceneTester {
	
   String indexDir = "/home/tobias/Dokumente/test_index_dir";
   String dataDir = "/home/tobias/Downloads/authorship-material-stathis";
   Indexer indexer;
   Searcher searcher;

   public static void main(String[] args) {
      LuceneTester tester;
      try {
         tester = new LuceneTester();
         //tester.createIndex();
         tester.search("Tobias");
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
		e.printStackTrace();
	} 
   }

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

   private void search(String searchQuery) throws IOException, ParseException {
	      searcher = new Searcher(indexDir);
	      TopDocs hits = searcher.search(searchQuery);
	   
	      System.out.println(hits.totalHits +
	         " documents found.");
	      for(ScoreDoc scoreDoc : hits.scoreDocs) {
	         Document doc = searcher.getDocument(scoreDoc);
	            System.out.println("File: "
	            + doc.get(LuceneConstants.FILE_PATH));
	      }
	      searcher.close();
	   }
}