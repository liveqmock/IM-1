package org.amaze.server.streams.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class StreamStartException extends AmazeException
{

	private static final long serialVersionUID = 1L;

	public StreamStartException()
	{
		super();
	}

	public StreamStartException( String s )
	{
		super( s );
	}

	public StreamStartException( Exception e )
	{
		super( e );
	}

	public StreamStartException( Throwable t )
	{
		super( t );
	}

	public StreamStartException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public StreamStartException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
