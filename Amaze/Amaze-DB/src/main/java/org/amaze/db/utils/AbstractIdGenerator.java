package org.amaze.db.utils;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;

public class AbstractIdGenerator implements IdGenerator
{
	Map<Class<?>, Long> currentIdMap = new HashMap<Class<?>, Long>();
	
	@Override
	public synchronized Long generate( Class objectKey ) throws HibernateException
    {
		if( currentIdMap.containsKey( objectKey ) )
			return currentIdMap.get( objectKey );
		else
			//return (Long) HibernateSession.query( "select max() from " + objectKey.getClass().getName(), null );
			return new Long( 0 );
    }
	
}
