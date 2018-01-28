package de.uni_leipzig.digital_text_forensics.lucene;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.service.LoggingDoc.LoggingDocService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class Searcher {

	@Autowired
	LoggingDocService loggingDocService;


	/**
	 * double time = loggingDocService.getClickTimeByDocId(docId); -> Die durchnittliche Zeit in ms für ein Dokument, gesucht nach der docId
	 * Long clicks = loggingDocService.getClickCountAndFindByDocId(docId); -> Wie oft wurde insgesamt auf ein Document geklickt, gesucht nach der docId
	 */

	public final String indexLocation = "LuceneIndex";
	private StandardAnalyzer analyzer = new StandardAnalyzer();
	private File indexFile = new File(indexLocation);
	private IndexReader indexReader;
	private Query q;
	private MultiFieldQueryParser multiFieldQueryParser;
	private Directory directory;
	private final int RESULT_COUNT = 300;

	private IndexSearcher searcher = null;
	public static final String[] PRE_TAGS = new String[] { "" };
	public static final String[] POST_TAGS = new String[] { "" };

	public List<SearchResult> search(String query) throws IOException, ParseException {



		indexReader = DirectoryReader.open(FSDirectory.open(Paths
				.get(indexLocation)));//IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		searcher = new IndexSearcher(indexReader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(RESULT_COUNT);

		directory = FSDirectory.open(indexFile.toPath());
		HashMap<String, Float> boosts = new HashMap<String, Float>();
        boosts.put(LuceneConstants.CONTENTS, 0.2f);
        boosts.put(LuceneConstants.TITLE, 0.8f);
        multiFieldQueryParser = new MultiFieldQueryParser(
            new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer, boosts);
        searcher.search(multiFieldQueryParser.parse(new String[] {query, query}, new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer), collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs; 
        
        //scoring
        double time =0;
        Long clicks=0L;
        int refCount=0;
        Document d;

        for (int i = 0; i < hits.length; ++i) {
          time = loggingDocService.getClickTimeByDocId(new Long(hits[i].doc));
          d = searcher.doc(hits[1].doc);
          refCount = Integer.parseInt(d.getField(LuceneConstants.REF_COUNT).stringValue()); //numericValue().intValue();
          clicks = loggingDocService.getClickCountAndFindByDocId(new Long(hits[i].doc));
          //System.out.println(" Score: " + hits[i].score + " time: " + "refCount: " + refCount + "clicks: " + clicks);
          hits[i].score = hits[i].score + (float) time + refCount + clicks;
          //System.out.println("  New score: " + hits[i].score);
        }

		// display results

/*		List<Document> d = new ArrayList<>();
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			d.add(searcher.doc(docId));
		}*/
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		List<SearchResult> list = mapDocumentListToSearchResults(Arrays.asList(hits), query);

		stopwatch.stop();
		long timeTaken = stopwatch.getTime();
		System.out.println("Found " + hits.length + " hits. Time: " + timeTaken);
		return list;
	}

	/**
	 * Mapping from ScoreDoc to SearchResult lists.
	 * @param docs ScoreDoc List
	 * @param query String
	 * @return List of SearchResults
	 */
	private List<SearchResult> mapDocumentListToSearchResults(List<ScoreDoc> docs, String query) {

		return docs.stream().map(topDoc -> {
					SearchResult searchResult = null;

					try {
						searchResult = new SearchResult(
								query,
								(long) topDoc.doc,
								searcher.doc(topDoc.doc).get(LuceneConstants.TITLE),
								searcher.doc(topDoc.doc).get(LuceneConstants.AUTHOR),
								searcher.doc(topDoc.doc).get(LuceneConstants.FILE_NAME),
								searcher.doc(topDoc.doc).get(LuceneConstants.PUBLICATION_DATE),
								getSnippet(topDoc.doc, query),
								new Link(searcher.doc(topDoc.doc).get(LuceneConstants.FILE_PATH))
								);
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

	/**
	 * Get one search result document
	 * @param docId Long
	 * @param query String
	 * @return SearchResult
	 */
	public SearchResult getDocument(Long docId, String query) {
		try {
			return new SearchResult(query, docId, searcher.doc(docId.intValue()).get(LuceneConstants.FILE_NAME),
					getSnippet(docId.intValue(), query),
					new Link(searcher.doc(docId.intValue()).get(LuceneConstants.FILE_PATH)));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSnippet(int docId, String queryString) {

		long startTime, stopTime;//TEST
		startTime = System.currentTimeMillis();//TEST

		SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		QueryScorer queryScorer = null;
		try {
          queryScorer = new QueryScorer(multiFieldQueryParser.parse(new String[] {queryString, queryString}, new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer));
        } catch (ParseException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
		Highlighter highlighter = new Highlighter(htmlFormatter, queryScorer);

		Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 400); // set snippet size
		highlighter.setTextFragmenter(fragmenter);
		
		String snippet = null;
		try {
			Document document = searcher.doc(docId);// getDocument(scoreDoc.doc);
			String content = document.get(LuceneConstants.CONTENTS);
			TokenStream tokenStream = TokenSources.getAnyTokenStream(indexReader,
					docId, LuceneConstants.CONTENTS, document, new StandardAnalyzer());
			//snippet = highlighter.getBestFragment(tokenStream, content); 
			snippet = highlighter.getBestFragment(analyzer, LuceneConstants.CONTENTS, content);
			//System.out.println("SNIPPET: " + snippet);
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
		//System.out.println(stopTime - startTime);//TEST
		return snippet;
	}

	private String getSnippet2(int docId, String queryString) {

		long startTime, stopTime;//TEST
		startTime = System.currentTimeMillis();//TEST
//		Query query = null;
//		try {
//			query = new QueryParser("contents", analyzer).parse(queryString);
//		}
//		catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		FastVectorHighlighter highlighter = makeHighlighter();//new FastVectorHighlighter();
		FieldQuery fieldQuery = highlighter.getFieldQuery(q);

		String snippet = null;
		try {
			snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), docId, "contents", 10000);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopTime = System.currentTimeMillis(); //TEST
		//System.out.println(stopTime - startTime);//TEST
		return snippet;
	}

	private FastVectorHighlighter makeHighlighter() {
		FragListBuilder fragListBuilder = new SimpleFragListBuilder(200);
		FragmentsBuilder fragmentBuilder = new SimpleFragmentsBuilder(PRE_TAGS, POST_TAGS);
		return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);
	}

}
