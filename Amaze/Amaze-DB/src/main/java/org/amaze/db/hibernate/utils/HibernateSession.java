package org.amaze.db.hibernate.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.amaze.database.hibernate.utils.exceptions.HibernateUtilException;
import org.amaze.database.utils.DataSourceUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSession
{

	private static boolean initialised = false;
	private static SessionFactory sessionFactory;
	private static Configuration configuration;
	private static DataSource dataSource;
	private static Connection connection;

	public static void main( String[] args )
	{
		HibernateSession.initialise( "hibernate.cfg.xml", true );
		//		HibernateSession.initialise("hibernate.properties", false);
	}

	public static void initialise( Configuration configuration ) throws HibernateUtilException
	{
		try
		{
			sessionFactory = HibernateUtils.buildSessionFactory( configuration );
			dataSource = DataSourceUtils.getDataSource( configuration.getProperties() );
			connection = dataSource.getConnection();
		}
		catch ( HibernateException e )
		{
			throw new HibernateUtilException( e );
		}
		catch ( SQLException e )
		{
			throw new HibernateUtilException( e );
		}
		initialised = true;
	}
	
	public static void initialiseAnnotationConfiguration( String configFile, String[] packages ) throws HibernateUtilException
	{
		try
		{
			configuration = HibernateUtils.buildAnnotationConfiguration( configFile, packages );
			sessionFactory = HibernateUtils.buildSessionFactory( configuration );
			dataSource = DataSourceUtils.getDataSource( configuration.getProperties() );
			connection = dataSource.getConnection();
		}
		catch ( HibernateException e )
		{
			throw new HibernateUtilException( e );
		}
		catch ( SQLException e )
		{
			throw new HibernateUtilException( e );
		}
		initialised = true;
	}

	public static void initialise( String configFile, boolean isXmlConfig ) throws HibernateUtilException
	{
		try
		{
			if ( isXmlConfig )
				configuration = HibernateUtils.buildConfiguration( configFile );
			else
				configuration = HibernateUtils.buildConfiguration( configFile, true );
			sessionFactory = HibernateUtils.buildSessionFactory( configuration );
			dataSource = DataSourceUtils.getDataSource( configuration.getProperties() );
			connection = dataSource.getConnection();
		}
		catch ( HibernateException e )
		{
			throw new HibernateUtilException( e );
		}
		catch ( SQLException e )
		{
			throw new HibernateUtilException( e );
		}
		initialised = true;
	}

	public static void cleanup()
	{
		if ( !initialised )
			return;
		// Close the session factory
		try
		{
			if ( getSessionFactory() != null )
				getSessionFactory().close();
		}
		catch ( HibernateException e )
		{

		}
	}

	public static Session enableDeleteFilter(Session session)
	{
		session.enableFilter( "deletedFilter" ).setParameter( "deleteFl", false );
		return session;
	}
	
	public static Session enablePartionFilter( Session session )
	{
		session.enableFilter( "partitionFilter" ).setParameter( "partitionId", false );
		return session;
	}
	
	public static Session disableDeleteFilter(Session session)
	{
		session.disableFilter( "deletedFilter" );
		return session;
	}
	
	public static Session disablePartionFilter( Session session )
	{
		session.disableFilter( "partitionFilter" );
		return session;
	}

	public static SessionFactory getSessionFactory()
	{
		checkInitialised();
		return sessionFactory;
	}

	public static Configuration getConfiguration()
	{
		checkInitialised();
		return configuration;
	}

	public static DataSource getDataSource()
	{
		checkInitialised();
		return dataSource;
	}

	public static Connection getConnection()
	{
		checkInitialised();
		return connection;
	}

	private static void checkInitialised()
	{
		if ( !initialised )
			throw new IllegalStateException();
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> find( String query ) throws HibernateException
	{
		return HibernateUtils.find( getSessionFactory(), query );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String paramName, Object paramValue ) throws HibernateException
	{
		return ( List<T> ) HibernateUtils.query( getSessionFactory(), query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return ( List<T> ) HibernateUtils.query( getSessionFactory(), query, paramNames, paramValues, -1 );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String[] paramNames, Object[] paramValues, int maxResults ) throws HibernateException
	{
		return ( List<T> ) HibernateUtils.query( getSessionFactory(), query, paramNames, paramValues, maxResults );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, Object[] paramValues ) throws HibernateException
	{
		return ( List<T> ) HibernateUtils.query( getSessionFactory(), query, paramValues, -1 );
	}

	public static <T> List<T> query( String query, Object[] paramValues, int maxResults ) throws HibernateException
	{
		return HibernateUtils.query( getSessionFactory(), query, paramValues, maxResults );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( String query ) throws HibernateException
	{
		return ( T ) HibernateUtils.internalQueryExpectOneRow( getSessionFactory(), query, new String[]
		{}, new Object[]
		{} );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( String query, String paramName, Object paramValue ) throws HibernateException
	{
		return ( T ) HibernateUtils.internalQueryExpectOneRow( getSessionFactory(), query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectExactlyOneRow( String query, String paramName, Object paramValue ) throws HibernateException
	{
		// other queryExpectOneRow could have been modified, not sure of other uses.
		return ( T ) HibernateUtils.internalQueryExpectExactlyOneRow( getSessionFactory(), query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return ( T ) HibernateUtils.internalQueryExpectOneRow( getSessionFactory(), query, paramNames, paramValues );
	}

	public static HibernateScrollableResults scroll( String query ) throws HibernateException
	{
		return HibernateUtils.scroll( getSessionFactory(), query );
	}

	public static HibernateScrollableResults scroll( String query, String paramName, Object paramValue ) throws HibernateException
	{
		return HibernateUtils.scroll( getSessionFactory(), query, paramName, paramValue );
	}

	public static HibernateScrollableResults scroll( String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return HibernateUtils.scroll( getSessionFactory(), query, paramNames, paramValues );
	}

	public static <T> T get( Class<T> clazz, Serializable id ) throws HibernateException
	{
		return HibernateUtils.get( getSessionFactory(), clazz, id );
	}

	public static <T> List<T> getAllObjects( Class<T> clazz ) throws HibernateException
	{
		return HibernateUtils.getAllObjects( getSessionFactory(), clazz );
	}

	public static <T> T createObject( Class<T> clazz ) throws HibernateException
	{
		return HibernateUtils.createObject( clazz );
	}

	public static <T> T instantiate( Class<T> clazz ) throws HibernateException
	{
		return HibernateUtils.instantiate( clazz );
	}

	public static void save( Object obj ) throws HibernateException
	{
		HibernateUtils.save( getSessionFactory(), obj );
	}

	public static void save( Object[] obj ) throws HibernateException
	{
		HibernateUtils.save( getSessionFactory(), obj );
	}

	public static void update( Object obj ) throws HibernateException
	{
		HibernateUtils.update( getSessionFactory(), obj );
	}

	public static void update( Object[] obj ) throws HibernateException
	{
		HibernateUtils.update( getSessionFactory(), obj );
	}

	public static void delete( Object obj ) throws HibernateException
	{
		HibernateUtils.delete( getSessionFactory(), obj );
	}

	public static void delete( Object[] objs ) throws HibernateException
	{
		HibernateUtils.delete( getSessionFactory(), objs );
	}

	public static void delete( String query ) throws HibernateException
	{
		HibernateUtils.delete( getSessionFactory(), query );
	}

	public static void delete( String query, Object[] params ) throws HibernateException
	{
		HibernateUtils.delete( getSessionFactory(), query, params );
	}

	public static void updateDatabase( List< ? extends Object> savedObjects, List< ? extends Object> updatedObjects, List< ? extends Object> deletedObjects ) throws HibernateException
	{
		HibernateUtils.updateDatabase( getSessionFactory(), savedObjects, updatedObjects, deletedObjects );
	}

}
