package com.smily.mybatis;

import org.apache.log4j.PropertyConfigurator;

public class MybatisGenerator {

	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("war/WEB-INF/log4j.properties");
		GeneratorMain.main(args);
	}
}
