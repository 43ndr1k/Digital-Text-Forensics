package de.uni_leipzig.digital_text_forensics.domain;

import de.uni_leipzig.digital_text_forensics.controller.SearchController;
import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.dto.SearchResultPage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * @Autor Maik Fröbe
 */
public class Pager {

	private static final int RESULTS_PER_PAGE = 10;

	public static Pair<Integer, List<ScoreDoc>> determineLastPageNumberAndContent(TopDocs topDocs) {
		if (topDocs == null || topDocs.scoreDocs == null || topDocs.scoreDocs.length == 0) {
			return Pair.of(1, new ArrayList<>());
		}

		final int page = ((topDocs.scoreDocs.length - 1) / RESULTS_PER_PAGE);
		List<ScoreDoc> ret = new ArrayList<>();

		for (int i = page * RESULTS_PER_PAGE; i < topDocs.scoreDocs.length; i++) {
			ret.add(topDocs.scoreDocs[i]);
		}

		return Pair.of(page + 1, ret);
	}

	private static LinkBuilder searchLink(String query, Integer page) {
		return ControllerLinkBuilder
				.linkTo(ControllerLinkBuilder.methodOn(SearchController.class).searchPage(query, page));
	}

	public List<SearchResult> mapScoreDocsToSearchResults(List<ScoreDoc> scoreDocs) {
		return scoreDocs.stream()
				//.map(topDoc -> Pair.of(searcherComponent.doc(topDoc.doc), topDoc.doc))
				.map(SearchResult::new)
				.collect(Collectors.toList());
	}

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

	private static List<Link> namedPaginationLinksInRange(int startInclusive, int endExclusive,
			SearchResultPage searchResultPage) {
		return IntStream.range(startInclusive, endExclusive)
				.mapToObj(i -> searchLink(searchResultPage.getQuery(), i).withRel(String.valueOf(i)))
				.collect(Collectors.toList());
	}

}
