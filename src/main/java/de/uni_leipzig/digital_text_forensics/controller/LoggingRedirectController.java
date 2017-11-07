package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
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

	@RequestMapping(method = RequestMethod.GET, path = "/url")
	public RedirectView redirect(
			@RequestParam
					Integer docId) {

		return new RedirectView(searcher.getDocument(docId));
	}
}
