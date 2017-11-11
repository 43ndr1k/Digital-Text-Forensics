package de.uni_leipzig.digital_text_forensics.controller;

import static de.uni_leipzig.digital_text_forensics.domain.Pager.injectPaginationLinks;

import de.uni_leipzig.digital_text_forensics.domain.Pager;
import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.dto.SearchResultPage;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

	@Autowired
	Searcher searcher;

	@Autowired
	Pager pager;

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

		//Suchen
		//TopDocs queryTopDocs;// = searcherComponent.search(originalQuery, topNForSearchResult);

/*		Pair<Integer, List<ScoreDoc>> lastPageNumberAndContent = determineLastPageNumberAndContent(queryTopDocs);
		searchResultPage.setTotalResults(queryTopDocs.totalHits);
		searchResultPage.setResultsOnPage(new Pager().mapScoreDocsToSearchResults(lastPageNumberAndContent.getRight()));
		searchResultPage.setPage(lastPageNumberAndContent.getLeft());
		injectPaginationLinks(searchResultPage);*/

		List<Document> searchDocList;
		List<SearchResult> searchResultList;
		try {
			searchDocList = searcher.search(query);
			List<Document> split = pager.split(searchDocList, currentPage);
			searchResultPage.setTotalResults(searchDocList.size());
			searchResultList = pager.mapDocumentListToSearchResults(split);
			searchResultPage.setResultsOnPage(searchResultList);
			searchResultPage.setPage(currentPage);
			injectPaginationLinks(searchResultPage);
		}
		catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
							
/*		searchResultPage.setTotalResults(15);
		searchResultPage.setResultsOnPage(searchResultList);
		searchResultPage.setPage(currentPage);

		injectPaginationLinks(searchResultPage);*/

		modelAndView.addObject("searchResultPage", searchResultPage);

		return modelAndView;
	}

}