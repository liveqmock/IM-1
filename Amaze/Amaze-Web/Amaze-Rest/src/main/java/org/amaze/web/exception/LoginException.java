package org.amaze.web.exception;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class LoginException extends AmazeException
{

	private static final long serialVersionUID = 1L;

	public LoginException()
	{
		super();
	}

	public LoginException( String s )
	{
		super( s );
	}

	public LoginException( Exception e )
	{
		super( e );
	}

	public LoginException( Throwable t )
	{
		super( t );
	}

	public LoginException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public LoginException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
