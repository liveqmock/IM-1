package org.amaze.commons.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.amaze.commons.xml.exceptions.XMLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLTransform
{

	private static final Logger logger = LogManager.getLogger( XMLTransform.class );

	public String getStringXmlFromDoc( Document doc )
	{
		try
		{
			doc.normalizeDocument();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform( new DOMSource( doc ), new StreamResult( writer ) );
			return writer.getBuffer().toString().replaceAll( "\n|\r", "" );
		}
		catch ( TransformerException e )
		{
			logger.fatal( "Error while transforming the XML file ", e );
			throw new XMLException( e );
		}
	}

	public Document getXMLDocumentObj( String xml, boolean isxmlString )
	{
		Document doc = null;
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if ( !isxmlString )
			{
				if ( this.getClass().getResourceAsStream( xml ) != null )
					doc = builder.parse( this.getClass().getResourceAsStream( xml ) );
				else
					doc = builder.parse( new File( xml ) );
				doc.normalize();
			}
			else
			{
				doc = builder.parse( new InputSource( new StringReader( xml ) ) );
				doc.normalize();
			}
		}
		catch ( ParserConfigurationException | SAXException | IOException e )
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
