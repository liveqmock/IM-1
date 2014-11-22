package org.amaze.commons.xml;

import java.io.File;
import java.io.StringReader;

import org.amaze.commons.xml.exceptions.XMLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class XMLTransform
{

	private static final Logger logger = LogManager.getLogger( XMLTransform.class );

	public Document getXMLDocumentObj( String xml, boolean isxmlString )
	{
		SAXReader reader = new SAXReader();
		Document doc = null;
		try
		{
			if ( !isxmlString )
			{
				if ( this.getClass().getResourceAsStream( xml ) != null )
					doc = reader.read( this.getClass().getResourceAsStream( xml ) ); 
				else
					doc = reader.read( new File( xml ) ); 
				doc.normalize();
			}
			else
			{
				doc = reader.read( new InputSource( new StringReader( xml )) );
				doc.normalize();
			}
		}
		catch ( DocumentException e )
		{
			logger.fatal( "Error while transforming the XML file ", e );
			throw new XMLException( e );
		}
		return doc;
	}
	
	public static void main( String[] args )
	{
		new XMLTransform().getXMLDocumentObj( "/org/amaze/db/metadata/Amaze-Schema.xml", false );
	}

}
