package com.smily.mybatis;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@NotScan
@Configuration
@ImportResource({ "classpath:com/gingkoo/gal/spring/core.xml", "classpath:com/gingkoo/gal/spring/env.xml",
		"classpath:com/gingkoo/gal/spring/facility.xml" })
public class GeneratorMain implements Callable<Integer> {

	@Autowired
	private Generator generator;

	@Bean
	public Generator generator() {
		return new Generator();
	}

	@Bean
	public DataSourceConfig dataSourceConfig() {
		return new DataSourceConfig();
	}

	@Override
	public Integer call() throws Exception {
		generator.generate();
		return Integer.valueOf(0);
	}

	public static void main(String[] args) throws Exception {
		System.exit(ContextHelper.call(GeneratorMain.class, new String[0]).intValue());
	}
}