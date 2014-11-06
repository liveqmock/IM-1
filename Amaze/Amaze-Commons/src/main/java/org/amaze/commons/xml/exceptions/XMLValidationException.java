package org.amaze.commons.xml.exceptions;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.AmazeString;

public class XMLValidationException extends AmazeException
{

	private static final long serialVersionUID = 1L;

	public XMLValidationException()
	{
		super();
	}

	public XMLValidationException( String s )
	{
		super( s );
	}

	public XMLValidationException( Exception e )
	{
		super( e );
	}

	public XMLValidationException( Throwable t )
	{
		super( t );
	}

	public XMLValidationException( String str, Object... args )
	{
		super( AmazeString.create( str, args ) );
	}

	public XMLValidationException( String str, Throwable t, Object... args )
	{
		super( AmazeString.create( str, args ), t );
	}

}
