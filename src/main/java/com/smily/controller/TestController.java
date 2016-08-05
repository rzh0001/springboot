package com.smily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smily.bean.Users;
import com.smily.service.UserService;

@RestController
public class TestController {
	@Autowired
	private UserService userService;

	@RequestMapping("/1")
	public Users test() {
		Users user = userService.findUserByUserName("rzh");
		return user;

	}
}
