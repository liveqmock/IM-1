package org.amaze.db.schema.exceptions;

import org.amaze.commons.exceptions.AmazeException;

public class SchemaParseException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public SchemaParseException( String str )
    {
        super( str );
    }

    public SchemaParseException( Throwable t )
    {
        super( t );
    }

    public SchemaParseException( String str, Object... args )
    {
        super( str, args );
    }

    public SchemaParseException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
