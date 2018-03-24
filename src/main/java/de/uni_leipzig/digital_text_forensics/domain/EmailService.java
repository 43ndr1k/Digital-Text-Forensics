package de.uni_leipzig.digital_text_forensics.domain;

import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.expression.Lists;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class EmailService {

	@Autowired
	private it.ozimov.springboot.mail.service.EmailService emailService;

	@Value("${spring.mail.from1}")
	private String from;

	@Value("${spring.mail.to1}")
	private String to;

	@Value("${spring.mail.personal1}")
	private String personal;

	public EmailService() {
	}

	public void sendEmail(String subject, String body) {
		DefaultEmail email = null;
		try {
			email = DefaultEmail.builder()
					.from(new InternetAddress(from,
							personal))
					.to(newArrayList(
							new InternetAddress(to,
									"Cleon I")))
					.subject(subject)
					.body(body)
					.encoding("UTF-8").build();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		emailService.send(email);
	}

}
