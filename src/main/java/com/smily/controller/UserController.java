package com.smily.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.smily.bean.Users;
import com.smily.dao.UserDetailsServiceDAO;
import com.smily.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserDetailsServiceDAO userDetailsServiceDAO;

	@Autowired
	private UserService userService;

	// handle when logged user go to login page
	@RequestMapping("/login")
	public String login() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			return "login";
		} else {
			return "home";
		}
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String registration(Users newUser) {
		try {
			if (userDetailsServiceDAO.loadUserEntityByUsername(newUser.getUsername()) != null) {
				return "redirect:" + "/login?registration&error";
			} else {
				// userDetailsServiceDAO.saveUser(newUser);
				userService.addUser(newUser);
				return "redirect:" + "/login?registration&success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:" + "/login?registration&errorServer";
		}
	}

}
