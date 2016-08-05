package com.smily.mybatis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@NotScan
@Component
public class Generator {
	private static final String CONFIG_FILE = "generator.xml";
	private final Logger logger;

	@Autowired
	private DataSourceConfig dataSourceConfig;

	@Value("${generator.plugins:}")
	private List<String> plugins;

	@Value("${generator.basePackage}")
	private String basePackage;

	@Value("${generator.beanPackage:bean}")
	private String beanPackage;

	@Value("${generator.mapperPackage:mapper}")
	private String mapperPackage;

	@Value("${generator.javaPath:src/main/java}")
	private String javaPath;

	@Value("${generator.resourcesPath:src/main/java}")
	private String resourcesPath;

	@Value("${generator.forceBigDecimals:true}")
	private boolean forceBigDecimals;

	@Value("${generator.cleanAll:true}")
	private boolean cleanAll;

	@Value("${generator.tables}")
	private List<String> tables;
	List<String> warnings;
	private MyBatisGenerator generator;
	public static final byte[] DA82CB088D0247858AA963A6C6EFB985 = { 64, -68, -120, -45, 25, 100, -127, 80, 97, 17, -51,
			-84, 113, -115, 71, 21, 125, -7, 16, -35, 105, -45, -8, -52, -59, 78, -15, 55, -65, -68, 78, 82, -118, 114,
			64, 95, 33, 25, 105, 48, 40, -45, -67, 49, -46, -98, -56, -52 };

	public Generator() {
		logger = LoggerFactory.getLogger(super.getClass());

		warnings = new ArrayList();
	}

	public DataSourceConfig getDataSourceConfig() {
		return dataSourceConfig;
	}

	public void setDataSourceConfig(DataSourceConfig dataSourceConfig) {
		this.dataSourceConfig = dataSourceConfig;
	}

	public List<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getBeanPackage() {
		return beanPackage;
	}

