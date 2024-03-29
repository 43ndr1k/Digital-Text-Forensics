package de.uni_leipzig.digital_text_forensics.domain;

import de.uni_leipzig.digital_text_forensics.controller.SearchController;
import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.dto.SearchResultPage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.lucene.search.ScoreDoc;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class Pager {

	public static final int RESULTS_PER_PAGE = 10;

	/**
	 * Split the ResultList to pages
	 * @param searchDocList List<ScoreDoc>
	 * @param currentPage int
	 * @return List<ScoreDoc>
	 */
	public List<ScoreDoc> split(List<ScoreDoc> searchDocList, int currentPage) {
		if (searchDocList == null || searchDocList.size() == 0) {
			return new ArrayList<>();
		}

		int from = (currentPage * RESULTS_PER_PAGE) - RESULTS_PER_PAGE;
/*		if (from == 0) {
			from = 1;
		}*/

		int to = currentPage * RESULTS_PER_PAGE;

		if (searchDocList.size() < 10) {
			to = searchDocList.size();
		}

		if ((searchDocList.size() - from) < 10) {
			to = searchDocList.size();
		}
		List<ScoreDoc> ret = searchDocList.subList(from, to);

		return ret;
	}

	private static LinkBuilder searchLink(String query, Integer page) {
		return ControllerLinkBuilder
				.linkTo(ControllerLinkBuilder.methodOn(SearchController.class).searchPage(query, page));
	}

	/**
	 * Create PaginationLinks
	 * @Autor Maik Fröbe
	 */
	public static void injectPaginationLinks(SearchResultPage searchResultPage) {
		if (searchResultPage == null) {
			return;
		}

		int currentPage = Math.max(searchResultPage.getPage(), 1);
		int maxPage = (((int) searchResultPage.getTotalResults() - 1) / RESULTS_PER_PAGE) + 1;

		if (currentPage > 1) {
			searchResultPage.setPreviousPage(searchLink(searchResultPage.getQuery(), currentPage - 1).withRel("prev"));
		}

		if (currentPage < maxPage) {
			searchResultPage.setNextPage(searchLink(searchResultPage.getQuery(), currentPage + 1).withRel("next"));
		}

		searchResultPage.setNamedPageLinksAfter(namedPaginationLinksInRange(currentPage + 1,
				Math.min(currentPage + 4, maxPage + 1), searchResultPage));

		searchResultPage.setNamedPageLinksBefore(namedPaginationLinksInRange(Math.max(currentPage - 3, 1),
				currentPage, searchResultPage));

		Link firstPageLink = searchLink(searchResultPage.getQuery(), 1).withRel("1");
		if (currentPage > 1 && !searchResultPage.getNamedPageLinksBefore().contains(firstPageLink)) {
			searchResultPage.setFirstPageLink(firstPageLink);
		}

		Link lastPageLink = searchLink(searchResultPage.getQuery(), maxPage).withRel(String.valueOf(maxPage));
		if (currentPage < (maxPage - 1) && !searchResultPage.getNamedPageLinksAfter().contains(lastPageLink)) {
			searchResultPage.setLastPageLink(lastPageLink);
		}
	}

	/**
	 * Creatig Links for namedPaginationLinks
	 * @param startInclusive int
	 * @param endExclusive int
	 * @param searchResultPage SearchResultPage
	 * @return List<Link>
	 */
	private static List<Link> namedPaginationLinksInRange(int startInclusive, int endExclusive,
			SearchResultPage searchResultPage) {
		return IntStream.range(startInclusive, endExclusive)
				.mapToObj(i -> searchLink(searchResultPage.getQuery(), i).withRel(String.valueOf(i)))
				.collect(Collectors.toList());
	}

}
