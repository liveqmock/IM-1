package org.amaze.db.utils.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class DataSourceException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public DataSourceException()
	{
		super();
	}

	public DataSourceException( String s )
	{
		super( s );
	}

	public DataSourceException( Exception e )
	{
		super( e );
	}

	public DataSourceException( Throwable t )
	{
		super( t );
	}

	public DataSourceException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public DataSourceException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
