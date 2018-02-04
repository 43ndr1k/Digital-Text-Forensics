package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.dto.SearchResult;
import de.uni_leipzig.digital_text_forensics.logging.Logging;
import de.uni_leipzig.digital_text_forensics.lucene.Searcher;
import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;
import de.uni_leipzig.digital_text_forensics.model.Query;
import de.uni_leipzig.digital_text_forensics.model.UserLog;
import de.uni_leipzig.digital_text_forensics.service.LoggingDoc.LoggingDocService;
import de.uni_leipzig.digital_text_forensics.service.UserLogging.UserLoggingService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RedirectController {

	@Autowired
	Searcher querySearcher;

	@Autowired
	LoggingDocService loggingDocService;

	private static final Logger LOGGER = LoggerFactory.getLogger(Logging.class);

	@Autowired
	UserLoggingService userLoggingService;

	/**
	 * Not found page
	 * @return ModelAndView
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/not-found")
	public ModelAndView notFound() {
		String comesFrom = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getHeader("referer").trim();
		ModelAndView modelAndView = new ModelAndView("notFound");
		modelAndView.addObject("redirect", comesFrom);

		return modelAndView;
	}

	/**
	 * Get Pdf file.
	 * @param docId Long
	 * @param query String
	 * @return ResponseEntity with pdf Stream
	 * @throws IOException
	 */
	@RequestMapping(value = "/pdf", method = RequestMethod.GET)
	public ResponseEntity<?> getFile(
/*			@RequestParam("file")
					String file, */
			@RequestParam
					Long docId,
			@RequestParam
					String query
	) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		SearchResult searchResult = querySearcher.getDocument(docId, query);
		
		String url = "pdfDocs/" + searchResult.getDocUrl().getHref()
				.substring(9, searchResult.getDocUrl().getHref().length() - 3) + "pdf";

		
		File file = new File(url);
		if (!file.exists()) {
			return ResponseEntity.status(301).location(
					URI.create(ControllerLinkBuilder.linkTo(
							ControllerLinkBuilder.methodOn(RedirectController.class).notFound())
							.withRel("targetUrl").getHref())).build();
		}

		ResponseEntity responseEntity = logging(docId, query, searchResult.getTitle(), url);
		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			return responseEntity;
		}

		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
		headers.add("Access-Control-Allow-Headers", "Content-Type");
		headers.add("Content-Disposition", "inline; filename=" + url);
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		headers.setContentLength(file.length());
		InputStream inputStream = new FileInputStream(file);
		return new ResponseEntity<InputStreamResource>(
				new InputStreamResource(inputStream), headers, HttpStatus.OK);
	}

	/**
	 * Logging the user activities.
	 * @param docId Long
	 * @param query String
	 * @param title String
	 * @param goTo String
	 * @return ResponseEntity
	 */
	private ResponseEntity logging(Long docId, String query, String title, String goTo) {

		String clientId = RequestContextHolder.currentRequestAttributes().getSessionId();
		String comesFrom = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getHeader("referer").trim();

		LOGGER.info(
				"A user identified by '{}' is redirected due to a call to '{}' with arguments {} to the url '{}'. The user comes from '{}'.",
				clientId,
				"ModelAndView de.uni_leipzig.digital_text_forensics.controller.RedirectController.getFile(String, String)",
				String.format("[%s, %s, %s]", docId, query, title),
				goTo,
				comesFrom
		);

		List<UserLog> userLogList = userLoggingService.findByClientIdOrderByDateDesc(clientId);
		if (userLogList.size() != 0) {
			UserLog userLog = null;

			userLog = userLogList.get(0);
			LocalDateTime startDate = userLog.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			LocalDateTime endDate = LocalDateTime.now();

			Long time = Duration.between(startDate, endDate).getSeconds();
			userLog.setTime(time);
			UserLog l = userLoggingService.updateUserLog(userLog);
			if (l == null) {
				return new ResponseEntity("bad logging", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		UserLog userLog = new UserLog(clientId, new Date(), null, comesFrom,
				goTo);
		LoggingDocument loggingDocument = new LoggingDocument(docId, title,
				new Query(query.toLowerCase()),
				userLog);

		LoggingDocument ll = loggingDocService.updateDocCount(loggingDocument);
		if (ll == null) {
			return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity("bad logging", HttpStatus.OK);
	}

}
