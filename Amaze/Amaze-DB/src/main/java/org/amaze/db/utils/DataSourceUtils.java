package org.amaze.db.utils;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.amaze.db.utils.exceptions.NotConfiguredException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DataSourceUtils
{

	public static String datasourceConfigFile;
	
	public static org.amaze.db.utils.DataSource systemDB;
	public static org.amaze.db.utils.DataSource systemUsageDB;
	
	public static Map<String, org.amaze.db.utils.DataSource> nameDataSourceMap;

	@PostConstruct
	public static void loadAllBeans()
	{
		// Code to load all the dataSources injecting
	}
	
	public String getDatasourceConfigFile()
	{
		return datasourceConfigFile;
	}

	public void setDatasourceConfigFile( String datasourceConfigFile )
	{
		DataSourceUtils.datasourceConfigFile = datasourceConfigFile;
	}

	public org.amaze.db.utils.DataSource getSystemDB()
	{
		if( systemDB == null)
			throw new NotConfiguredException( "System DB not configured in the DataSourceConfigurationFile" );
		return systemDB;
	}

	public void setSystemDB( org.amaze.db.utils.DataSource systemDB )
	{
		DataSourceUtils.systemDB = systemDB;
	}

	public org.amaze.db.utils.DataSource getSystemUsageDB()
	{
		if( systemUsageDB == null)
			throw new NotConfiguredException( "System Usage DB not configured in the DataSourceConfigurationFile" );
		return systemUsageDB;
	}

	public void setSystemUsageDB( org.amaze.db.utils.DataSource systemUsageDB )
	{
		DataSourceUtils.systemUsageDB = systemUsageDB;
	}

	public Map<String, org.amaze.db.utils.DataSource> getNameDataSourceMap()
	{
		return nameDataSourceMap;
	}

	public void setNameDataSourceMap( Map<String, org.amaze.db.utils.DataSource> nameDataSourceMap )
	{
		if( nameDataSourceMap == null || nameDataSourceMap.size() < 0)
			throw new NotConfiguredException( "System Usage DB not configured in the DataSourceConfigurationFile" );
		DataSourceUtils.nameDataSourceMap = nameDataSourceMap;
	}


	public static DataSource getDataSource( Properties properties )
	{
		DataSource dataSource = null;
		String dialect = properties.getProperty( "hibernate.dialect" ).toUpperCase();
		if ( dialect.contains( "MYSQL" ) )
			dataSource = createMySqlDataSource( properties );
		if ( dialect.contains( "Oracle" ) )
			dataSource = createOracleDataSource( properties );
		if ( dialect.contains( "HSQL" ) )
			dataSource = createHSQLDataSource( properties );
		return dataSource;
	}

	private static DataSource createHSQLDataSource( Properties properties )
	{
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser( properties.getProperty( "hibernate.connection.username" ) );
		dataSource.setPassword( properties.getProperty( "hibernate.connection.password" ) );
		dataSource.setUrl( properties.getProperty( "hibernate.connection.url" ) );
		return dataSource;
	}

	private static DataSource createOracleDataSource( Properties properties )
	{
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser( properties.getProperty( "user" ) );
		dataSource.setPassword( properties.getProperty( "password" ) );
		dataSource.setUrl( properties.getProperty( "Url" ) );
		return dataSource;
	}

	private static DataSource createMySqlDataSource( Properties properties )
	{
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser( properties.getProperty( "hibernate.connection.username" ) );
		dataSource.setPassword( properties.getProperty( "hibernate.connection.password" ) );
		dataSource.setUrl( properties.getProperty( "hibernate.connection.url" ) );
		return dataSource;
	}

}
