package org.amaze.db.schema.exceptions;

import org.amaze.commons.exceptions.AmazeException;

public class SchemaLoadException extends AmazeException
{
	private static final long serialVersionUID = 1L;

	public SchemaLoadException( String str )
    {
        super( str );
    }

    public SchemaLoadException( Throwable t )
    {
        super( t );
    }

    public SchemaLoadException( String str, Object... args )
    {
        super( str, args );
    }

    public SchemaLoadException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
