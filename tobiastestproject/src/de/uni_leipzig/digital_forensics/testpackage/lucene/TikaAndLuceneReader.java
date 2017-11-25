package de.uni_leipzig.digital_forensics.testpackage.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TikaAndLuceneReader {
    private static final String INDEX_DIR = "/home/tobias/workspace/lucenetest/indexdir";
    
    public static void main(String[] args) throws Exception
    {
        IndexSearcher searcher = createSearcher();
         
//        //Search by ID
//        TopDocs foundDocs = searchById(1, searcher);
//         
//        System.out.println("Total Results :: " + foundDocs.totalHits);
//         
//        for (ScoreDoc sd : foundDocs.scoreDocs)
//        {
//            Document d = searcher.doc(sd.doc);
//            System.out.println(String.format(d.get("date")));
//        }
        
        
        //Search by title
        //TopDocs foundDocs = searchByTitle("Tue Apr 11 10:07:43 CEST 2006", searcher);
        TopDocs foundDocs = searchById(1, searcher);
        System.out.println("Total Results :: " + foundDocs.totalHits);
         
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println(String.format(d.get("date")));
        }
        
        

    }
     
    private static TopDocs searchByTitle(String title, IndexSearcher searcher) {
        QueryParser qp = new QueryParser("title", new StandardAnalyzer());
        Query titleQuery;
        TopDocs hits = null;
		try {
			titleQuery = qp.parse(title);
			hits = searcher.search(titleQuery, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return hits;
	}

//	private static TopDocs searchByTitle(String title, IndexSearcher searcher) throws Exception {
//        QueryParser qp = new QueryParser("title", new StandardAnalyzer());
//        Query titleQuery = qp.parse(title);
//        TopDocs hits = searcher.search(titleQuery, 10);
//        return hits;
//    }
    
 
    private static TopDocs searchById(Integer id, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("id", new StandardAnalyzer());
        Query idQuery = qp.parse(id.toString());
        TopDocs hits = searcher.search(idQuery, 10);
        return hits;
    }
 
    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}
