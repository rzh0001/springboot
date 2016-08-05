package com.smily;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smily.bean.UsersExample;
import com.smily.dao.UsersMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTest2 {

	private static final Log log = LogFactory.getLog(ApplicationTest2.class);

	@Autowired
	private UsersMapper usersMapper;

	@Test
	public void test() {
		UsersExample usersExample = new UsersExample();
		usersExample.or();

	}
}
