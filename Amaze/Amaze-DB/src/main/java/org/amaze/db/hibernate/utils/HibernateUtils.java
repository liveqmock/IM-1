package org.amaze.db.hibernate.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amaze.commons.objects.MultiKey;
import org.amaze.commons.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.joda.time.DateTime;

public class HibernateUtils
{
	private static final Logger logger = LogManager.getLogger( HibernateUtils.class );
	
	public static Object doSessionWork( SessionFactory sessionFactory, SessionWorkListener listener ) throws HibernateException
	{
		return doSessionWork( sessionFactory, null, listener );
	}

	public static Object doSessionWork( SessionFactory sessionFactory, Interceptor interceptor, SessionWorkListener listener ) throws HibernateException
	{
		Session session = null;
		Transaction tx = null;
		Object returnObj;
		try
		{
			if ( interceptor == null )
			{
				session = sessionFactory.openSession();
				HibernateSession.enablePartionFilter( session );
				HibernateSession.enableDeleteFilter( session );
			}
			else
			{
				session = sessionFactory.withOptions().interceptor( interceptor ).openSession();
				HibernateSession.enablePartionFilter( session );
				HibernateSession.enableDeleteFilter( session );
			}
			tx = session.beginTransaction();
			returnObj = listener.doSessionWork( session );
			tx.commit();
		}
		catch ( Exception e )
		{
			try
			{
				if ( tx != null )
					tx.rollback();
			}
			catch ( HibernateException e1 )
			{

			}
			throw new HibernateException( "Error attempting to do session work", e );
		}
		finally
		{
			try
			{
				if ( session != null )
					session.close();
			}
			catch ( HibernateException e )
			{
				logger.error( " HIbernate exception occured while doing session work, ", e );
			}
		}
		return returnObj;
	}

	public static <T> T instantiate( Class<T> clazz ) throws HibernateException
	{
		T obj;
		try
		{
			obj = clazz.newInstance();
		}
		catch ( InstantiationException e )
		{
			throw new HibernateException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new HibernateException( e );
		}
		return obj;
	}

