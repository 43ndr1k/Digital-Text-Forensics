package de.uni_leipzig.digital_text_forensics.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

import de.uni_leipzig.digital_text_forensics.domain.Pager;

public class Searcher {
  
  public final String indexLocation = "/Users/David/Desktop/LuceneIndex";
  private StandardAnalyzer analyzer = new StandardAnalyzer();
  
  public List<Document> search(String query) throws IOException, ParseException {
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));//IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
    IndexSearcher searcher = new IndexSearcher(reader);
    TopScoreDocCollector collector = TopScoreDocCollector.create(Pager.RESULTS_PER_PAGE);
  
    Query q = new QueryParser("contents", analyzer).parse(query);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;

    // display results
    System.out.println("Found " + hits.length + " hits.");
    List<Document> d = new ArrayList<>();
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      d.add(searcher.doc(docId));
    }
    return d;
  }
    
}
