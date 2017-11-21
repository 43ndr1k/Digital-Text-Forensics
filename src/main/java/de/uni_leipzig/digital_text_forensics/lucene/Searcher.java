package de.uni_leipzig.digital_text_forensics.lucene;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class Searcher {

	public final String indexLocation = "LuceneIndex";
	private StandardAnalyzer analyzer = new StandardAnalyzer();
	private final int RESULT_COUNT = 30;

	private IndexSearcher searcher = null;
    public static final String[] PRE_TAGS = new String[]{""};
    public static final String[] POST_TAGS = new String[]{""};

	public List<SearchResult> search(String query) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get(indexLocation)));//IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(RESULT_COUNT);

		Query q = new QueryParser("contents", analyzer).parse(query);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// display results
		System.out.println("Found " + hits.length + " hits.");
/*		List<Document> d = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			d.add(searcher.doc(docId));
		}*/

		return mapDocumentListToSearchResults(Arrays.asList(hits), query);
	}

	private List<SearchResult> mapDocumentListToSearchResults(List<ScoreDoc> docs, String query) {

		return docs.stream().map(topDoc -> {
					SearchResult searchResult = null;

					try {
						searchResult = new SearchResult(query, new Long(topDoc.doc),
								searcher.doc(topDoc.doc).get("filename"),
								getSnippet2(topDoc.doc, query), new Link(searcher.doc(topDoc.doc).get("path")));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					return searchResult;
				}
		).collect(Collectors.toList());

/*		return null;
		docs.stream()
				.map(document -> new SearchResult(document.get("filename"), "sinppel", new Link(document.get("path"))))
				.collect(Collectors.toList());*/

	}

	public SearchResult getDocument(Long docId, String query) {
		try {
			return new SearchResult(query, docId, searcher.doc(docId.intValue()).get("filename"),
					getSnippet(docId.intValue(), query), new Link(searcher.doc(docId.intValue()).get("path")));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSnippet(int docId, String queryString) {

	    long startTime, stopTime;//TEST
	    startTime = System.currentTimeMillis();//TEST
		Query query = null;
		try {
			query = new QueryParser("contents", analyzer).parse(queryString);
		}
		catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		QueryScorer queryScorer = new QueryScorer(query, "contents");
		Highlighter highlighter = new Highlighter(htmlFormatter, queryScorer);

		File indexFile = new File(indexLocation);
		Directory directory;
		IndexReader indexReader;
		String snippet = null;
		try {
			directory = FSDirectory.open(indexFile.toPath());
			indexReader = DirectoryReader.open(directory);
			Document document = searcher.doc(docId);// getDocument(scoreDoc.doc);
			String content = document.get("contents");
			TokenStream tokenStream = TokenSources.getAnyTokenStream(indexReader,
					docId, "contents", document, new StandardAnalyzer());
			snippet = highlighter.getBestFragment(tokenStream, content);
			//System.out.println(fragment);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTime = System.currentTimeMillis(); //TEST
		System.out.println(stopTime-startTime);//TEST
		return snippet;
	}
	
	private String getSnippet2(int docId, String queryString) { 
	  
	  long startTime, stopTime;//TEST
	  startTime = System.currentTimeMillis();//TEST
	  Query query = null;
      try {
          query = new QueryParser("contents", analyzer).parse(queryString);
      }
      catch (ParseException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
      }
	  FastVectorHighlighter highlighter = makeHighlighter();//new FastVectorHighlighter();
      FieldQuery fieldQuery = highlighter.getFieldQuery(query);
      
      String snippet = null;
      try {
        snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), docId, "contents", 10000);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      stopTime = System.currentTimeMillis(); //TEST
      System.out.println(stopTime-startTime);//TEST      
	  return snippet;
	  }
	
    private FastVectorHighlighter makeHighlighter() {
      FragListBuilder fragListBuilder = new SimpleFragListBuilder(200);
      FragmentsBuilder fragmentBuilder = new SimpleFragmentsBuilder(PRE_TAGS, POST_TAGS);
      return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);
  }

}
