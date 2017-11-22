package de.uni_leipzig.digital_text_forensics.dao;

import de.uni_leipzig.digital_text_forensics.model.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocQueryDao extends JpaRepository<Query, Long> {

	List<Query> findByQueryStartingWith(String query);

}
