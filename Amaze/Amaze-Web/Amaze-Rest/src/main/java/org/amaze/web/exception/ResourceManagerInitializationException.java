package org.amaze.web.exception;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class ResourceManagerInitializationException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public ResourceManagerInitializationException()
	{
		super();
	}

	public ResourceManagerInitializationException( String s )
	{
		super( s );
	}

	public ResourceManagerInitializationException( Exception e )
	{
		super( e );
	}

	public ResourceManagerInitializationException( Throwable t )
	{
		super( t );
	}

	public ResourceManagerInitializationException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public ResourceManagerInitializationException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
