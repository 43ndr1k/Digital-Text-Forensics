package de.uni_leipzig.digital_text_forensics.dao;

import de.uni_leipzig.digital_text_forensics.model.Query;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DocQueryDao  {

	//List<Query> findDistinctTop5ByQueryStartingWith(String query);
	//@org.springframework.data.jpa.repository.Query("select distinct q.query from Query q where q.query like :qu%")
	List<String> findQuerysStartsWith(String qu);
}
