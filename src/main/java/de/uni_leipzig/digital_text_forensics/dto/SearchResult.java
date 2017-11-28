package de.uni_leipzig.digital_text_forensics.dto;

import de.uni_leipzig.digital_text_forensics.controller.RedirectController;
import java.io.IOException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class SearchResult {

	private Long docId;

	private String title;

	private String snippet;

	private Link webUrl;

	private Link docUrl;

	private String query;

	public SearchResult(String query, Long docId, String title, String snippet, Link docUrl) {
		this.query = query;
		this.docId = docId;
		this.title = title;
		this.snippet = snippet;
		this.webUrl = createLink(docId, query);
		this.docUrl = docUrl;

	}

	public SearchResult() {
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
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

	public Link getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(Link webUrl) {
		this.webUrl = webUrl;
	}

	public Link getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(Link docUrl) {
		this.docUrl = docUrl;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	private static Link createLink(Long docID, String query) {

/*		Link link1 = (ControllerLinkBuilder.linkTo(RedirectController.class)
				.withRel("targetUrl"));

		String a = String.format("/pdf/?docId=%s&query=%s", docID, query);
		Link link = new Link(link1.getHref() + a)
				.withRel("targetUrl");
		return link;*/

		try {
			return ControllerLinkBuilder.linkTo(
					ControllerLinkBuilder.methodOn(RedirectController.class).getFile(docID, query))
					.withRel("targetUrl");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}
}
