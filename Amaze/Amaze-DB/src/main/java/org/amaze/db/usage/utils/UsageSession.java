package org.amaze.db.usage.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.amaze.db.usage.utils.exceptions.UsageException;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;

public class UsageSession
{

	private static Boolean systemUsageInitialised = false;

	@Autowired(required = true)
	private static EntityManagerFactory systemUsage;

	@Autowired
	private static DataSource systemUsageDataSource;

	private static Connection systemUsageConnection;

	@Autowired
	private static CassandraOperations cqlTemplate;

	public Boolean getSystemUsageInitialised()
	{
		return systemUsageInitialised;
	}

	public void setSystemUsageInitialised( Boolean systemUsageInitialised )
	{
		UsageSession.systemUsageInitialised = systemUsageInitialised;
	}

	public EntityManagerFactory getSystemUsage()
	{
		checkInitialised();
		return systemUsage;
	}

	public void setSystemUsage( EntityManagerFactory systemUsage )
	{
		UsageSession.systemUsage = systemUsage;
	}

	public DataSource getSystemUsageDataSource()
	{
		checkInitialised();
		return systemUsageDataSource;
	}

	public void setSystemUsageDataSource( DataSource systemUsageDataSource )
	{
		UsageSession.systemUsageDataSource = systemUsageDataSource;
		try
		{
			UsageSession.systemUsageConnection = systemUsageDataSource.getConnection();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public Connection getSystemUsageConnection()
	{
		checkInitialised();
		return systemUsageConnection;
	}

	public void setSystemUsageConnection( Connection systemUsageConnection )
	{
		UsageSession.systemUsageConnection = systemUsageConnection;
	}

	public CassandraOperations getCqlTemplate()
	{
		checkInitialised();
		return cqlTemplate;
	}

	public void setCqlTemplate( CassandraOperations cqlTemplate )
	{
		UsageSession.cqlTemplate = cqlTemplate;
	}

	private static void checkInitialised()
	{
		if ( !systemUsageInitialised )
			throw new IllegalStateException( "The Session Utils is not initialized" );
	}

	public static EntityManager getEntityManager()
	{
		checkInitialised();
		return systemUsage.createEntityManager();
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> find( String query ) throws UsageException
	{
		try
		{
			return ( List<T> ) getEntityManager().createQuery( query ).getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String paramName, Object paramValue ) throws UsageException
	{
		try
		{
			return ( List<T> ) getEntityManager().createQuery( query ).setParameter( paramName, paramValue ).getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String[] paramNames, Object[] paramValues ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < paramNames.length; i++ )
				q.setParameter( paramNames[i], paramValues[i] );
			return ( List<T> ) q.getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, String[] paramNames, Object[] paramValues, int maxResults ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < paramNames.length; i++ )
				q.setParameter( paramNames[i], paramValues[i] );
			q.setMaxResults( maxResults );
			return ( List<T> ) q.getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, Object[] paramValues ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < paramValues.length; i++ )
				q.setParameter( i, paramValues[i] );
			return ( List<T> ) q.getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( String query, Object[] paramValues, int maxResults ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < paramValues.length; i++ )
				q.setParameter( i, paramValues[i] );
			q.setMaxResults( maxResults );
			return ( List<T> ) q.getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}

	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( String query ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			List<T> result = q.getResultList();
			if ( result.size() == 1 )
				return ( T ) result.get( 0 );
			else if ( result.size() == 0 )
				throw new UsageException( "There are no rows retrievd from the query" );
			else
				throw new UsageException( "There are more than one rows retrievd from the query" );
		}
		catch ( UsageException e )
		{
			throw e;
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectExactlyOneRow( String query, String paramName, Object paramValue ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			List<T> result = q.getResultList();
			q.setParameter( paramName, paramValue );
			if ( result.size() == 1 )
				return ( T ) result.get( 0 );
			else if ( result.size() == 0 )
				throw new UsageException( "There are no rows retrievd from the query" );
			else
				throw new UsageException( "There are more than one rows retrievd from the query" );
		}
		catch ( UsageException e )
		{
			throw e;
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( String query, String[] paramNames, Object[] paramValues ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			List<T> result = q.getResultList();
			for ( int i = 0; i < paramValues.length; i++ )
				q.setParameter( i, paramValues[i] );
			if ( result.size() == 1 )
				return ( T ) result.get( 0 );
			else if ( result.size() == 0 )
				throw new UsageException( "There are no rows retrievd from the query" );
			else
				throw new UsageException( "There are more than one rows retrievd from the query" );
		}
		catch ( UsageException e )
		{
			throw e;
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static <T> T get( Class<T> clazz, Serializable id ) throws UsageException
	{
		try
		{
			return getEntityManager().find( clazz, id );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> getAllObjects( Class<T> clazz ) throws UsageException
	{
		try
		{
			return ( List<T> ) getEntityManager().createQuery( "from " + clazz.getName() ).getResultList();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static <T> T createObject( Class<T> clazz ) throws UsageException
	{
		T obj;
		try
		{
			obj = clazz.newInstance();
		}
		catch ( InstantiationException | IllegalAccessException e )
		{
			throw new UsageException( e );
		}
		return obj;
	}

	public static <T> T instantiate( Class<T> clazz ) throws UsageException
	{
		T obj;
		try
		{
			obj = clazz.newInstance();
		}
		catch ( InstantiationException | IllegalAccessException e )
		{
			throw new UsageException( e );
		}
		return obj;
	}

	public static void save( Object obj ) throws UsageException
	{
		try
		{
			getEntityManager().persist( obj );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void save( Object[] objArr ) throws UsageException
	{
		try
		{
			EntityManager em = getEntityManager();
			for ( Object object : objArr )
				em.persist( object );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void update( Object obj ) throws UsageException
	{
		try
		{
			getEntityManager().merge( obj );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void update( Object[] objArr ) throws UsageException
	{
		try
		{
			EntityManager em = getEntityManager();
			for ( Object object : objArr )
				em.merge( object );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static Integer update( String query, String[] paramNames, Object[] paramValues ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < paramNames.length; i++ )
				q.setParameter( paramNames[i], paramValues[i] );
			Integer results = q.executeUpdate();
			return results;
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void delete( Object obj ) throws HibernateException
	{
		try
		{
			getEntityManager().remove( obj );
		}
		catch ( Exception e )
		{

		}
	}

	public static void delete( Object[] objs ) throws HibernateException
	{
		try
		{
			EntityManager em = getEntityManager();
			for ( Object object : objs )
				em.remove( object );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void delete( String query ) throws HibernateException
	{
		try
		{
			getEntityManager().createQuery( query ).executeUpdate();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static Integer delete( String query, Object[] params ) throws UsageException
	{
		try
		{
			Query q = getEntityManager().createQuery( query );
			for ( int i = 0; i < params.length; i++ )
				q.setParameter( i, params[i] );
			return q.executeUpdate();
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

	public static void updateDatabase( List< ? extends Object> savedObjects, List< ? extends Object> updatedObjects, List< ? extends Object> deletedObjects ) throws HibernateException
	{
		try
		{
			EntityManager em = getEntityManager();
			for ( Object obj : savedObjects )
				em.persist( obj );
			for ( Object obj : updatedObjects )
				em.merge( obj );
			for ( Object obj : deletedObjects )
				em.remove( obj );
		}
		catch ( Exception e )
		{
			throw new UsageException( e );
		}
	}

}