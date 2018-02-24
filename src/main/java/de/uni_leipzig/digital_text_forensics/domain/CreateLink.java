package de.uni_leipzig.digital_text_forensics.domain;

import de.uni_leipzig.digital_text_forensics.controller.RedirectController;
import java.io.IOException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

/**
 * Creating the public web url for one document.
 */
public class CreateLink {

	public static Link createDefaultLink() {

		Link link1 = (ControllerLinkBuilder.linkTo(RedirectController.class)
				.withRel("targetUrl"));

		return link1;
	}

	public static Link createLink(Link link1, Long docID, String query) {

		String a = String.format("/pdf/?docId=%s&query=%s", docID, query);
		return new Link(link1.getHref() + a)
				.withRel("targetUrl");

/*		try {
			return ControllerLinkBuilder.linkTo(
					ControllerLinkBuilder.methodOn(RedirectController.class).getFile(docID, query))
					.withRel("targetUrl");
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;*/
	}

}
