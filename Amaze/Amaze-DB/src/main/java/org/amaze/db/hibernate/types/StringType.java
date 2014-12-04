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
	private static final int[] TYPES = { Types.VARCHAR };

	public static final StringType STRING_TYPE = new StringType();

	@Override
	public boolean isMutable()
	{
		return false;
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
	public int hashCode( Object x ) throws HibernateException
	{
		return x.hashCode();
	}
	
	@Override
	public Object deepCopy( Object value ) throws HibernateException
	{
		return value;
	}
	
	@Override
	public Object replace( Object original, Object target, Object arg2 ) throws HibernateException
	{
		return deepCopy( original );
	}
	
	@Override
	public Serializable disassemble( Object value ) throws HibernateException
	{
		return ( Serializable ) deepCopy( value );
	}
	
	@Override
	public Object assemble( Serializable cached, Object owner ) throws HibernateException
	{
		return deepCopy( cached );
	}
	
	@Override
	public int[] sqlTypes()
	{
		return TYPES;
	}
	
	@Override
	public Object nullSafeGet( ResultSet res, String[] names, SessionImplementor session, Object owner ) throws HibernateException, SQLException
	{
		String value = res.getString( names[0] );
		if ( value == null )
			return null;
		return value;
	}
	
	@Override
	public void nullSafeSet( PreparedStatement stmt, Object value, int index, SessionImplementor session ) throws HibernateException, SQLException
	{
		if ( value == null )
			stmt.setNull( index, Types.VARCHAR );
		if ( !( value instanceof String ) )
			throw new UnsupportedOperationException( "Cannot convert " + value.getClass() );
		stmt.setString( index, (String) value );
	}

	@Override
	public Class returnedClass()
	{
		return String.class;
	}

}
