package de.uni_leipzig.digital_text_forensics.service.LoggingDoc;

import de.uni_leipzig.digital_text_forensics.dao.LoggingDocDao;
import de.uni_leipzig.digital_text_forensics.model.LoggingDocument;
import de.uni_leipzig.digital_text_forensics.model.Query;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingDocServiceImpl implements LoggingDocService {

	@Autowired
	LoggingDocDao loggingDocDao;

	private void emptyId(Long id) {
		if (id == null) {
			throw new RuntimeException();
		}
	}

	private void emptyDoc(LoggingDocument loggingDocument) {
		if (loggingDocument == null || loggingDocument.equals("")) {
			throw new RuntimeException();
		}
	}

	@Override
	public LoggingDocument findbyId(Long id) {
		emptyId(id);
		return loggingDocDao.findOne(id);
	}

	@Override
	public LoggingDocument findByDocId(Long id) {
		emptyId(id);
		return loggingDocDao.findByDocId(id);
	}

	@Override
	public LoggingDocument create(LoggingDocument loggingDocument) {
		emptyDoc(loggingDocument);
		return loggingDocDao.save(loggingDocument);
	}

	@Override
	public LoggingDocument updateDoc(LoggingDocument loggingDocument) {
		emptyDoc(loggingDocument);
		LoggingDocument loggingDocument1 = loggingDocDao.findByDocId(loggingDocument.getDocId());
		if (loggingDocument1 != null) {
			loggingDocument1.setDocId(loggingDocument.getDocId());
			loggingDocument1.setClickCount(loggingDocument.getClickCount());
			loggingDocument1.setDocTitle(loggingDocument.getDocTitle());

			Query query = loggingDocument.getQuery().get(0);
			if (loggingDocument1.getQuery().stream()
					.noneMatch(query1 -> query1.getQuery().trim().equals((query.getQuery().trim())))) {
				loggingDocument1.getQuery().add(loggingDocument.getQuery().get(0));
			}

			return loggingDocDao.save(loggingDocument1);
		}
		else {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public LoggingDocument updateDocCount(LoggingDocument loggingDocument) {
		emptyDoc(loggingDocument);
		LoggingDocument loggingDocument1 = loggingDocDao.findByDocId(loggingDocument.getDocId());
		if (loggingDocument1 != null) {
			loggingDocument1.setDocId(loggingDocument.getDocId());
			Long count = loggingDocument1.getClickCount();
			loggingDocument1.setClickCount(++count);
			loggingDocument1.setDocTitle(loggingDocument.getDocTitle());

			Query query = loggingDocument.getQuery().get(0);
			if (loggingDocument1.getQuery().stream()
					.noneMatch(query1 -> query1.getQuery().trim().equals((query.getQuery().trim())))) {
				loggingDocument1.getQuery().addAll(loggingDocument.getQuery());
			}
			loggingDocument1.getUserLogList().addAll(loggingDocument.getUserLogList());

			return loggingDocDao.save(loggingDocument1);
		}
		else {
			loggingDocument.setClickCount(1L);
			return loggingDocDao.save(loggingDocument);
		}
	}

}
