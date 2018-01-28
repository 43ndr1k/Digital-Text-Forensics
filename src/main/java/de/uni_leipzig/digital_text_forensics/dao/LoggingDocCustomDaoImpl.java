package de.uni_leipzig.digital_text_forensics.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LoggingDocCustomDaoImpl implements LoggingDocCustomDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * get average time from document with docId x.
	 * @param docId Long
	 * @return Double
	 */
	//coalesce(avg(ull.time), 0)
	@Override
	public Double getClickTimeByDocId(Long docId) {
		final Query query = this.entityManager.createQuery(

				"select avg(ull.time) "
						+ "from "
						+ "LoggingDocument ld "
						+ "join ld.userLogList ull "
						+ "where "
						+ "ld.docId = :docId"
		);
		query.setParameter("docId", docId);

		Object o = query.getSingleResult();
		double d = 0;
		if (o != null) {
			d = (Double) o;
		}
		return d;

	}


}
