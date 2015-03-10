package org.amaze.commons.converters;

import org.amaze.commons.exceptions.AmazeException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonConverter
{

	/**
	 * Convert Java object to JSON String
	 * 
	 * @param object
	 * @return
	 */
	public static String fromJavaToJson( Object object )
	{
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper();
			return jsonMapper.writeValueAsString( object );
		}
		catch ( Exception e )
		{
			throw new AmazeException( e );
		}
	}

	/**
	 * Convert a JSON string to an java object
	 * 
	 * @param json
	 * @return
	 * 
	 */
	public static Object fromJsonToJava( String json, Class< ? > type )
	{
		try
		{
			ObjectMapper jsonMapper = new ObjectMapper();
			return jsonMapper.readValue( json, type );
		}
		catch ( Exception e )
		{
			throw new AmazeException( e );
		}
	}

}