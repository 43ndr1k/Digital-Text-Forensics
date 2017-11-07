package de.uni_leipzig.digital_text_forensics.dto;

import de.uni_leipzig.digital_text_forensics.controller.LoggingRedirectController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class SearchResult {

	private Integer docId;

	private String title;

	private String snippet;

	private Link webUrl;

	private Link docUrl;

	public SearchResult(Integer docId, String title, String snippet, Link docUrl) {
		this.docId = docId;
		this.title = title;
		this.snippet = snippet;
		this.webUrl = createLink(docId);
		this.docUrl = docUrl;

	}

	public SearchResult() {
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
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

	private static Link createLink(Integer docID) {
		return
				ControllerLinkBuilder.linkTo(
						ControllerLinkBuilder.methodOn(LoggingRedirectController.class).redirect(docID))
						.withRel("targetUrl");
	}
}
