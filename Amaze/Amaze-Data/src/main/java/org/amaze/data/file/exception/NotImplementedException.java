package org.amaze.data.file.exception;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class NotImplementedException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public NotImplementedException()
	{
		super();
	}

	public NotImplementedException( String s )
	{
		super( s );
	}

	public NotImplementedException( Exception e )
	{
		super( e );
	}

	public NotImplementedException( Throwable t )
	{
		super( t );
	}

	public NotImplementedException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public NotImplementedException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}



}
