package org.amaze.commons.exceptions;

import org.amaze.commons.utils.AmazeString;

public class AmazeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public AmazeException()
	{
		super();
	}

	public AmazeException( String s )
	{
		super( s );
	}

	public AmazeException( Exception e )
	{
		super( e );
	}

	public AmazeException( Throwable t )
	{
		super( t );
	}

	public AmazeException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public AmazeException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
