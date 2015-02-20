package org.amaze.server.plugins.etl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.amaze.server.plugins.etl.Load;

public class TestLoad implements Load<String>
{
	
	private Map<String, Object> params;

	@Override
	public Map<String, Object> getJobParams()
	{
		return params;
	}
	
	@Override
	public void setJobParams( Map<String, Object> params )
	{
		this.params = params;
	}

	@Override
	public Boolean load( List<String> values )
	{
		try
		{
			FileWriter w = new FileWriter( new File( params.get( "demoETLFile2" ).toString() ), true );
			for ( String stream : values )
			{
				w.write( stream );
				w.write( "\n" );
				w.flush();
			}
			w.close();
		}
		catch ( IOException e )
		{
			System.out.println( "Error " + e );
		}
		return null;
	}

}
