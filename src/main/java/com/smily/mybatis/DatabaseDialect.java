package com.smily.mybatis;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DatabaseDialect {
	unknown, oracle, db2, sqlserver, mysql, sqlite, hsqldb;

	private static final Logger logger;
//	private final String driverClass;
	private String driverClass;
//	private final String validationQuery;
	private String validationQuery;

	public String getDriverClass() {
		return driverClass;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public static DatabaseDialect parse(String dialect) {
		DatabaseDialect ret = EnumHelper.parse(DatabaseDialect.class, dialect);
		if (ret == null) {
			if ("mssql".equalsIgnoreCase(dialect))
				ret = sqlserver;
			else {
				ret = unknown;
			}
		}
		return ret;
	}

	public static DatabaseDialect fromJdbcUrl(String url) {
		if (url == null) {
			return unknown;
		}

		for (DatabaseDialect dialect : values()) {
			if (url.contains(":" + dialect.name() + ":")) {
				return dialect;
			}
		}

		return unknown;
	}

	public static DatabaseDialect fromDataSource(DataSource dataSource) throws SQLException {
		String url = null;

		logger.trace("dataSource = {}", dataSource.getClass().getName());
		try {
			url = BeanUtils.getProperty(dataSource, "url");
			logger.trace("Get JDBC URL from {dataSource.url}");
		} catch (Exception ignored) {
		}
		if (url == null)
			try {
				url = BeanUtils.getProperty(dataSource, "jdbcUrl");
				logger.trace("Get JDBC URL from {dataSource.jdbcUrl}");
			} catch (Exception ignored) {
			}
		if (url == null) {
			Connection connection = dataSource.getConnection();
			Throwable localThrowable2 = null;
			try {
				url = connection.getMetaData().getURL();
				logger.trace("Get JDBC URL from {connection.metaData}");
			} catch (Throwable localThrowable1) {
			} finally {
				if (connection != null)
					if (localThrowable2 != null)
						try {
							connection.close();
						} catch (Throwable x2) {
							localThrowable2.addSuppressed(x2);
						}
					else
						connection.close();
			}
		}
		logger.trace("url = {}", url);

		if (url == null) {
			throw new IllegalArgumentException("Cannot retrieve jdbc url from dataSource");
		}

		return fromJdbcUrl(url);
	}

	static {
		logger = LoggerFactory.getLogger(DatabaseDialect.class);
	}
}
