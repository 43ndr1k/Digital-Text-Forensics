package de.uni_leipzig.digital_text_forensics;

import de.uni_leipzig.digital_text_forensics.domain.Pager;
import de.uni_leipzig.digital_text_forensics.lucene.LuceneConstants;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.suggest.DocumentDictionary;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableAutoConfiguration
public class config {

	@Bean
	public Searcher querySearcher() throws IOException {
		return new Searcher();
	}

	@Bean
	public Pager pager() {
		return new Pager();
	}

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer ret = new DefaultCookieSerializer();

		ret.setCookieName("client_id");
		ret.setCookiePath("/");
		ret.setCookieMaxAge(Integer.MAX_VALUE);

		return ret;
	}

	@Bean
	public static SessionRepository<?> sessionStore() {
		return new MapSessionRepository();
	}

	@Bean
	public static Dictionary dictionary() throws IOException {
		return new DocumentDictionary(DirectoryReader.open(FSDirectory.open(Paths.get("LuceneIndex"))),
				LuceneConstants.TITLE,
				LuceneConstants.TITLE);
	}

	@Bean
	public static IndexReader indexReader(@Value("LuceneIndex") String indexDirectory) throws IOException {
		return DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectory)));
	}

	@Bean
	public static IndexSearcher searcher() throws IOException {
		return new IndexSearcher(indexReader(LuceneConstants.INDEX_PATH));
	}

	@Bean
	public static StandardAnalyzer analyzer()
	{
		return new StandardAnalyzer();
	}

	@Bean
	public static TopScoreDocCollector collector () {
		return TopScoreDocCollector.create(LuceneConstants.MAX_SEARCH);
	}

}
