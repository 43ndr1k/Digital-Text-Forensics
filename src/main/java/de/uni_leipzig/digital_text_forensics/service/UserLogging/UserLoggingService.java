package de.uni_leipzig.digital_text_forensics.service.UserLogging;

import de.uni_leipzig.digital_text_forensics.model.UserLog;
import java.util.List;

public interface UserLoggingService {

	List<UserLog> findByClientId(String clientId);

	UserLog update(UserLog userLog);
}
