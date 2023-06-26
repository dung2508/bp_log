package vn.edu.clevai.bplog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.edu.clevai.bplog.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender emailSender;

	private final String environment = "TEST";

	private final String from = "LMS System <lms@clevai.edu.vn>";

	@Override
	public void send(String to, String subject, String htmlBody, Map<String, String> attachments) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom(from);
		helper.setTo(to);
		setBodyAndSend(subject, htmlBody, attachments, helper, message);

		log.info(
				"Sent the email '{}' to recipients: {}.",
				subject,
				to
		);
	}


	private void setBodyAndSend(String subject, String htmlBody,
								Map<String, String> attachments,
								MimeMessageHelper helper,
								MimeMessage message)
			throws MessagingException {

		if (!"prod".equals(environment)) {
			helper.setSubject(environment + "-" + subject);
		} else {
			helper.setSubject(subject);
		}
		helper.setText(htmlBody, true);

		if (!Objects.isNull(attachments)) {
			for (Map.Entry<String, String> entry : attachments.entrySet()) {
				String attachmentName = entry.getKey();
				String fileLocation = entry.getValue();
				FileSystemResource file = new FileSystemResource(new File(fileLocation));
				helper.addAttachment(attachmentName, file);
			}
		}
		emailSender.send(message);
	}

}
