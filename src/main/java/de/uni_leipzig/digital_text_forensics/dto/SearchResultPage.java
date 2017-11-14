package de.uni_leipzig.digital_text_forensics.dto;

import java.util.List;
import org.springframework.hateoas.Link;

public class SearchResultPage {
	private String query;

	private int page;

	private List<SearchResult> resultsOnPage;

	private long totalResults;

	private Link nextPage;

	private Link previousPage;

	private Link firstPageLink;

	private Link lastPageLink;

	private List<Link> namedPageLinksBefore;

	private List<Link> namedPageLinksAfter;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<SearchResult> getResultsOnPage() {
		return resultsOnPage;
	}

	public void setResultsOnPage(List<SearchResult> resultsOnPage) {
		this.resultsOnPage = resultsOnPage;
	}

	public long getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(long totalResults) {
		this.totalResults = totalResults;
	}

	public Link getNextPage() {
		return nextPage;
	}

	public void setNextPage(Link nextPage) {
		this.nextPage = nextPage;
	}

	public Link getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(Link previousPage) {
		this.previousPage = previousPage;
	}

	public Link getFirstPageLink() {
		return firstPageLink;
	}

	public void setFirstPageLink(Link firstPageLink) {
		this.firstPageLink = firstPageLink;
	}

	public Link getLastPageLink() {
		return lastPageLink;
	}

	public void setLastPageLink(Link lastPageLink) {
		this.lastPageLink = lastPageLink;
	}

	public List<Link> getNamedPageLinksBefore() {
		return namedPageLinksBefore;
	}

	public void setNamedPageLinksBefore(List<Link> namedPageLinksBefore) {
		this.namedPageLinksBefore = namedPageLinksBefore;
	}

	public List<Link> getNamedPageLinksAfter() {
		return namedPageLinksAfter;
	}

	public void setNamedPageLinksAfter(List<Link> namedPageLinksAfter) {
		this.namedPageLinksAfter = namedPageLinksAfter;
	}
}
