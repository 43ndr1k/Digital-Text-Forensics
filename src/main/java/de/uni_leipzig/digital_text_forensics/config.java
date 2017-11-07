package de.uni_leipzig.digital_text_forensics;

import de.uni_leipzig.digital_text_forensics.domain.Pager;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
