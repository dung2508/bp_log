package vn.edu.clevai.bplog.service;

import javax.mail.MessagingException;
import java.util.Map;

public interface EmailService {
	void send(String to, String subject, String htmlBody, Map<String, String> attachments) throws MessagingException;
}
