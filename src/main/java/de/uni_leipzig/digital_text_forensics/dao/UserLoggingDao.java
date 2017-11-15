package de.uni_leipzig.digital_text_forensics.dao;

import de.uni_leipzig.digital_text_forensics.model.UserLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoggingDao extends JpaRepository<UserLog, Long> {

	List<UserLog> findByClientIdOrderByDateDesc(String clientId);
}
