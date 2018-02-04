package de.uni_leipzig.digital_text_forensics.controller;

import static de.uni_leipzig.digital_text_forensics.domain.Pager.injectPaginationLinks;

import de.uni_leipzig.digital_text_forensics.domain.Pager;
import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.dto.SearchResultPage;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

	@Autowired
	Searcher querySearcher;

	@Autowired
	Pager pager;

	/**
	 * Searching method
	 * @param query String
	 * @param currentPage Long
	 * @return ModelAndView
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/")
	public ModelAndView searchPage(
			@RequestParam(defaultValue = "")
					String query,
			@RequestParam(defaultValue = "1")
					Integer currentPage) {

		ModelAndView modelAndView = new ModelAndView("search");
		SearchResultPage searchResultPage = new SearchResultPage();

		if (query.equals("")) {
			modelAndView.addObject("searchResultPage", searchResultPage);
			return modelAndView;
		}

		searchResultPage.setQuery(query);
		List<SearchResult> searchResultList;

		try {
			List<ScoreDoc> list = querySearcher.search(query.toLowerCase());
			List<ScoreDoc> split = pager.split(list, currentPage);
			searchResultList = querySearcher.mapDocumentListToSearchResults(split,query);
			searchResultPage.setTotalResults(list.size());
			searchResultPage.setResultsOnPage(searchResultList);
			searchResultPage.setPage(currentPage);
			injectPaginationLinks(searchResultPage);
		}
		catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modelAndView.addObject("searchResultPage", searchResultPage);

		return modelAndView;
	}

}
