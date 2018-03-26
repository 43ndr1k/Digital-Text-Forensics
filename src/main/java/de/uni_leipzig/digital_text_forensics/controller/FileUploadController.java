package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.service.Mail.MailService;
import de.uni_leipzig.digital_text_forensics.model.MetaData;
import de.uni_leipzig.digital_text_forensics.service.Storage.StorageService;
import de.uni_leipzig.digital_text_forensics.service.Storage.StorageFileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileUploadController {

	private final StorageService storageService;
	private final MailService mailService;

	@Value("${spring.mail.send.text1}")
	private String mailUploadText;

	@Value("${spring.mail.subject1}")
	private String subject;

	@Autowired
	public FileUploadController(StorageService storageService, MailService mailService) {
		this.storageService = storageService;
		this.mailService = mailService;
	}

	@GetMapping("/upload")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toString())
				.collect(Collectors.toList()));
		model.addAttribute("metaData", new MetaData());
		return "uploadForm";
	}

	/**
	 * Get upload files.
	 * @param filename
	 * @return
	 */
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	/**
	 * Post a new file.
	 */
	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes, @ModelAttribute MetaData metaData, HttpServletResponse resp)
			throws URISyntaxException, IOException, MessagingException {

		if (file.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_NO_CONTENT,
					"File not uploaded " ); // explicitely put error message in request
			return null;  // return null to in

		}
		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		mailService.send(subject, mailUploadText, file.getOriginalFilename());

		return  "redirect:/upload";
	}

	/**
	 * Exception Handling for File not found.
	 */
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
