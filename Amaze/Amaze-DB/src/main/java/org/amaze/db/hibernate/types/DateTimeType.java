package org.amaze.db.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

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

	@Override
	public Object replace( Object original, Object target, Object owner ) throws HibernateException
	{
		return original;
	}

	@Override
	public void nullSafeSet( PreparedStatement stmt, Object value, int index, SessionImplementor session ) throws HibernateException, SQLException
	{
		if ( value == null )
			stmt.setNull( index, Types.TIMESTAMP );
		if ( !( value instanceof DateTime ) )
			throw new UnsupportedOperationException( "Cannot convert " + value.getClass() );
		stmt.setDate( index, new java.sql.Date( ( ( DateTime ) value ).getMillis() ) );
	}

	@Override
	public Object nullSafeGet( ResultSet res, String[] names, SessionImplementor session, Object owner ) throws HibernateException, SQLException
	{
		java.sql.Date value = res.getDate( names[0] );
		if ( value == null )
			return null;
		return new DateTime( value.getTime() );
	}

	@Override
	public boolean isMutable()
	{
		return true;
	}

	@Override
	public int hashCode( Object value ) throws HibernateException
	{
		return value.hashCode();
	}

	@Override
	public boolean equals( Object x, Object y )
	{
		if ( x == y )
			return true;
		if ( x == null || y == null )
			return false;
		return x.equals( y );
	}

	@Override
	public Serializable disassemble( Object value ) throws HibernateException
	{
		if ( !( value instanceof Date ) )
			throw new UnsupportedOperationException( "Cannot convert " + value.getClass() );
		return new DateTime( ( Date ) value );
	}

	@Override
	public Object deepCopy( Object value )
	{
		if ( value == null )
			return null;
		if ( !( value instanceof DateTime ) )
			throw new UnsupportedOperationException( "Cannot convert " + value.getClass() );
		return new DateTime( value );
	}

	@Override
	public Object assemble( Serializable cached, Object owner ) throws HibernateException
	{
		return cached;
	}

	public static Object nullSafeGet( ResultSet rs, int i ) throws SQLException
	{
		java.sql.Date value = rs.getDate( i );
		if ( value == null )
			return null;
		return new DateTime( value.getTime() );
	}

}