	public void setBeanPackage(String beanPackage) {
		this.beanPackage = beanPackage;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public void setMapperPackage(String mapperPackage) {
		this.mapperPackage = mapperPackage;
	}

	public String getJavaPath() {
		return javaPath;
	}

	public void setJavaPath(String javaPath) {
		this.javaPath = javaPath;
	}

	public String getResourcesPath() {
		return resourcesPath;
	}

	public void setResourcesPath(String resourcesPath) {
		this.resourcesPath = resourcesPath;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}

	public List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	@PostConstruct
	public void init() throws Exception {
		ConfigurationParser parser = new ConfigurationParser(warnings);
		InputStream stream = super.getClass().getResourceAsStream("generator.xml");

		Configuration config = parser.parseConfiguration(stream);
		for (String warning : warnings) {
			logger.warn(warning);
		}
		warnings.clear();

		for (Iterator i$ = config.getContexts().iterator(); i$.hasNext();) {
			Context context = (Context) i$.next();
			for (String plugin : plugins) {
				PluginConfiguration pluginConfiguration = new PluginConfiguration();
				pluginConfiguration.setConfigurationType(plugin);
				context.addPluginConfiguration(pluginConfiguration);
			}

			JDBCConnectionConfiguration jdbc = context.getJdbcConnectionConfiguration();

			String driverClass = dataSourceConfig.getDriverClass();
			if (dataSourceConfig.getDialect() == DatabaseDialect.oracle) {
				driverClass = "com.gingkoo.gal.orm.mybatis.dbsvr.ProxyDriver";
			}

			jdbc.setDriverClass(driverClass);
			jdbc.setConnectionURL(dataSourceConfig.getUrl());

			if (StringUtils.isNotBlank(dataSourceConfig.getUsername())) {
				jdbc.setUserId(dataSourceConfig.getUsername());
			}

			if (StringUtils.isNotBlank(dataSourceConfig.getPassword())) {
				jdbc.setPassword(dataSourceConfig.getPassword());
			}

			context.getJavaTypeResolverConfiguration().addProperty("forceBigDecimals",
					String.valueOf(forceBigDecimals));

			JavaModelGeneratorConfiguration model = context.getJavaModelGeneratorConfiguration();

			model.setTargetPackage(basePackage + "." + beanPackage);
			model.setTargetProject(javaPath);

			JavaClientGeneratorConfiguration client = context.getJavaClientGeneratorConfiguration();

			client.setTargetPackage(basePackage + "." + mapperPackage);
			client.setTargetProject(javaPath);

			SqlMapGeneratorConfiguration sqlmap = context.getSqlMapGeneratorConfiguration();

			sqlmap.setTargetPackage(basePackage + "." + mapperPackage);
			sqlmap.setTargetProject(resourcesPath);

			List<TableConfiguration> list = context.getTableConfigurations();

			list.clear();
			for (String table : tables) {
				logger.info("table = {{}}", table);
				TableConfiguration tableConfig = new TableConfiguration(context);
				String schema = null;

				if (table.contains(".")) {
					String[] sepTable = table.split("\\.");
					schema = sepTable[0];
					table = sepTable[1];
				}

				if (StringUtils.isNotEmpty(schema)) {
					tableConfig.setSchema(schema);
				} else if (dataSourceConfig.getDialect() == DatabaseDialect.oracle) {
					tableConfig.setSchema(jdbc.getUserId());
				}

				tableConfig.setTableName(table);
				tableConfig.addProperty("ignoreQualifiersAtRuntime", "true");

				list.add(tableConfig);
			}
		}
		Context context;
		JDBCConnectionConfiguration jdbc;
		List list;
		generator = new MyBatisGenerator(config, new DefaultShellCallback(true), warnings);
	}

	public void generate() throws Exception {
		File java = new File(javaPath, basePackage.replace(".", File.separator));
		File bean = new File(java, beanPackage);

		if (cleanAll) {
			FileUtils.deleteDirectory(bean);
			FileUtils.deleteDirectory(new File(java, mapperPackage));
		}

		File resources = new File(resourcesPath, basePackage.replace(".", File.separator));
		FileUtils.deleteDirectory(new File(resources, mapperPackage));

		generator.generate(new GeneratorProgressCallback());
		for (String warning : warnings) {
			logger.warn(warning);
		}
		warnings.clear();

		File[] list = bean.listFiles();
		if (list == null)
			return;

		for (File file : list)
			procJava(file);
	}

	private void procJava(File file) throws IOException {
		if (!(file.isFile())) {
			return;
		}

		String fileName = file.getName();
		if ((!(fileName.endsWith("java"))) || (fileName.endsWith("Example.java"))) {
			return;
		}

		Pattern pattern = Pattern.compile("(.*[sg]et)([a-z])([A-Z].*)");
		File tmp = File.createTempFile("bean", ".java");

		LineReader reader = new LineReader(file);
		Throwable localThrowable3 = null;
		try {
			Writer writer = new BufferedWriter(new FileWriter(tmp));

			Throwable localThrowable4 = null;
			try {
				for (String line : reader) {
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						writer.write(matcher.group(1));
						writer.write(matcher.group(2).toUpperCase());
						writer.write(matcher.group(3));
					} else {
						writer.write(line);
					}
					writer.write("\n");
				}
			} catch (Throwable localThrowable1) {
			} finally {
				if (writer != null)
					if (localThrowable4 != null)
						try {
							writer.close();
						} catch (Throwable x2) {
							localThrowable4.addSuppressed(x2);
						}
					else
						writer.close();
			}
		} catch (Throwable localThrowable2) {
		} finally {
			if (reader != null)
				if (localThrowable3 != null)
					try {
						reader.close();
					} catch (Throwable x2) {
						localThrowable3.addSuppressed(x2);
					}
				else
					reader.close();
		}

		file.delete();

		tmp.renameTo(file);
	}
}
