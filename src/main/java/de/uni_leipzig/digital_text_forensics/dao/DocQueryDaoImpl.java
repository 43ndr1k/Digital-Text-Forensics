package de.uni_leipzig.digital_text_forensics.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class DocQueryDaoImpl implements DocQueryDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<String> findQuerysStartsWith(String qu) {
		final Query query = this.entityManager.createQuery(

				"select distinct query from Query q where q.query like :qu"
		);
		query.setParameter("qu", qu + "%");
		query.setMaxResults(5);

		return query.getResultList();
	}
}
