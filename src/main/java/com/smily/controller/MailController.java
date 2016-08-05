package com.smily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smily.service.MailService;

@Controller
public class MailController {

	@Autowired
	private MailService mailService;

	@RequestMapping("/mail/{mail}")
	public void sendMail(@RequestParam("mail") String mail) {
		mailService.sendSimpleMail(mail);
	}
}
