package org.amaze.db.usage.utils.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class UsageException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public UsageException()
	{
		super();
	}

	public UsageException( String s )
	{
		super( s );
	}

	public UsageException( Exception e )
	{
		super( e );
	}

	public UsageException( Throwable t )
	{
		super( t );
	}

	public UsageException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public UsageException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
