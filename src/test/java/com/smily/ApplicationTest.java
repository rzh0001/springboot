package com.smily;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTest {

	private static final Log log = LogFactory.getLog(ApplicationTest.class);

	@Autowired
	private JavaMailSender mailSender;
	// @Autowired
	// private VelocityEngine velocityEngine;

	@Test
	public void sendSimpleMail() throws Exception {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("rzh0001@qq.com");
		message.setTo("1012927830@qq.com");
		message.setSubject("主题：简单邮件");
		message.setText("测试邮件内容");

		mailSender.send(message);
	}

	@Test
	public void sendAttachmentsMail() throws Exception {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom("rzh0001@qq.com");
		helper.setTo("1012927830@qq.com");
		helper.setSubject("主题：有附件");
		helper.setText("有附件的邮件");

		FileSystemResource file = new FileSystemResource(
				new File("/Users/Smily/Documents/stsWs/new/src/main/resources/static/img/weixin.jpg"));
		helper.addAttachment("附件-1.jpg", file);
		helper.addAttachment("附件-2.jpg", file);

		mailSender.send(mimeMessage);
	}

	@Test
	public void sendInlineMail() throws Exception {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom("rzh0001@qq.com");
		helper.setTo("1012927830@qq.com");
		helper.setSubject("主题：嵌入静态资源");
		helper.setText("<html><body><img src=\"cid:weixin\" ></body></html>", true);

		FileSystemResource file = new FileSystemResource(
				new File("/Users/Smily/Documents/stsWs/new/src/main/resources/static/img/weixin.jpg"));
		helper.addInline("weixin", file);

		mailSender.send(mimeMessage);
	}

	// @Test
	// public void sendTemplateMail() throws Exception {
	//
	// MimeMessage mimeMessage = mailSender.createMimeMessage();
	//
	// MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	// helper.setFrom("rzh0001@qq.com");
	// helper.setTo("1012927830@qq.com");
	// helper.setSubject("主题：模板邮件");
	//
	//
	// Map<String, Object> model = new HashMap<String, Object>();
	// model.put("username", "Smily");
	// String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
	// "mail_template.vm", "UTF-8", model);
	// helper.setText(text, true);
	//
	// mailSender.send(mimeMessage);
	// }

}
