package org.amaze.db.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;

public class DateTimeType implements UserType
{

	public static final DateTimeType DATE_TIME_TYPE = new DateTimeType();

	private static final int[] TYPES =
	{ Types.TIMESTAMP };

	public int[] sqlTypes()
	{
		return TYPES;
	}

	public Class returnedClass()
	{
		return DateTime.class;
	}

	public boolean equals( Object x, Object y )
	{
		if ( x == y )
			return true;
		if ( x == null || y == null )
			return false;

		return x.equals( y );
	}

	public Object deepCopy( Object x )
	{
		if ( x == null )
			return null;
		return new DateTime( ( ( DateTime ) x ) );
	}

	public boolean isMutable()
	{
		return true;
	}

	@Override
	public Object assemble( Serializable arg0, Object arg1 ) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable disassemble( Object arg0 ) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode( Object arg0 ) throws HibernateException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object nullSafeGet( ResultSet arg0, String[] arg1, SessionImplementor arg2, Object arg3 ) throws HibernateException, SQLException
	{
		return null;
	}

	@Override
	public void nullSafeSet( PreparedStatement arg0, Object arg1, int arg2, SessionImplementor arg3 ) throws HibernateException, SQLException
	{

	}

	@Override
	public Object replace( Object arg0, Object arg1, Object arg2 ) throws HibernateException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Object nullSafeGet( ResultSet rs, int i )
	{
		// TODO Auto-generated method stub
		return null;
	}
}
