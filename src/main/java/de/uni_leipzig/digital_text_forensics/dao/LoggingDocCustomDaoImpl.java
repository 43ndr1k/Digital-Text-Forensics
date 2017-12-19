package de.uni_leipzig.digital_text_forensics.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoggingDocCustomDaoImpl implements LoggingDocCustomDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Double getClickTimeByDocId(Long docId) {
		final Query query = this.entityManager.createQuery(

				"select coalesce(avg(ull.time), 0) "
						+ "from "
						+ "LoggingDocument ld "
						+ "join ld.userLogList ull "
						+ "where "
						+ "ld.docId = :docId"
		);
		query.setParameter("docId", docId);

		return (Double) query.getSingleResult();

	}


}
