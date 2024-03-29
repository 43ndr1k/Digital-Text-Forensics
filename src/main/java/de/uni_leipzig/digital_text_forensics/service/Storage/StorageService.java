package de.uni_leipzig.digital_text_forensics.service.Storage;

import de.uni_leipzig.digital_text_forensics.model.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void init();

	void store(MultipartFile file);

	void updateFile(File file);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

	void deleteFile(Path file);

	boolean moveFiles(List<String> files);

	boolean moveSelectedFiles(List<String> files);


}
