package org.amaze.server.plugins.etl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.amaze.server.plugins.etl.Extract;

public class TestExtract implements Extract<String>
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
	public List<String> extract()
	{
		try
		{
			List<String> lines = new ArrayList<String>();
			File f = new File( params.get( "demoETLFile1" ).toString() );
			BufferedReader r = new BufferedReader( new FileReader( f ), 1000 );
			String line = null;
			while ( ( line = r.readLine() ) != null )
			{
				lines.add( line );
			}
			return lines;
		}
		catch ( Exception e )
		{

		}
		return null;
	}

}
