package com.smily.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailHelper {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(MimeMessage mimeMessage) {
		mailSender.send(mimeMessage);
	}

	public void sendMail(SimpleMailMessage simpleMailMessage) {
		mailSender.send(simpleMailMessage);
	}

}
