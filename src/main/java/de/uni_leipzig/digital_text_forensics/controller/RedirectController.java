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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Controller
public class RedirectController {

	@Autowired
	Searcher searcher;

	@Autowired
	LoggingDocService loggingDocService;

	private static final Logger LOGGER = LoggerFactory.getLogger(Logging.class);

	@Autowired
	UserLoggingService userLoggingService;

/*	@RequestMapping(method = RequestMethod.GET, path = "/url")
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

		return new RedirectView("/pdf?file=" + url);
	}*/

	@RequestMapping(value = "/pdf", method = RequestMethod.GET, produces = "application/pdf")
	public StreamingResponseBody getFile(
/*			@RequestParam("file")
					String file, */
			@RequestParam
					Long docId,
			@RequestParam
					String query,
			HttpServletResponse response) throws IOException {

		SearchResult searchResult = searcher.getDocument(docId, query);
		String url = searchResult.getDocUrl().getHref().substring(15, searchResult.getDocUrl().getHref().length() - 4);
		url = "pdfDocs/" + url + ".pdf";

		ResponseEntity responseEntity = logging(docId, query, searchResult.getTitle(), url);
		if (responseEntity.getStatusCode() != HttpStatus.OK) {
			responseEntity.toString();
		}

		File file = new File(url);
		if (!file.exists()) {
			new ResponseEntity("File not found", HttpStatus.NOT_FOUND);
		}
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "inline; filename=" + url);
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

	private ResponseEntity logging(Long docId, String query, String title, String goTo) {

		String clientId = RequestContextHolder.currentRequestAttributes().getSessionId();
		String comesFrom = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getHeader("referer").trim();

		LOGGER.info(
				"A user identified by '{}' is redirected due to a call to '{}' with arguments {} to the url '{}'. The user comes from '{}'.",
				clientId,
				"ModelAndView de.uni_leipzig.digital_text_forensics.controller.RedirectController.getFile(String, String)",
				"[{}, {}, {}]", docId, query, title,
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
