package org.amaze.db.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.utils.exceptions.NotConfiguredException;
import org.amaze.db.utils.exceptions.NotImplementedException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DataSourceUtils
{

	public static org.amaze.db.utils.DataSource systemDB;
	public static org.amaze.db.utils.DataSource systemUsageDB;

	public static Map<String, org.amaze.db.utils.DataSource> nameDataSourceMap = new HashMap<String, org.amaze.db.utils.DataSource>();

	@PostConstruct
	public static void loadAllBeans()
	{
		try
		{
			systemDB = createSystemDataSource( HibernateSession.getDataSource().getConnection().getMetaData().getURL() );
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
	}

	private static org.amaze.db.utils.DataSource createSystemDataSource( String dataBaseType )
	{
		org.amaze.db.utils.DataSource dataSource = null;
		if ( dataBaseType.contains( "oracle" ) )
			dataSource = new OracleDataSource( "amaze", "system", HibernateSession.getSessionFactory(), HibernateSession.getDataSource(), HibernateSession.getConnection() );
		else if ( dataBaseType.contains( "mysql" ) )
			dataSource = new MySQLDataSource( "amaze", "system", HibernateSession.getSessionFactory(), HibernateSession.getDataSource(), HibernateSession.getConnection() );
		else
			throw new NotImplementedException( " Not supported database " + dataBaseType + " for the Amaze platform... " );
		dataSource.attachSchema( "/org/amaze/db/metadata/Amaze-Schema.xml" );
		return dataSource;
	}

	public static org.amaze.db.utils.DataSource getSystemDB()
	{
		if ( systemDB == null )
			throw new NotConfiguredException( "System DB not configured in the DataSourceConfigurationFile" );
		return systemDB;
	}

	public void setSystemDB( org.amaze.db.utils.DataSource systemDB )
	{
		DataSourceUtils.systemDB = systemDB;
	}

	public org.amaze.db.utils.DataSource getSystemUsageDB()
	{
		if ( systemUsageDB == null )
			throw new NotConfiguredException( "System Usage DB not configured in the DataSourceConfigurationFile" );
		return systemUsageDB;
	}

	public void setSystemUsageDB( org.amaze.db.utils.DataSource systemUsageDB )
	{
		DataSourceUtils.systemUsageDB = systemUsageDB;
	}

	public Map<String, org.amaze.db.utils.DataSource> getNameDataSourceMap()
	{
		if ( nameDataSourceMap.size() < 0 )
			throw new NotConfiguredException( "System Usage DB not configured in the DataSourceConfigurationFile" );
		return nameDataSourceMap;
	}

	public void setNameDataSourceMap( Map<String, org.amaze.db.utils.DataSource> nameDataSourceMap )
	{
		DataSourceUtils.nameDataSourceMap = nameDataSourceMap;
	}

	public void addDataSource( String dataSourceName, org.amaze.db.utils.DataSource dataSource )
	{
		if ( dataSource != null && dataSource != null )
			nameDataSourceMap.put( dataSourceName, dataSource );
		else
			throw new NotConfiguredException( "Null cannot be considered as a DataSource " );
	}

	public org.amaze.db.utils.DataSource getDataSource( String dataSourceName )
	{
		if ( nameDataSourceMap.containsKey( dataSourceName ) )
			return nameDataSourceMap.get( dataSourceName );
		else
			throw new NotConfiguredException( "No Such DataSource configured with name " + dataSourceName );
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
