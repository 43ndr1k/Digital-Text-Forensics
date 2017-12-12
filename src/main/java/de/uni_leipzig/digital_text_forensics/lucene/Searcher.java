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
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class Searcher {

	public final String indexLocation = "LuceneIndex";
	private StandardAnalyzer analyzer = new StandardAnalyzer();
	private File indexFile = new File(indexLocation);
	private IndexReader indexReader;
	private Query q;
	private Directory directory;
	private final int RESULT_COUNT = 30;

	private IndexSearcher searcher = null;
	public static final String[] PRE_TAGS = new String[] { "" };
	public static final String[] POST_TAGS = new String[] { "" };

	public List<SearchResult> search(String query) throws IOException, ParseException {
		indexReader = DirectoryReader.open(FSDirectory.open(Paths
				.get(indexLocation)));//IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		searcher = new IndexSearcher(indexReader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(RESULT_COUNT);

		directory = FSDirectory.open(indexFile.toPath());
		//q = new QueryParser(LuceneConstants.CONTENTS, analyzer).parse(query);
		q  = new MultiFieldQueryParser(
            new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE},
            analyzer).parse(new String[] {query, query}, new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer);
		
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
						searchResult = new SearchResult(
								query,
								(long) topDoc.doc,
								searcher.doc(topDoc.doc).get(LuceneConstants.TITLE),
								searcher.doc(topDoc.doc).get(LuceneConstants.AUTHOR),
								searcher.doc(topDoc.doc).get(LuceneConstants.FILE_NAME),
								searcher.doc(topDoc.doc).get(LuceneConstants.PUBLICATION_DATE),
								getSnippet(topDoc.doc, query),
								new Link(searcher.doc(topDoc.doc).get(LuceneConstants.FILE_PATH)));
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
		QueryScorer queryScorer = new QueryScorer(q, LuceneConstants.CONTENTS);
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
		System.out.println(stopTime - startTime);//TEST
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
		System.out.println(stopTime - startTime);//TEST
		return snippet;
	}

	private FastVectorHighlighter makeHighlighter() {
		FragListBuilder fragListBuilder = new SimpleFragListBuilder(200);
		FragmentsBuilder fragmentBuilder = new SimpleFragmentsBuilder(PRE_TAGS, POST_TAGS);
		return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);
	}

}
