package de.uni_leipzig.digital_text_forensics.service.Storage;

import de.uni_leipzig.digital_text_forensics.model.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageService.class);

	private final Path rootLocation;
	private final Path xmlLocation;
	private final Path xmlFlies2;
	private final Path pdfFiles2;

	@Value("${pdfDocDir}")
	private String pdfDocs;

	@Value("${xmlFileDir}")
	private String xmlFiles;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.xmlLocation = Paths.get(properties.getXmlLocation());
		this.xmlFlies2 = Paths.get(properties.getXmlFiles());
		this.pdfFiles2 = Paths.get(properties.getPdfDocs());
	}

	@Override
	public void store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory "
								+ filename);
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(filename),
					StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	@Override
	public void updateFile(File file) {

		try {
			if (file == null) {
				throw new StorageException("Failed to store empty file ");
			}
			if (file.getFilename().contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory "
								+ file.getFilename());
			}

			byte data[] = file.getText().getBytes();
			InputStream inputStream = new ByteArrayInputStream(data);

			Files.copy(inputStream, this.rootLocation.resolve(file.getFilename()),
					StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file " + file.getFilename(), e);
		}

	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void deleteFile(Path file) {
		String filename = "";
		try {
			Files.delete(file);
			filename = file.getFileName().toString().substring(0, file.getFileName().toString().length()-3) + "xml";
			if (Files.exists(load(filename))) {
				Files.delete(load(filename));
			}
		}
		catch (IOException e) {
			throw new StorageFileNotFoundException("Could not delete file: " + filename, e);
		}

	}

	@Override
	public boolean moveFiles(List<String> files) {
		final boolean[] test = { true };
		files.forEach(s -> {
			String t = s.substring(0, s.length()-3);
			t += "xml";
			Path pdfPath = load(s);
			Path pdfPath1 = FileSystems.getDefault().getPath(pdfDocs + "/" + s);

			Path xmlPath = FileSystems.getDefault().getPath(xmlLocation + "/" +t);
			Path xmlPath1 = FileSystems.getDefault().getPath(xmlFiles + "/" + t);

			try {

				if (Files.exists(pdfPath) && Files.exists(xmlPath)) {
					Files.move(pdfPath, pdfPath1,
							StandardCopyOption.REPLACE_EXISTING);
					Files.move(xmlPath, xmlPath1,
							StandardCopyOption.REPLACE_EXISTING);
				} else {
					LOGGER.error("File {} or file {} not exists", pdfPath.getFileName(), xmlPath.getFileName());
					test[0] = false;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		return test[0];
	}

	@Override
	public boolean moveSelectedFiles(List<String> files) {
		final boolean[] test = { true };
		files.forEach(s -> {
			String t = s.substring(0, s.length()-3);
			t += "xml";

			Path xmlPath = load(t);
			Path xmlPath1 = FileSystems.getDefault().getPath(xmlLocation + "/" + t);

			try {

				if (Files.exists(xmlPath)) {
					Files.move(xmlPath, xmlPath1,
							StandardCopyOption.REPLACE_EXISTING);
				} else {
					LOGGER.error("File {} not exists", xmlPath.getFileName());
					test[0] = false;
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		return test[0];
	}

	@Override
	public void init() {
		try {
			LOGGER.info("Create init dirs");
			Files.createDirectories(rootLocation);
			Files.createDirectories(xmlLocation);
			Files.createDirectories(pdfFiles2);
			Files.createDirectories(xmlFlies2);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
