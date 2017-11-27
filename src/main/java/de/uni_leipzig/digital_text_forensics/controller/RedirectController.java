package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import de.uni_leipzig.digital_text_forensics.service.LoggingDoc.LoggingDocService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {

	@Autowired
	Searcher searcher;

	@Autowired
	LoggingDocService loggingDocService;

	@RequestMapping(method = RequestMethod.GET, path = "/url")
	public RedirectView redirect(
			@RequestParam
					Long docId,
			@RequestParam
					String query,
			@RequestParam
					String title) {

		SearchResult searchResult = searcher.getDocument(docId, query);

		//response.setContentType("application/pdf");

		String url = searchResult.getDocUrl().getHref().substring(15, searchResult.getDocUrl().getHref().length() - 4);

		RedirectView ret = new RedirectView("/pdf?file=" + url);
		ret.setContentType("application/pdf");
		return ret;
	}

	@RequestMapping(value = "/pdf", method = RequestMethod.GET, produces = "application/pdf")
	public StreamingResponseBody getFile(
			@RequestParam("file")
					String file, HttpServletResponse response) throws IOException {

		String url = "pdfDocs/" + file + ".pdf";
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=" + file);
		//response.setHeader("Content-Disposition", "attachment; filename=" + file);
		InputStream inputStream = new FileInputStream(new File(url));
		return outputStream -> {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStream.write(data, 0, nRead);
			}
		};

	}

}
