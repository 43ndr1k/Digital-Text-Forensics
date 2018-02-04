package de.uni_leipzig.digital_text_forensics.lucene;

import de.uni_leipzig.digital_text_forensics.domain.CreateLink;
import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.service.LoggingDoc.LoggingDocService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class Searcher {

	@Autowired
	LoggingDocService loggingDocService;

	@Autowired
	private StandardAnalyzer analyzer;

	@Autowired
	private IndexReader indexReader;

	@Autowired
	private IndexSearcher searcher;

	private MultiFieldQueryParser multiFieldQueryParser;

	private static final String[] PRE_TAGS = new String[] { "" };
	private static final String[] POST_TAGS = new String[] { "" };

	public Searcher() throws IOException {
		HashMap<String, Float> boosts = new HashMap<String, Float>();
		boosts.put(LuceneConstants.CONTENTS, 0.2f);
		boosts.put(LuceneConstants.TITLE, 0.8f);
		multiFieldQueryParser = new MultiFieldQueryParser(
				new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer, boosts);
	}

	public List<ScoreDoc> search(String query) throws IOException, ParseException {

		TopScoreDocCollector collector = TopScoreDocCollector.create(LuceneConstants.MAX_SEARCH);
		searcher.search(MultiFieldQueryParser
				.parse(new String[] {query, query}, new String[] {LuceneConstants.CONTENTS, LuceneConstants.TITLE}, analyzer), collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		//scoring
		double time =0;
		Long clicks=0L;
		int refCount=0;
		Document d;

		for (int i = 0; i < hits.length; ++i) {
			time = loggingDocService.getClickTimeByDocId((long) hits[i].doc);
			d = searcher.doc(hits[1].doc);
			refCount = Integer.parseInt(d.getField(LuceneConstants.REF_COUNT).stringValue()); //numericValue().intValue();
			clicks = loggingDocService.getClickCountAndFindByDocId((long) hits[i].doc);
			hits[i].score = hits[i].score + (float) time + refCount + clicks;
		}

		return Arrays.asList(hits);
	}

	/**
	 * Mapping from ScoreDoc to SearchResult lists.
	 * @param docs ScoreDoc List
	 * @param query String
	 * @return List of SearchResults
	 */
	public List<SearchResult> mapDocumentListToSearchResults(List<ScoreDoc> docs, String query) {

		Link link1 = CreateLink.createDefaultLink();
		return docs.parallelStream().map(topDoc -> {
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
								new Link(searcher.doc(topDoc.doc).get(LuceneConstants.FILE_PATH)),
								CreateLink.createLink(link1, (long) topDoc.doc,query)
						);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					return searchResult;
				}
		).parallel().collect(Collectors.toList());
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
					new Link(searcher.doc(docId.intValue()).get(LuceneConstants.FILE_PATH))
			);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getSnippet(int docId, String queryString) {

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
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return snippet;
	}

	/*private String getSnippet2(int docId, String queryString) {

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
		return snippet;
	}*/

	private FastVectorHighlighter makeHighlighter() {
		FragListBuilder fragListBuilder = new SimpleFragListBuilder(200);
		FragmentsBuilder fragmentBuilder = new SimpleFragmentsBuilder(PRE_TAGS, POST_TAGS);
		return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);
	}

}
