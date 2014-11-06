package org.amaze.commons.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public final class AmazeString
{
	private String key;
	private String message;

	private static Map<String, String> resourceKeys = new HashMap<String, String>();

	static
	{
		ResourceBundle rm = null;
		try
		{
			rm = ResourceBundle.getBundle( "org.amaze.commons.resources.XmlResourceBundle" );
		}
		catch ( Exception e )
		{
			// ignore exception
		}

		if ( rm != null )
		{
			Enumeration<String> keys = rm.getKeys();
			while ( keys.hasMoreElements() )
			{
				String key = keys.nextElement();
				resourceKeys.put( key, rm.getString( key ) );
			}
		}
	}

	// / <summary>
	// / Construct a localizable string for the specified string value, which
	// also
	// / acts as the lookup key for the localized resource file
	// / </summary>
	// / <param name="str">The string to localize, which acts a the lookup
	// key</param>
	private AmazeString( String str )
	{
		this.key = str;

		// Default message to key string
		message = key;

		if ( key.equals( "" ) )
			return;

		// If either the resource file lookup or the key lookup fails, default
		// to using the
		// default culture version i.e. the key itself!
		try
		{
			String temp = resourceKeys.get( key );
			if ( temp != null )
				message = temp; // Assign message with the lookup if all
								// succeeds!
		}
		catch ( Exception exp )
		{
		}
	}

	public static String create( String key, Object... args )
	{
		// Get the localized string
		AmazeString str = new AmazeString( key );
		String message = str.toString();

		// If there are no args to sub in, assume the message string doesn't
		// need any
		// and our work here is done
		if ( args == null || args.length == 0 )
			return message;

		// Now parse any placeholders in the localized string with the args
		// passed in
		message = parse( message, args );
		return message;
	}

	private static String parse( String baseMessage, Object[] args )
	{
		// Loop around all the parameters and replace the '%x' placedholders
		// with the corresponding entries in the args array (from %1 to %x)
		for ( int i = 0; i < args.length; i++ )
		{
			if ( args[i] == null )
			{
				baseMessage = StringUtils.replace( baseMessage, "%" + ( i + 1 ), "NULL" );
			}
			else
			{
				baseMessage = StringUtils.replace( baseMessage, "%" + ( i + 1 ), args[i].toString() );
			}
		}
		return baseMessage;
	}

	public String toString()
	{
		return message;
	}
}
