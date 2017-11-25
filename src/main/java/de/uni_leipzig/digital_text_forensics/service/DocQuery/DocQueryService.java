package de.uni_leipzig.digital_text_forensics.service.DocQuery;

import de.uni_leipzig.digital_text_forensics.model.Query;
import java.util.List;

public interface DocQueryService {

	List<Query> findByQueryStartingWith(String query);

}
