package org.amaze.db.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class StringType implements UserType
{
	private static final int[] TYPES =
	{ Types.VARCHAR };

	public static final StringType STRING_TYPE = new StringType();

	@Override
	public int[] sqlTypes()
	{
		return TYPES;
	}

	@Override
	public Class returnedClass()
	{
		return String.class;
	}

	@Override
	public boolean equals( Object x, Object y ) throws HibernateException
	{
		if ( x == y )
			return true;
		if ( x == null || y == null )
			return false;
		return x.equals( y );
	}

	@Override
	public Object assemble( Serializable cached, Object owner ) throws HibernateException
	{
		return deepCopy( cached );
	}

	@Override
	public Object deepCopy( Object value ) throws HibernateException
	{
		return value;
	}

	@Override
	public Serializable disassemble( Object value ) throws HibernateException
	{
		return ( Serializable ) deepCopy( value );
	}

	@Override
	public int hashCode( Object arg0 ) throws HibernateException
	{
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable()
	{
		return true;
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
	public Object replace( Object original, Object target, Object arg2 ) throws HibernateException
	{
		return deepCopy( original );
	}

}
