package de.uni_leipzig.digital_text_forensics.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_logging")
public class UserLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String clientId;
	private Date date;
	private Long time = 0L;
	private String comesFrom;
	private String goTo;

	public UserLog() {

	}

	public UserLog(String clientId, Date date, Long time, String comesFrom, String goTo) {
		this.clientId = clientId;
		this.date = date;
		this.time = time;
		this.comesFrom = comesFrom;
		this.goTo = goTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getComesFrom() {
		return comesFrom;
	}

	public void setComesFrom(String comesFrom) {
		this.comesFrom = comesFrom;
	}

	public String getGoTo() {
		return goTo;
	}

	public void setGoTo(String goTo) {
		this.goTo = goTo;
	}
}
