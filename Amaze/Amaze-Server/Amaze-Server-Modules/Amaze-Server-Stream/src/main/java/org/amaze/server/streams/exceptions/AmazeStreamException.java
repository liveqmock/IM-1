package org.amaze.server.streams.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class AmazeStreamException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public AmazeStreamException()
	{
		super();
	}

	public AmazeStreamException( String s )
	{
		super( s );
	}

	public AmazeStreamException( Exception e )
	{
		super( e );
	}

	public AmazeStreamException( Throwable t )
	{
		super( t );
	}

	public AmazeStreamException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public AmazeStreamException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
