package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;
import de.uni_leipzig.digital_text_forensics.model.Query;
import de.uni_leipzig.digital_text_forensics.service.LoggingDocService;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoggingRedirectController {

	@Autowired
	Searcher searcher;

	@Autowired
	LoggingDocService loggingDocService;

	@RequestMapping(method = RequestMethod.GET, path = "/url")
	public RedirectView redirect(
			@RequestParam
					Long docId,
			@RequestParam
					String query) {

		SearchResult searchResult = searcher.getDocument(docId, query);
		LoggingDocument loggingDocument = mapSearchResultToLoggingDoc(searchResult);
		loggingDocService.updateDocCount(loggingDocument);

		return new RedirectView(searchResult.getDocUrl().getHref());
	}

	private LoggingDocument mapSearchResultToLoggingDoc(SearchResult searchResult) {

		List<Query> queryList = new LinkedList<>();
		queryList.add(new Query(searchResult.getQuery()));
		return new LoggingDocument(searchResult.getDocId(), searchResult.getTitle(), queryList);

	}
}
