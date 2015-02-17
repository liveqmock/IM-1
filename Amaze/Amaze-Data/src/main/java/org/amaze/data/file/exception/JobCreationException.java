package org.amaze.data.file.exception;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class JobCreationException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public JobCreationException()
	{
		super();
	}

	public JobCreationException( String s )
	{
		super( s );
	}

	public JobCreationException( Exception e )
	{
		super( e );
	}

	public JobCreationException( Throwable t )
	{
		super( t );
	}

	public JobCreationException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public JobCreationException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}

