package de.uni_leipzig.digital_text_forensics.controller;

import de.uni_leipzig.digital_text_forensics.lucene.LuceneConstants;
import de.uni_leipzig.digital_text_forensics.lucene.XMLFileIndexer;
import de.uni_leipzig.digital_text_forensics.model.File;
import de.uni_leipzig.digital_text_forensics.service.Mail.MailService;
import de.uni_leipzig.digital_text_forensics.model.MetaData;
import de.uni_leipzig.digital_text_forensics.service.Storage.StorageService;
import de.uni_leipzig.digital_text_forensics.service.Storage.StorageFileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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
public class FileController {

	private final StorageService storageService;
	private final MailService mailService;

	@Value("${spring.mail.send.text1}")
	private String mailUploadText;

	@Value("${spring.mail.subject1}")
	private String subject;

	@Autowired
	public FileController(StorageService storageService, MailService mailService) {
		this.storageService = storageService;
		this.mailService = mailService;
	}

	@GetMapping("/upload")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll()
				.map(
						path -> MvcUriComponentsBuilder.fromMethodName(FileController.class,
								"serveFile", path.getFileName().toString()).build().toString())
				.filter(path -> path.endsWith(".pdf"))
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
			RedirectAttributes redirectAttributes, @ModelAttribute MetaData metaData, HttpServletRequest request)
			throws URISyntaxException, IOException, MessagingException {

		String contentType = file.getContentType();

		if (!contentType.equals("application/pdf")) {
			redirectAttributes.addFlashAttribute("message",
					"File is not a pdf. " + file.getOriginalFilename() + "!");
			return "redirect:/upload";
		}

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message",
					"File is Empty. " + file.getOriginalFilename() + "!");
			return "redirect:/upload";  // return null to in

		}

		storageService.store(file);

		/* todo:
		 * save metata. create xml template
		 */

		/* todo:
		 * Erstellen der xml files.
		 */

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		String a = "http://" + request.getLocalAddr() + ":" + request.getLocalPort();

		mailService.send(subject, mailUploadText, file.getOriginalFilename(), a);

		return  "redirect:/upload";
	}

	@GetMapping("/uploaded-files")
	public String uploadedFiles(Model model) {

		model.addAttribute("files", storageService.loadAll().map(
				path -> path.getFileName().toString()).filter(path -> path.endsWith(".pdf"))
				.collect(Collectors.toList()));

		return "uploadedFiles";
	}

	/**
	 * Show File.
	 * @param filename
	 * @return
	 */
	@GetMapping("/show-file/{filename:.+}")
	public String showFile(@PathVariable String filename, Model model, RedirectAttributes redirectAttributes) throws IOException {

		filename = filename.substring(0, filename.length()-3) + "xml";

		if (!Files.exists(storageService.load(filename))) {
			redirectAttributes.addFlashAttribute("message2",
					"Metadata file not found");
			return  "redirect:/uploaded-files";
		}

		Resource file = storageService.loadAsResource(filename);

		String text = "";
		try{
			InputStream is = file.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}
			br.close();

		}catch(IOException e){
			e.printStackTrace();
		}

		model.addAttribute("file", new File(text));
		model.addAttribute("filename", file.getFilename());
		return "file";
	}

	/**
	 * Post a new file.
	 */
	@PostMapping("/update-file/{filename:.+}")
	public String updateFile(@PathVariable String filename, RedirectAttributes redirectAttributes, @ModelAttribute
			File file)
			throws URISyntaxException, IOException, MessagingException {

		file.setFilename(filename);
		storageService.updateFile(file);

		redirectAttributes.addFlashAttribute("message2",
				"You successfully updated file " + file.getFilename() + "!");


		return  "redirect:/uploaded-files";
	}

	@PostMapping("/indexing")
	public String indexing(Model model, RedirectAttributes redirectAttributes, @RequestParam
			List<String> files) {

		boolean test = storageService.moveSelectedFiles(files);
		if (!test) {
			redirectAttributes.addFlashAttribute("refiles", files);
			redirectAttributes.addFlashAttribute("message",
					"Index not updaded");
			return "redirect:uploaded-files";
		}

		XMLFileIndexer xmlFileIndexer = null;
		try {
			xmlFileIndexer = new XMLFileIndexer(LuceneConstants.INDEX_PATH, "upload-dir/selectedMetadata");
			xmlFileIndexer.run();
		} catch (IOException e) {
			e.printStackTrace();
		}

		test = storageService.moveFiles(files);

		if (test) {
			redirectAttributes.addFlashAttribute("refiles", files);
			redirectAttributes.addFlashAttribute("message",
					"Index updaded");
		} else {
			redirectAttributes.addFlashAttribute("message",
					"Index not updaded");
		}

		return "redirect:uploaded-files";
	}

	/**
	 * Exception Handling for File not found.
	 */
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
