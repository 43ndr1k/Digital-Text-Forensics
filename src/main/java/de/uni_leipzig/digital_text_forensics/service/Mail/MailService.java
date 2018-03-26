package de.uni_leipzig.digital_text_forensics.service.Mail;



import groovy.util.logging.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.h2.mvstore.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Slf4j
@Service
public class MailService {

	@Value("${spring.mail.from1}")
	private String from;

	@Value("${spring.mail.to1}")
	private String to;

	@Value("${spring.mail.personal1}")
	private String personal;

	@Value("${spring.mail.user1}")
	private String user;

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	public MailService() {
	}

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String username;

	/**
	 * Send a email.
	 * @param subject
	 * @param text
	 * @param files
	 * @throws MessagingException
	 */
	public void send(String subject, String text, String files) throws
			MessagingException {

		final Context ctx = new Context();

		final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true, "UTF-8");
		message.setSubject(subject);
		message.setTo(to);
		ctx.setVariable("text", text);
		ctx.setVariable("files", files);
		ctx.setVariable("user", user);
		ctx.setVariable("personal", personal);

		try {
			message.setFrom(new InternetAddress(username, personal));
		} catch (UnsupportedEncodingException e) {
		}

		final String htmlContent = this.templateEngine.process( "emailTemplate", ctx);
		message.setText(htmlContent, true);

		this.mailSender.send(mimeMessage);
	}


}
