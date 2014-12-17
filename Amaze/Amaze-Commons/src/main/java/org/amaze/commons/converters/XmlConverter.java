package org.amaze.commons.converters;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;

public class XmlConverter
{

	/**
	 * COnvert from Java Object to XML
	 * @param object
	 * @return
	 */
	public static String fromJavaToXML( Serializable object )
	{
		XStream xs = new XStream();
		return xs.toXML( object );
	}

	/**
	* Convert from XML to Java object
	* @param xml
	* @return
	*/
	public static Object fromXMLToJava( String xml )
	{
		XStream xs = new XStream();
		return xs.fromXML( xml );
	}
	
}
