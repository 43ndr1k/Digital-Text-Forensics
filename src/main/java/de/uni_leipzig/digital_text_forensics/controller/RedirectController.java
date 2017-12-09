package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import de.uni_leipzig.digital_text_forensics.service.LoggingDoc.LoggingDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

	@Autowired
	Searcher searcher;

	@Autowired
	LoggingDocService loggingDocService;

	@RequestMapping(method = RequestMethod.GET, path = "/url")
	public RedirectView redirect(
			@RequestParam
					Long docId,
			@RequestParam
					String query,
			@RequestParam
					String title) {

		SearchResult searchResult = searcher.getDocument(docId, query);

		return new RedirectView(searchResult.getDocUrl().getHref());
	}

}