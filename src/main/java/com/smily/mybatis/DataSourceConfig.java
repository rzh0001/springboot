package com.smily.mybatis;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DataSourceConfig implements ResourceLoaderAware, InitializingBean {
	public static final String JDBC_PREFIX = "jdbc.";
	public static final String JDBC_JNDI_NAME = "jdbc.jndiName";
	public static final String JDBC_DATASOURCE_CLASS = "jdbc.dataSourceClass";
	public static final String JDBC_DRIVER_CLASS = "jdbc.driverClass";
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USERNAME = "jdbc.username";
	public static final String JDBC_PASSWORD = "jdbc.password";
	public static final String JDBC_VALIDATION_QUERY = "jdbc.validationQuery";
	public static final String JDBC_SCRIPTS = "jdbc.scripts";
	public static final String JDBC_ENV_PREFIX = "jdbc.ds.";
	private static final String DRUID_DATASOURCE_CLASS = "com.alibaba.druid.pool.DruidDataSource";
	private static final String DRUID_CACHE_MAX = "maxPoolPreparedStatementPerConnectionSize";
	public final Logger logger;

	@Value("${jdbc.jndiName:}")
	private String jndiName;

	@Value("${jdbc.dataSourceClass:}")
	private String dataSourceClass;

	@Value("${jdbc.driverClass:}")
	private String driverClass;

	@Value("${jdbc.url:}")
	private String url;

	@Value("${jdbc.username:}")
	private String username;

	@Value("${jdbc.password:}")
	private String password;

	@Value("${jdbc.validationQuery:}")
	private String validationQuery;

	@Value("${jdbc.scripts:}")
	private String[] scriptPaths;

	@Autowired
	@Qualifier("env")
	private Properties env;

	@Autowired
	private Decryptor decryptor;
	private ResourceLoader resourceLoader;
	private Map<String, String> dsProperties;
	private DatabaseDialect dialect;
	private List<Resource> scripts;
	public static final byte[] E4D228617D3B4E79B6D27BA53EBA4F08 = { -19, 119, 10, -9, 108, 81, 95, -74, 1, 13, -42,
			-112, -104, 57, -95, 31, -15, -125, -15, -78, -55, -95, 70, -50, -97, 7, 83, 83, 73, -43, -35, -96, -89, -6,
			105, 71, 88, -36, -68, -119, 68, 74, 92, -111, 93, 47, -69, -78 };

	public DataSourceConfig() {
		logger = LoggerFactory.getLogger(super.getClass());

		dsProperties = new LinkedHashMap();

		dialect = DatabaseDialect.unknown;
		scripts = new ArrayList();
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getDataSourceClass() {
		return dataSourceClass;
	}

	public void setDataSourceClass(String dataSourceClass) {
		this.dataSourceClass = dataSourceClass;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getScriptPaths() {
		return scriptPaths;
	}

	public void setScriptPaths(String[] scriptPaths) {
		this.scriptPaths = scriptPaths;
	}

	public Properties getEnv() {
		return env;
	}

	public void setEnv(Properties env) {
		this.env = env;
	}

	public Decryptor getDecryptor() {
		return decryptor;
	}

	public void setDecryptor(Decryptor decryptor) {
		this.decryptor = decryptor;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Map<String, String> getDsProperties() {
		return dsProperties;
	}

	public String getDsProperty(String key) {
		return (dsProperties.get(key));
	}

	public void setDsProperty(String key, String value) {
		dsProperties.put(key, value);
	}

	public void setDialect(DatabaseDialect dialect) {
		this.dialect = dialect;
		if (driverClass == null)
			driverClass = dialect.getDriverClass();
	}

	public DatabaseDialect getDialect() {
		return dialect;
	}

	public List<Resource> getScripts() {
		return scripts;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		jndiName = StringUtils.stripToNull(jndiName);
		url = StringUtils.stripToNull(url);

		Assert.isTrue(isReady(), "{jdbc.jndiName} or {jdbc.url} must be set");

		init();

		logger.debug("dataSourceConfig = {}", this);
	}

	public boolean isReady() {
		return ((jndiName != null) || (url != null));
	}

	public void init() throws Exception {
		jndiName = StringUtils.stripToNull(jndiName);
		url = StringUtils.stripToNull(url);

		if ((dialect == null) || (dialect == DatabaseDialect.unknown)) {
			if (url != null)
				dialect = DatabaseDialect.fromJdbcUrl(url);
			else {
				dialect = DatabaseDialect.unknown;
			}
		}

		dataSourceClass = StringUtils.stripToNull(dataSourceClass);
		driverClass = StringUtils.stripToNull(driverClass);
		if (driverClass == null) {
			if (url != null) {
				try {
					driverClass = DriverManager.getDriver(url).getClass().getName();
				} catch (SQLException e) {
					driverClass = dialect.getDriverClass();
				}
			} else {
				driverClass = dialect.getDriverClass();
			}
		}

		validationQuery = StringUtils.stripToNull(validationQuery);
		if (validationQuery == null) {
			validationQuery = dialect.getValidationQuery();
		}

		username = StringUtils.stripToNull(username);
		password = StringUtils.stripToNull(password);
		if (password != null) {
			password = decryptor.decrypt(password);
		}

		if ((dialect == DatabaseDialect.oracle) && ("com.alibaba.druid.pool.DruidDataSource".equals(dataSourceClass)))
			dsProperties.put("maxPoolPreparedStatementPerConnectionSize", "100");
		Iterator i$;
		if (env != null) {
			for (i$ = env.keySet().iterator(); i$.hasNext();) {
				Object obj = i$.next();
				String key = (String) obj;
				if (key.startsWith("jdbc.ds.")) {
					dsProperties.put(key.substring("jdbc.ds.".length()), env.getProperty(key));
				}
			}
		}

		if (scriptPaths != null) {
			ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);

			for (String scriptPath : scriptPaths) {
				scriptPath = StringUtils.stripToNull(scriptPath);
				if (scriptPath != null)
					scripts.addAll(Arrays.asList(resolver.getResources(scriptPath)));
			}
		}
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);

		if (StringUtils.isNotEmpty(jndiName)) {
			builder.append("jndiName", jndiName);
		}

		if (StringUtils.isNotEmpty(dataSourceClass)) {
			builder.append("dataSourceClass", dataSourceClass);
		}

		if (StringUtils.isNotEmpty(driverClass)) {
			builder.append("driverClass", driverClass);
		}

		if (dialect != null) {
			builder.append("dialect", dialect);
		}

		if (StringUtils.isNotEmpty(url)) {
			builder.append("url", url);
		}

		if (StringUtils.isNotEmpty(username)) {
			builder.append("username", username);
		}

		if ((scripts != null) && (scripts.size() > 0)) {
			for (Resource script : scripts) {
				builder.append("script", script.getDescription());
			}
		}

		if ((dsProperties != null) && (!(dsProperties.isEmpty()))) {
			for (Map.Entry entry : dsProperties.entrySet()) {
				builder.append("ds." + ((String) entry.getKey()), entry.getValue());
			}
		}

		return builder.toString();
	}
}