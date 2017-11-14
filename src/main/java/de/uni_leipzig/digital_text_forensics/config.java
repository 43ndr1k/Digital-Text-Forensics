package de.uni_leipzig.digital_text_forensics;

import de.uni_leipzig.digital_text_forensics.domain.Pager;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
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
	public Searcher searcher() {
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

}
