package de.uni_leipzig.digital_text_forensics.dto;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.springframework.hateoas.Link;

public class SearchResult {
	public static final String INDEX_FIELD_TITLE = "title";

	public static final String INDEX_FIELD_CONTENT = "content";

	public static final String INDEX_FIELD_LINK = "link";

	private String title;

	private String snippet;

	private Link url;

	// TODO: 26.10.17
	// url anpassen
	public SearchResult(Pair<Document, Integer> result) {
		setTitle(result.getLeft().get(INDEX_FIELD_TITLE));
		setSnippet(result.getLeft().get(INDEX_FIELD_CONTENT));
		setUrl(createTargetLink(result.getRight()));
	}

	public SearchResult(String title, String snippet, Link url) {
		this.title = title;
		this.snippet = snippet;
		this.url = url;
	}

	public SearchResult() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public Link getUrl() {
		return url;
	}

	public void setUrl(Link url) {
		this.url = url;
	}

	private static Link createTargetLink(Integer docID) {
		return null;//ControllerLinkBuilder.linkTo(
		//ControllerLinkBuilder.methodOn(RedirectController.class).redirect(docID))
		//.withRel("targetUrl");
	}
}
