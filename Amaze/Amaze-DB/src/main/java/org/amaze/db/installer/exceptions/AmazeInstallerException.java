package org.amaze.db.installer.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class AmazeInstallerException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public AmazeInstallerException()
	{
		super();
	}

	public AmazeInstallerException( String s )
	{
		super( s );
	}

	public AmazeInstallerException( Exception e )
	{
		super( e );
	}

	public AmazeInstallerException( Throwable t )
	{
		super( t );
	}

	public AmazeInstallerException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public AmazeInstallerException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
