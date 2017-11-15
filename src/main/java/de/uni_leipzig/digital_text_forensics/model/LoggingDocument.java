package de.uni_leipzig.digital_text_forensics.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "logging_documente")
public class LoggingDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private Long docId;

	@Column(nullable = false)
	private String docTitle;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Query> query = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL)
	private List<UserLog> userLogList = new ArrayList<>();

	private Long clickCount = 0L;

	public LoggingDocument() {
	}

	public LoggingDocument(Long id, Long docId, String docTitle, Query query, Long clickCount, UserLog userLog) {
		this.id = id;
		this.docId = docId;
		this.docTitle = docTitle;
		this.query.add(query);
		this.userLogList.add(userLog);
		this.clickCount = clickCount;
	}

	public LoggingDocument(Long docId, String docTitle, Query query, UserLog userLog) {
		this.docId = docId;
		this.docTitle = docTitle;
		this.query.add(query);
		this.userLogList.add(userLog);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public Long getClickCount() {
		return clickCount;
	}

	public void setClickCount(Long clickCount) {
		this.clickCount = clickCount;
	}

	public List<Query> getQuery() {
		return query;
	}

	public void setQuery(List<Query> query) {
		this.query = query;
	}

	public List<UserLog> getUserLogList() {
		return userLogList;
	}

	public void setUserLogList(List<UserLog> userLogList) {
		this.userLogList = userLogList;
	}
}
