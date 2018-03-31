package de.uni_leipzig.digital_text_forensics.service.LoggingDoc;

import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;

public interface LoggingDocService {

	LoggingDocument findbyId(Long id);

	LoggingDocument findByDocId(Long id);

	LoggingDocument create(LoggingDocument loggingDocument);

	LoggingDocument updateDoc(LoggingDocument loggingDocument);

	LoggingDocument updateDocCount(LoggingDocument loggingDocument);

	Long getClickCountAndFindByDocId(Long id);

	Double getClickTimeByDocId(Long id);


}
