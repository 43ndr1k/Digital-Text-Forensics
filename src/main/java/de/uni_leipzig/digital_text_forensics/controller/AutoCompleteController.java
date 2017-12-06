package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.model.Query;
import de.uni_leipzig.digital_text_forensics.service.DocQuery.DocQueryService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.FuzzySuggester;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AutoCompleteController {

	@Autowired
	DocQueryService docQueryService;

	private final FuzzySuggester fuzzySuggester = new FuzzySuggester(new RAMDirectory(), "sadsa", new SimpleAnalyzer());

	@RequestMapping(method = RequestMethod.GET, path = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> autoComplete(
			@RequestParam(defaultValue = "")
					String query) throws IOException {

		List<String> list = docQueryService.findByQueryStartingWith(query);

/*		List<String> list = list1.stream().map(query1 -> {
			return query1.getQuery().trim();
		}).collect(Collectors.toList());*/
		List<LookupResult> ret = fuzzySuggester.lookup(query.toLowerCase(), Boolean.FALSE, 5);

		list.addAll(ret.stream()
				.map(i -> String.valueOf(i.key))
				.collect(Collectors.toList()));

		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

	@Autowired
	public AutoCompleteController(Dictionary dict) throws IOException {
		fuzzySuggester.build(dict);
	}

}