	public static void save( SessionFactory sessionFactory, Object object ) throws HibernateException
	{
		final Object objectF = object;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				session.save( objectF );
				return null;
			}
		} );
	}

	public static void save( SessionFactory sessionFactory, Object[] objects ) throws HibernateException
	{
		final Object[] objectsF = objects;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				for ( Object object : objectsF )
					session.save( object );
				return null;
			}
		} );
	}

	public static void update( SessionFactory sessionFactory, Object object ) throws HibernateException
	{
		final Object objectF = object;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				session.update( objectF );
				return null;
			}
		} );
	}

	public static void update( SessionFactory sessionFactory, Object[] objects ) throws HibernateException
	{
		final Object[] objectsF = objects;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				for ( Object object : objectsF )
					session.update( object );
				return null;
			}
		} );
	}

	public static void delete( SessionFactory sessionFactory, Object object ) throws HibernateException
	{
		final Object objectF = object;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				session.delete( objectF );
				return null;
			}
		} );
	}

	public static void delete( SessionFactory sessionFactory, Object[] objects ) throws HibernateException
	{
		final Object[] objectsF = objects;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				for ( Object object : objectsF )
					session.delete( object );
				return null;
			}
		} );
	}

	public static void delete( SessionFactory sessionFactory, String query ) throws HibernateException
	{
		final String queryF = query;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				session.delete( queryF );
				return null;
			}
		} );
	}

	public static void delete( SessionFactory sessionFactory, String query, Object[] params ) throws HibernateException
	{
		final String queryF = query;
		final Object[] paramsF = params;
		doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Integer doSessionWork( Session session ) throws HibernateException
			{
				session.delete( queryF, paramsF );
				return null;
			}
		} );
	}

	public static <T> List<T> query( SessionFactory sessionFactory, String query ) throws HibernateException
	{
		return query( sessionFactory, query, new String[]
		{}, new Object[]
		{} );
	}

	public static <T> List<T> query( SessionFactory sessionFactory, String query, String paramName, Object paramValue ) throws HibernateException
	{
		return query( sessionFactory, query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	public static <T> List<T> query( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return query( sessionFactory, query, paramNames, paramValues, -1 );
	}

	public static <T> List<T> query( SessionFactory sessionFactory, String query, Object[] paramValues ) throws HibernateException
	{
		return query( sessionFactory, query, paramValues, -1 );
	}

	public static <T> List<T> query( SessionFactory sessionFactory, String query, Object[] paramValues, int maxResults ) throws HibernateException
	{
		final String queryF = query;
		final Object[] paramValuesF = paramValues;
		final int maxResultsF = maxResults;
		// shortcut the query if the max results is zero
		if ( maxResultsF == 0 )
			return new ArrayList<T>();

		@SuppressWarnings( "unchecked" )
		List<T> results = ( List<T> ) doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				Query q = session.createQuery( queryF );
				setQueryParameters( q, paramValuesF );
				if ( maxResultsF > 0 )
					q.setMaxResults( maxResultsF );
				List<T> results = q.list();
				session.clear();
				return results;
			}
		} );

		return results;
	}
	
	public static Integer update( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		final String queryF = query;
		final Object[] paramValuesF = paramValues;
		Integer results = ( Integer ) doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				Query q = session.createQuery( queryF );
				setQueryParameters( q, paramValuesF );
				Integer results = q.executeUpdate();
				session.clear();
				return results;
			}
		} );
		return results;
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( Session session, String query, Object[] paramValues, int maxResults ) throws HibernateException
	{
		final String queryF = query;
		final Object[] paramValuesF = paramValues;
		final int maxResultsF = maxResults;
		// shortcut the query if the max results is zero
		if ( maxResultsF == 0 )
			return new ArrayList<T>();
		Query q = session.createQuery( queryF );
		setQueryParameters( q, paramValuesF );
		if ( maxResultsF > 0 )
			q.setMaxResults( maxResultsF );
		List<T> results = ( List<T> ) q.list();
		return results;
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues, int maxResults ) throws HibernateException
	{
		final String queryF = query;
		final String[] paramNamesF = paramNames;
		final Object[] paramValuesF = paramValues;
		final int maxResultsF = maxResults;
		// shortcut the query if the max results is zero
		if ( maxResultsF == 0 )
			return new ArrayList<T>();
		List<T> results = ( List<T> ) doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				Query q = session.createQuery( queryF );
				setQueryParameters( q, paramNamesF, paramValuesF );
				if ( maxResultsF > 0 )
					q.setMaxResults( maxResultsF );
				List<T> results = q.list();
				session.clear();
				return results;
			}
		} );
		return results;
	}

	public static <T> List<T> query( Session session, String query ) throws HibernateException
	{
		return query( session, query, new String[]
		{}, new Object[]
		{} );
	}

	public static <T> List<T> query( Session session, String query, String paramName, Object paramValue ) throws HibernateException
	{
		return query( session, query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	public static <T> List<T> query( Session session, String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return query( session, query, paramNames, paramValues, -1 );
	}

	public static <T> List<T> query( Session session, String query, Object[] paramValues ) throws HibernateException
	{
		return query( session, query, paramValues, -1 );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> List<T> query( Session session, String query, String[] paramNames, Object[] paramValues, int maxResults ) throws HibernateException
	{
		final String queryF = query;
		final String[] paramNamesF = paramNames;
		final Object[] paramValuesF = paramValues;
		final int maxResultsF = maxResults;
		// shortcut the query if the max results is zero
		if ( maxResultsF == 0 )
			return new ArrayList<T>();
		Query q = session.createQuery( queryF );
		setQueryParameters( q, paramNamesF, paramValuesF );
		if ( maxResultsF > 0 )
			q.setMaxResults( maxResultsF );
		List<T> results = ( List<T> ) q.list();
		return results;
	}

	public static void setQueryParameters( Query q, Object[] paramValues ) throws HibernateException
	{
		for ( int i = 0; i < paramValues.length; i++ )
		{
			if ( paramValues[i] instanceof Boolean )
			{
				q.setParameter( i, paramValues[i] );
			}
			else if ( paramValues[i] instanceof DateTime )
			{
				q.setParameter( i, paramValues[i] );
			}
			else
			{
				q.setParameter( i, paramValues[i] );
			}
		}
	}

	@SuppressWarnings( "rawtypes" )
	public static void setQueryParameters( Query q, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		// scan each parameter and substitute the correct type for special cases
		// such as yes_no
		// for the booleans and our custom date time type
		for ( int i = 0; i < paramNames.length; i++ )
		{
			if ( paramValues[i] instanceof Boolean )
			{
				q.setParameter( paramNames[i], paramValues[i] );
			}
			else if ( paramValues[i] instanceof DateTime )
			{
				q.setParameter( paramNames[i], paramValues[i] );
			}
			else if ( paramValues[i] instanceof Collection )
			{
				q.setParameterList( paramNames[i], ( Collection ) paramValues[i] );
			}
			else if ( paramValues[i] instanceof BigDecimal )
			{
				q.setParameter( paramNames[i], paramValues[i] );
			}
			else if ( paramValues[i] instanceof String )
			{
				q.setParameter( paramNames[i], ( String ) paramValues[i] );
			}
			else
			{
				q.setParameter( paramNames[i], paramValues[i] );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T get( Session session, Class<T> clazz, Serializable id ) throws HibernateException
	{
		return ( T ) session.get( clazz, id );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T get( SessionFactory sessionFactory, Class<T> clazz, Serializable id ) throws HibernateException
	{
		final Class<T> toClazzF = clazz;
		final Serializable idF = id;
		T result = ( T ) doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				T result = ( T ) session.get( toClazzF, idF );
				session.clear();
				return result;
			}
		} );
		return result;
	}

	public static HibernateScrollableResults scroll( SessionFactory sessionFactory, String query ) throws HibernateException
	{
		return scroll( sessionFactory, query, new String[0], new Object[0], -1 );
	}

	public static HibernateScrollableResults scroll( SessionFactory sessionFactory, String query, String paramName, Object paramValue ) throws HibernateException
	{
		return scroll( sessionFactory, query, new String[]
		{ paramName }, new Object[]
		{ paramValue }, -1 );
	}

	public static HibernateScrollableResults scroll( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues ) throws HibernateException
	{
		return scroll( sessionFactory, query, paramNames, paramValues, -1 );
	}

	public static HibernateScrollableResults scroll( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues, int maxResults ) throws HibernateException
	{
		// NOTE: can't use the standard doSessionWork() wrapper since we have to
		// leave
		// the session open in order for the scrollable results to work
		Session session = null;
		Transaction tx = null;
		ScrollableResults results = null;
		try
		{
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery( query );
			setQueryParameters( q, paramNames, paramValues );
			if ( maxResults >= 0 )
			{
				q.setMaxResults( maxResults );
			}
			results = q.scroll();
			tx.commit();
		}
		catch ( HibernateException e )
		{
			try
			{
				if ( tx != null )
					tx.rollback();
			}
			catch ( HibernateException e1 )
			{
				//				log.error("Error rolling back transaction", e1);
			}
			try
			{
				if ( session != null )
					session.close();
			}
			catch ( HibernateException e1 )
			{
				//				log.error("Error closing session", e1);
			}

			throw e;
		}
		return new HibernateScrollableResults( session, results );
	}

	@SuppressWarnings( "rawtypes" )
	public static List find( SessionFactory sessionFactory, String query ) throws HibernateException
	{
		final String queryF = query;

		List results = ( List ) doSessionWork( sessionFactory, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				List results = session.createQuery( queryF ).list();
				session.clear();
				return results;
			}
		} );

		return results;
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<String, Integer> buildKeyIdMap( SessionFactory sessionFactory, String clazz, String keyProperty, String idProperty ) throws HibernateException
	{
		Map<String, Integer> keyIdMap = null;
		String hql = "select obj." + keyProperty + ", obj." + idProperty + " from " + clazz + " obj";

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		keyIdMap = new HashMap<String, Integer>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			String key = ( String ) resultRow[0];
			Integer id = ( Integer ) resultRow[1];
			keyIdMap.put( key, id );
		}

		return keyIdMap;
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<String, Integer> buildKeyIdMap( SessionFactory sessionFactory, Class clazz, String keyProperty ) throws HibernateException
	{
		return buildKeyIdMap( sessionFactory, clazz.getName(), keyProperty, "id" );
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<String, Integer> buildKeyIdMap( SessionFactory sessionFactory, Class clazz, String keyProperty, String idProperty ) throws HibernateException
	{
		return buildKeyIdMap( sessionFactory, clazz.getName(), keyProperty, idProperty );
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<Integer, String> buildIdKeyMap( SessionFactory sessionFactory, Class clazz, String keyProperty ) throws HibernateException
	{
		return buildIdKeyMap( sessionFactory, clazz.getName(), "id", keyProperty );
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<Integer, String> buildIdKeyMap( SessionFactory sessionFactory, Class clazz, String idProperty, String keyProperty ) throws HibernateException
	{
		return buildIdKeyMap( sessionFactory, clazz.getName(), idProperty, keyProperty );
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<Integer, String> buildIdKeyMap( SessionFactory sessionFactory, String clazz, String idProperty, String keyProperty ) throws HibernateException
	{
		Map<Integer, String> idKeyMap = null;
		String hql = "select obj." + idProperty + ", obj." + keyProperty + " from " + clazz + " obj";

		// Get the id-key values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the id-key map
		idKeyMap = new HashMap<Integer, String>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			Integer id = ( Integer ) resultRow[0];
			String key = ( String ) resultRow[1];
			idKeyMap.put( id, key );
		}

		return idKeyMap;
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<String, Integer> buildKeyIdMap( SessionFactory sessionFactory, Class clazz, List<String> keyProperties ) throws HibernateException
	{
		return buildKeyIdMap( sessionFactory, clazz.getName(), keyProperties, "id" );
	}

	@SuppressWarnings( "rawtypes" )
	public static Map<String, Integer> buildKeyIdMap( SessionFactory sessionFactory, String clazz, List<String> keyProperties, String idProperty ) throws HibernateException
	{
		Map<String, Integer> keyIdMap = null;
		String hql = "select obj." + StringUtils.merge( keyProperties, ", obj." ) + ", obj." + idProperty + " from " + clazz + " obj";

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		keyIdMap = new HashMap<String, Integer>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			String[] keys = new String[resultRow.length - 1];

			for ( int i = 0; i < keys.length; i++ )
			{
				keys[i] = resultRow[i].toString();
			}
			String key = StringUtils.merge( keys, "\t" );
			Integer id = ( Integer ) resultRow[resultRow.length - 1];
			keyIdMap.put( key, id );
		}

		return keyIdMap;
	}

	public static <T> Map<Object, T> buildKeyObjectMap( SessionFactory sessionFactory, Class<T> clazz, String... keyProperties ) throws HibernateException
	{
		return buildKeyObjectMap( sessionFactory, "", clazz, keyProperties );
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" } )
	public static <T> Map<Object, T> buildKeyObjectMap( SessionFactory sessionFactory, String whereClause, Class<T> clazz, String... keyProperties ) throws HibernateException
	{
		Map<Object, T> keyObjectMap = null;
		String hql = "select obj." + StringUtils.merge( keyProperties, ", obj." ) + ", obj" + " from " + clazz.getName() + " obj";

		if ( !whereClause.equals( "" ) )
			hql += whereClause;

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		keyObjectMap = new HashMap<Object, T>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			Object[] keys = new Object[resultRow.length - 1];

			for ( int i = 0; i < keys.length; i++ )
			{
				keys[i] = resultRow[i];
			}

			Object key;
			if ( keys.length > 1 )
			{
				key = new MultiKey( keys );
			}
			else
			{
				key = keys[0];
			}

			Object obj = ( Object ) resultRow[resultRow.length - 1];
			keyObjectMap.put( key, ( T ) obj );
		}

		return keyObjectMap;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" } )
	public static <T> Map<Object, T> buildKeyPropertyMap( SessionFactory sessionFactory, String whereClause, Class<T> clazz, List<String> keyProperties, String idProperty ) throws HibernateException
	{
		Map<Object, T> keyObjectMap = null;
		String hql = "select obj." + StringUtils.merge( keyProperties, ", obj." ) + ", obj." + idProperty + " from " + clazz.getName() + " obj";

		if ( !whereClause.equals( "" ) )
			hql += whereClause;

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		keyObjectMap = new HashMap<Object, T>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			Object[] keys = new Object[resultRow.length - 1];

			for ( int i = 0; i < keys.length; i++ )
			{
				keys[i] = resultRow[i];
			}

			Object key;
			if ( keys.length > 1 )
			{
				key = new MultiKey( keys );
			}
			else
			{
				key = keys[0];
			}

			Object obj = ( Object ) resultRow[resultRow.length - 1];
			keyObjectMap.put( key, ( T ) obj );
		}

		return keyObjectMap;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" } )
	public static <T> Map<Object, List<T>> buildKeyListObjectMap( SessionFactory sessionFactory, Class<T> clazz, String... keyProperties ) throws HibernateException
	{
		Map<Object, List<T>> keyObjectMap = null;
		String hql = "select obj." + StringUtils.merge( keyProperties, ", obj." ) + ", obj" + " from " + clazz.getName() + " obj";

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		keyObjectMap = new HashMap<Object, List<T>>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			Object[] keys = new Object[resultRow.length - 1];

			for ( int i = 0; i < keys.length; i++ )
			{
				keys[i] = resultRow[i];
			}

			Object key;
			if ( keys.length > 1 )
			{
				key = new MultiKey( keys );
			}
			else
			{
				key = keys[0];
			}

			Object obj = ( Object ) resultRow[resultRow.length - 1];

			List<T> list = null;
			if ( keyObjectMap.containsKey( key ) )
			{
				list = keyObjectMap.get( key );
			}
			else
			{
				list = new ArrayList<T>();
				keyObjectMap.put( key, list );
			}

			list.add( ( T ) obj );
		}

		return keyObjectMap;
	}

	public static <T> Map<Integer, T> buildIdMap( SessionFactory sessionFactory, Class<T> clazz ) throws HibernateException
	{
		return ( Map<Integer, T> ) buildIdMap( sessionFactory, clazz, null );
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" } )
	public static <T> Map<Integer, T> buildIdMap( SessionFactory sessionFactory, Class<T> clazz, String whereClause ) throws HibernateException
	{
		Map<Integer, T> idMap = null;
		String hql = "select obj.id, obj from " + clazz.getName().substring( clazz.getName().lastIndexOf( "." ) + 1 ) + " obj";

		if ( whereClause != null && whereClause.length() > 0 )
		{
			hql += " where " + whereClause;
		}

		// Get the key-id values
		List results = HibernateUtils.find( sessionFactory, hql );

		// Build the key-id map
		idMap = new HashMap<Integer, T>();
		for ( Iterator it = results.iterator(); it.hasNext(); )
		{
			Object[] resultRow = ( Object[] ) it.next();
			Integer id = ( Integer ) resultRow[0];
			Object obj = resultRow[1];
			idMap.put( id, ( T ) obj );
		}

		return idMap;
	}

	public static void updateDatabase( SessionFactory sessionFactory, List< ? extends Object> savedObjects, List< ? extends Object> updatedObjects, List< ? extends Object> deletedObjects ) throws HibernateException
	{
		updateDatabase( sessionFactory, null, savedObjects, updatedObjects, deletedObjects );
	}

	public static void updateDatabase( SessionFactory sessionFactory, Interceptor interceptor, List< ? extends Object> savedObjects, List< ? extends Object> updatedObjects, List< ? extends Object> deletedObjects ) throws HibernateException
	{
		final List< ? extends Object> savedObjectsF = savedObjects;
		final List< ? extends Object> updatedObjectsF = updatedObjects;
		final List< ? extends Object> deletedObjectsF = deletedObjects;

		doSessionWork( sessionFactory, interceptor, new SessionWorkListener()
		{
			public Object doSessionWork( Session session ) throws HibernateException
			{
				updateDatabase( session, savedObjectsF, updatedObjectsF, deletedObjectsF );
				return null;
			}
		} );
	}

	public static void updateDatabase( Session session, List< ? extends Object> savedObjects, List< ? extends Object> updatedObjects, List< ? extends Object> deletedObjects ) throws HibernateException
	{
		if ( deletedObjects != null )
		{
			for ( Object deleteObject : deletedObjects )
				session.delete( deleteObject );
		}
		// flush here so an object with a unique key can be removed and another
		// object with the same key added without causing the db to get upset
		session.flush();

		// saves/adds next
		if ( savedObjects != null )
		{
			for ( Object saveObject : savedObjects )
				session.save( saveObject );
		}

		// updates last
		if ( updatedObjects != null )
		{
			for ( Object updateObject : updatedObjects )
				session.update( updateObject );
		}
	}

	public static List<Object[]> findInResults( List<Object[]> haystack, Map<Integer, Object> searchKeys )
	{
		if ( haystack.size() == 0 )
			return new ArrayList<Object[]>();
		List<Object[]> results = new ArrayList<Object[]>();
		for ( Object[] row : haystack )
		{
			for ( Map.Entry<Integer, Object> searchKey : searchKeys.entrySet() )
			{
				if ( !row[searchKey.getKey()].equals( searchKey.getValue() ) )
				{
					break;
				}
				results.add( row );
			}
		}
		return results;
	}

	@SuppressWarnings( "rawtypes" )
	public static <T extends Object> List<T> findInResults( SessionFactory sessionFactory, List<T> haystack, Map<String, Object> searchKeys ) throws HibernateException
	{
		if ( haystack.size() == 0 )
			return new ArrayList<T>();

		Class toClazz = haystack.get( 0 ).getClass();

		// assumes all of haystack is the same type
		ClassMetadata classMetadata = sessionFactory.getClassMetadata( toClazz );

		return findInResults( classMetadata, haystack, searchKeys );
	}

	public static <T extends Object> List<T> findInResults( ClassMetadata classMetadata, List<T> haystack, Map<String, Object> searchKeys ) throws HibernateException
	{
		if ( classMetadata == null )
		{
			throw new IllegalArgumentException( "Class metadata must not be null" );
		}

		List<T> results = new ArrayList<T>();

		if ( haystack.size() == 0 )
			return results;

		// loop over all objects
		for ( T obj : haystack )
		{
			boolean match = true;
			// loop over each search key looking for mismatches
			for ( Map.Entry<String, Object> entry : searchKeys.entrySet() )
			{
				if ( classMetadata.getPropertyType( entry.getKey() ).getReturnedClass().equals( String.class ) )
				{
					if ( !( ( String ) classMetadata.getPropertyValue( obj, entry.getKey() ) ).equalsIgnoreCase( ( String ) entry.getValue() ) )
					{
						match = false;
						break;
					}
				}
				else
				{
					// property doesn't match so skip to next object
					Object propertyValue = classMetadata.getPropertyValue( obj, entry.getKey() );
					if ( propertyValue == null )
					{
						if ( entry.getValue() != null )
						{
							match = false;
							break;
						}
					}
					else
					{
						if ( !propertyValue.equals( entry.getValue() ) )
						{
							match = false;
							break;
						}
					}
				}
			}
			// if it matched add it to the results
			if ( match )
			{
				results.add( obj );
			}
		}
		return results;
	}

	@SuppressWarnings( "rawtypes" )
	public static List<Object> findObject( SessionFactory sessionFactory, Class clazz, String searchKey, Object searchKeyValue ) throws HibernateException
	{
		List<String> keys = new ArrayList<String>();
		keys.add( searchKey );
		List<Object> values = new ArrayList<Object>();
		values.add( searchKeyValue );
		return findObject( sessionFactory, clazz, keys, values );
	}

	@SuppressWarnings( "rawtypes" )
	public static List<Object> findObject( SessionFactory sessionFactory, Class clazz, List<String> searchKeys, List<Object> searchKeyValues ) throws HibernateException
	{
		String queryString = "select obj from " + clazz.getName() + " obj where";

		for ( int i = 0; i < searchKeys.size(); i++ )
		{
			if ( i != 0 )
				queryString += " and";
			queryString += " obj." + searchKeys.get( i );

			// if a search key value is null, use different syntax
			// remember to remove the value from the parameter list
			if ( searchKeyValues.get( i ) == null || ( searchKeyValues.get( i ) instanceof String && ( ( String ) searchKeyValues.get( i ) ).length() == 0 ) )
			{
				queryString += " is null";
			}
			else
			{
				queryString += " = ?";
			}
		}

		// strip out null search key values
		List<Object> tempSearchKeyValues = new ArrayList<Object>();
		for ( Object searchKeyValue : searchKeyValues )
		{
			if ( searchKeyValue != null && !( searchKeyValue instanceof String && ( ( String ) searchKeyValue ).length() == 0 ) )
			{
				tempSearchKeyValues.add( searchKeyValue );
			}
		}

		return ( List<Object> ) HibernateUtils.query( sessionFactory, queryString, tempSearchKeyValues.toArray() );
	}

	@SuppressWarnings(
	{ "unchecked" } )
	public static <T> List<T> getAllObjects( SessionFactory sessionFactory, Class<T> clazz ) throws HibernateException
	{
		return find( sessionFactory, "from " + clazz.getName() );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( SessionFactory sessionFactory, String query ) throws HibernateException
	{
		return ( T ) internalQueryExpectOneRow( sessionFactory, query, new String[]
		{}, new Object[]
		{} );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( SessionFactory sessionFactory, String query, String paramName, Object paramValue ) throws HibernateException
	{
		return ( T ) internalQueryExpectOneRow( sessionFactory, query, new String[]
		{ paramName }, new Object[]
		{ paramValue } );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T queryExpectOneRow( SessionFactory sessionFactory, String query, String paramName[], Object paramValue[] ) throws HibernateException
	{
		return ( T ) internalQueryExpectOneRow( sessionFactory, query, paramName, paramValue );
	}

	@SuppressWarnings( "rawtypes" )
	public static Object internalQueryExpectOneRow( SessionFactory sessionFactory, String query, String paramName[], Object paramValue[] ) throws HibernateException
	{
		List results = query( sessionFactory, query, paramName, paramValue );
		if ( results.size() == 0 )
		{
			throw new HibernateException( "Failed to get any rows, expected one" );
		}
		if ( results.size() > 1 )
		{
			throw new HibernateException( "Got mulitple rows, expected one" );
		}
		return results.get( 0 );
	}

	@SuppressWarnings( "rawtypes" )
	public static Object internalQueryExpectExactlyOneRow( SessionFactory sessionFactory, String query, String paramName[], Object paramValue[] ) throws HibernateException
	{
		// other internalQueryExpectOneRow could have been modified, not sure of
		// other uses.
		// so new method "internalQueryExpectExactlyOneRow"
		List results = query( sessionFactory, query, paramName, paramValue, 1 );
		if ( results.size() == 0 )
		{
			throw new HibernateException( "Failed to get any rows, expected one" );
		}
		if ( results.size() > 1 )
		{
			throw new HibernateException( "Got mulitple rows, expected one" );
		}
		return results.get( 0 );
	}

	@SuppressWarnings( "rawtypes" )
	public static ClassMetadata getClassMetadata( SessionFactory sessionFactory, Class clazz ) throws HibernateException
	{
		return sessionFactory.getClassMetadata( clazz );
	}

	@SuppressWarnings( "rawtypes" )
	public static List<String> getAllPropertyNames( SessionFactory sessionFactory, Class clazz ) throws HibernateException
	{
		ClassMetadata classMetadata = getClassMetadata( sessionFactory, clazz );
		// update all properties and the primary key
		List<String> propertyNames = new ArrayList<String>();
		propertyNames.add( classMetadata.getIdentifierPropertyName() );
		propertyNames.addAll( Arrays.asList( classMetadata.getPropertyNames() ) );
		return propertyNames;
	}

	@SuppressWarnings( "rawtypes" )
	public static List<String> getPropertyNames( SessionFactory sessionFactory, Class clazz ) throws HibernateException
	{
		ClassMetadata classMetadata = getClassMetadata( sessionFactory, clazz );
		// update all properties and the primary key
		List<String> propertyNames = new ArrayList<String>( Arrays.asList( classMetadata.getPropertyNames() ) );
		return propertyNames;
	}

	@SuppressWarnings( "rawtypes" )
	public static String getIdenfifierPropertyName( SessionFactory sessionFactory, Class clazz ) throws HibernateException
	{
		ClassMetadata classMetadata = getClassMetadata( sessionFactory, clazz );
		// get id name
		return classMetadata.getIdentifierPropertyName();
	}

	public static ScrollableResults queryScrollableResults( SessionFactory sessionFactory, String query, String[] paramNames, Object[] paramValues, int maxResults ) throws HibernateException
	{
		final String[] paramNamesF = paramNames;
		final Object[] paramValuesF = paramValues;
		HibernateScrollableResults results = null;
		Session session = null;
		try
		{
			// open a new session
			results = scroll( sessionFactory, query, paramNamesF, paramValuesF, -1 );
			return results.getScrollableResults();
		}
		catch ( Exception e )
		{
			throw new HibernateException( "Error attempting to do session work", e );
		}
		finally
		{
			// attempt to close the session
			try
			{
				if ( session != null )
					session.disconnect();
			}
			catch ( HibernateException e )
			{
				//				log.error("Error closing session", e);
			}
		}
	}

	public static <T> T createObject( Class<T> clazz ) throws HibernateException
	{
		T obj;
		try
		{
			obj = clazz.newInstance();
		}
		catch ( InstantiationException e )
		{
			throw new HibernateException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new HibernateException( e );
		}
		return obj;
	}

}
