package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.model.Query;
import de.uni_leipzig.digital_text_forensics.service.DocQuery.DocQueryService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AutoCompleteController {

	@Autowired
	DocQueryService docQueryService;

	@RequestMapping(method = RequestMethod.GET, path = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> autoComplete(
			@RequestParam(defaultValue = "")
					String query) {

		List<Query> list1 = docQueryService.findByQueryStartingWith(query);

		List<String> list = list1.stream().map(query1 -> {
			return query1.getQuery().trim();
		}).collect(Collectors.toList());

		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

}
