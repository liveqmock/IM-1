package org.amaze.db.utils.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class NotConfiguredException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public NotConfiguredException()
	{
		super();
	}

	public NotConfiguredException( String s )
	{
		super( s );
	}

	public NotConfiguredException( Exception e )
	{
		super( e );
	}

	public NotConfiguredException( Throwable t )
	{
		super( t );
	}

	public NotConfiguredException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public NotConfiguredException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}


}
