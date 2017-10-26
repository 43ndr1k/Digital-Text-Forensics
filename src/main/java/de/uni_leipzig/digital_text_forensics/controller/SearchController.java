package de.uni_leipzig.digital_text_forensics.controller;

import static de.uni_leipzig.digital_text_forensics.domain.Pager.injectPaginationLinks;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.dto.SearchResultPage;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.search.TopDocs;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchController {

	@RequestMapping(method = RequestMethod.GET, path = "/")
	public ModelAndView searchPage(
			@RequestParam(defaultValue = "")
					String query,
			@RequestParam(defaultValue = "1")
					Integer currentPage) {

		ModelAndView modelAndView = new ModelAndView();
		SearchResultPage searchResultPage = new SearchResultPage();

		if (query.equals("")) {
			modelAndView.addObject(searchResultPage);
			return modelAndView;
		}

		searchResultPage.setQuery(query);

		//Suchen
		TopDocs queryTopDocs;// = searcherComponent.search(originalQuery, topNForSearchResult);

/*		Pair<Integer, List<ScoreDoc>> lastPageNumberAndContent = determineLastPageNumberAndContent(queryTopDocs);
		searchResultPage.setTotalResults(queryTopDocs.totalHits);
		searchResultPage.setResultsOnPage(new Pager().mapScoreDocsToSearchResults(lastPageNumberAndContent.getRight()));
		searchResultPage.setPage(lastPageNumberAndContent.getLeft());
		injectPaginationLinks(searchResultPage);*/

		SearchResult searchResult = new SearchResult();
		searchResult.setSnippet("Snippel");
		searchResult.setTitle("Titel");
		searchResult.setUrl(new Link("Link"));

		List<SearchResult> searchResultList = new LinkedList<>();
		searchResultList.add(searchResult);

		searchResultPage.setTotalResults(10);
		searchResultPage.setResultsOnPage(searchResultList);
		searchResultPage.setPage(1);
		injectPaginationLinks(searchResultPage);

		modelAndView.addObject(searchResultPage);
		return modelAndView;
	}

}
