package org.amaze.server.job;

import java.io.File;
import java.util.Map;

import org.amaze.commons.xml.XMLTransform;

public class JobBuilder
{
	
	XMLTransform transform = new XMLTransform();
	
	public Map<String, File> buildJob( org.amaze.db.hibernate.objects.Job job )
	{
		transform.getXMLDocumentObj( "jobtemplate.xml", false );
		return null;
	}
	
	
	public static void main( String[] args )
	{
		
	}
	
}
