package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.service.UserLogging.UserLoggingService;
import java.util.ArrayList;
import java.util.List;
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
	UserLoggingService userLoggingService;

	@RequestMapping(method = RequestMethod.GET, path = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<String>> autoComplete(
			@RequestParam(defaultValue = "")
					String tag) {

		List<String> list = new ArrayList<>();

		if (tag.equals("te")) {
			list.add("Test1");
			list.add("Test2");
			list.add("Test6");
			list.add("Test9");
			list.add(tag);

		}
		else {

			list.add("Welt");
			list.add("Welt2");
			list.add("Welt3");
			list.add("Welt5");

		}

		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

}
