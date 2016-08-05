package com.smily.service;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smily.bean.Users;
import com.smily.dao.UsersMapper;
import com.smily.enums.ROLE;

@Service
public class UserService {

	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private PasswordHelper passwordHelper;

	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

	public Users findUserByUserName(String username) {

		return usersMapper.selectByPrimaryKey(username);
	}

	public void addUser(Users user) {
		user.setPasswordStr(user.getPassword());
		user.setRole(ROLE.USER.toString());
		user.setSalt(randomNumberGenerator.nextBytes().toHex());
		user.setPassword(passwordHelper.encryptPassword(user.getPasswordStr(), user.getSalt()));
		usersMapper.insert(user);
	}

}
