package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.service.UserLogging.UserLoggingService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AutoCompleteController {

	@Autowired
	UserLoggingService userLoggingService;

	@RequestMapping(method = RequestMethod.GET, path = "/auto-complete")
	public ResponseEntity<List<String>> autoComplete() {

		List<String> list = new ArrayList<>();

		list.add("Test1");
		list.add("test2");
		list.add("test3");
		list.add("Welt");
		list.add("Hallo");

		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

}
