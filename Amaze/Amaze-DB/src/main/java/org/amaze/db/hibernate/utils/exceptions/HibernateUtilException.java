package org.amaze.db.hibernate.utils.exceptions;

import org.amaze.commons.exceptions.AmazeException;

public class HibernateUtilException extends AmazeException
{

	private static final long serialVersionUID = 1L;

	public HibernateUtilException( String str )
    {
        super( str );
    }

    public HibernateUtilException( Throwable t )
    {
        super( t );
    }

    public HibernateUtilException( String str, Object... args )
    {
        super( str, args );
    }

    public HibernateUtilException( String str, Throwable t, Object... args )
    {
        super( str, t, args );
    }
}
