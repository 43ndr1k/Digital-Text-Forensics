package de.uni_leipzig.digital_text_forensics.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "doc_querys")
public class Query {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String query;

	public Query() {

	}

	public Query(String query) {
		this.query = query;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
