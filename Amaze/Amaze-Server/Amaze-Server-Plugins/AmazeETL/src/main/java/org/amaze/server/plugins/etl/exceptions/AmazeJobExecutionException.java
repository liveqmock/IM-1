package org.amaze.server.plugins.etl.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class AmazeJobExecutionException extends AmazeException
{
	
	private static final long serialVersionUID = 1L;

	public AmazeJobExecutionException()
	{
		super();
	}

	public AmazeJobExecutionException( String s )
	{
		super( s );
	}

	public AmazeJobExecutionException( Exception e )
	{
		super( e );
	}

	public AmazeJobExecutionException( Throwable t )
	{
		super( t );
	}

	public AmazeJobExecutionException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public AmazeJobExecutionException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}


}
