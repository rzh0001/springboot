package com.smily.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.smily.mail.MailHelper;

@Service
public class MailService {

	@Value("${spring.mail.username}")
	String myMail;

	@Autowired
	private MailHelper mailHelper;

	public void sendSimpleMail(String mail) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(myMail);
		message.setTo(mail);
		message.setSubject("主题：简单邮件");
		message.setText("测试邮件内容");

		mailHelper.sendMail(message);
	}
}
