package de.uni_leipzig.digital_text_forensics.dao;

import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggingDocDao extends JpaRepository<LoggingDocument, Long> {

	LoggingDocument findByDocId(Long docId);

}
