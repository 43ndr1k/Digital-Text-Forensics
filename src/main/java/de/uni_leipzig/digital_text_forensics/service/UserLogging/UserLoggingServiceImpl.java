package de.uni_leipzig.digital_text_forensics.service.UserLogging;

import de.uni_leipzig.digital_text_forensics.dao.UserLoggingDao;
import de.uni_leipzig.digital_text_forensics.model.UserLog;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserLoggingServiceImpl implements UserLoggingService {

	@Autowired
	UserLoggingDao userLoggingDao;

	private void emptyClientId(String clientId) {
		if (clientId == null || clientId.equals("")) {
			throw new RuntimeException();
		}
	}

	private void emptyUserLog(UserLog userLog) {
		if (userLog == null) {
			throw new RuntimeException();
		}
	}

	@Override
	public List<UserLog> findByClientIdOrderByDateDesc(String clientId) {
		emptyClientId(clientId);
		return userLoggingDao.findByClientIdOrderByDateDesc(clientId);
	}

	@Override
	public UserLog updateUserLog(UserLog userLog) {
		emptyUserLog(userLog);
		return userLoggingDao.save(userLog);
	}
}
