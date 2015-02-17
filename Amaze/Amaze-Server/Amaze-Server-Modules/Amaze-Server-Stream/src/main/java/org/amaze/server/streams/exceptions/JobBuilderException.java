package org.amaze.server.streams.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class JobBuilderException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public JobBuilderException()
	{
		super();
	}

	public JobBuilderException( String s )
	{
		super( s );
	}

	public JobBuilderException( Exception e )
	{
		super( e );
	}

	public JobBuilderException( Throwable t )
	{
		super( t );
	}

	public JobBuilderException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public JobBuilderException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
