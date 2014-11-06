package org.amaze.db.hibernate.types;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class DecimalType implements UserType
{
	public static final DecimalType DECIMAL_TYPE = new DecimalType();

	private static final int[] TYPES =
	{ Types.BIGINT };

	public int[] sqlTypes()
	{
		return TYPES;
	}

	public Class returnedClass()
	{
		return BigDecimal.class;
	}

	public boolean equals( Object x, Object y ) throws HibernateException
	{
		if ( x == y )
			return true;
		if ( x == null || y == null )
			return false;
		return x.equals( y );
	}

	public void nullSafeSet( PreparedStatement st, Object value, int index ) throws HibernateException, SQLException
	{
		deepCopy( value );
	}

	@Override
	public Object deepCopy( Object value ) throws HibernateException
	{
		if ( value == null )
			return null;
		BigDecimal bigDecimal = ( BigDecimal ) value;
		return new BigDecimal( bigDecimal.unscaledValue(), bigDecimal.scale() );
	}

	@Override
	public boolean isMutable()
	{
		return true;
	}

	@Override
	public Object assemble( Serializable value, Object arg1 ) throws HibernateException
	{
		return deepCopy( value );
	}

	@Override
	public Serializable disassemble( Object value ) throws HibernateException
	{
		return ( Serializable ) deepCopy( value );
	}

	@Override
	public int hashCode( Object value ) throws HibernateException
	{
		return value.hashCode();
	}

	@Override
	public Object nullSafeGet( ResultSet arg0, String[] arg1, SessionImplementor arg2, Object value ) throws HibernateException, SQLException
	{
		return deepCopy( value );
	}

	@Override
	public void nullSafeSet( PreparedStatement arg0, Object value, int arg2, SessionImplementor arg3 ) throws HibernateException, SQLException
	{
		deepCopy( value );	}

	@Override
	public Object replace( Object original, Object target, Object arg2 ) throws HibernateException
	{
		return original;
	}

	public static Object nullSafeGet( ResultSet rs, int i )
	{
		// TODO Auto-generated method stub
		return null;
	}
}
