package de.uni_leipzig.digital_text_forensics.service.DocQuery;

import de.uni_leipzig.digital_text_forensics.dao.DocQueryDao;
import de.uni_leipzig.digital_text_forensics.model.Query;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocQueryServiceImpl implements DocQueryService {

	@Autowired
	DocQueryDao docQueryDao;

	@Override
	public List<Query> findByQueryStartingWith(String query) {
		return docQueryDao.findTop10ByQueryStartingWith(query);
	}
}
