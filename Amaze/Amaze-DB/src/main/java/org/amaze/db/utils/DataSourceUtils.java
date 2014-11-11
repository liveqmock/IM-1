package org.amaze.db.utils;

import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DataSourceUtils {

	public static DataSource getDataSource(Properties properties) {
		DataSource dataSource = null;
		String dialect = properties.getProperty("hibernate.dialect").toUpperCase();
		if(dialect.contains("MYSQL"))
			dataSource = createMySqlDataSource(properties);
		if(dialect.contains("Oracle"))
			dataSource = createOracleDataSource(properties);
		if(dialect.contains("HSQL"))
			dataSource = createHSQLDataSource(properties);
		return dataSource;
	}

	private static DataSource createHSQLDataSource(Properties properties) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(properties.getProperty("hibernate.connection.username"));
		dataSource.setPassword(properties.getProperty("hibernate.connection.password"));
		dataSource.setUrl(properties.getProperty("hibernate.connection.url"));
		return dataSource;
	}

	private static DataSource createOracleDataSource(Properties properties) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(properties.getProperty("user"));
		dataSource.setPassword(properties.getProperty("password"));
		dataSource.setUrl(properties.getProperty("Url"));
		return dataSource;
	}

	private static DataSource createMySqlDataSource(Properties properties) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(properties.getProperty("hibernate.connection.username"));
		dataSource.setPassword(properties.getProperty("hibernate.connection.password"));
		dataSource.setUrl(properties.getProperty("hibernate.connection.url"));
		return dataSource;
	}

}
