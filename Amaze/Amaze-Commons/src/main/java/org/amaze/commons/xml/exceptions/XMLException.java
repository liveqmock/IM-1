package org.amaze.commons.xml.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class XMLException extends AmazeException
{

	private static final long serialVersionUID = 1L;

	public XMLException()
	{
		super();
	}

	public XMLException( String s )
	{
		super( s );
	}

	public XMLException( Exception e )
	{
		super( e );
	}

	public XMLException( Throwable t )
	{
		super( t );
	}

	public XMLException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public XMLException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
