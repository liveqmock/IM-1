package org.amaze.server.plugins.etl.test;

import groovy.lang.GroovyShell;

import java.util.Map;

import org.amaze.server.plugins.etl.Transform;

public class TestTransform implements Transform<String, String>
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
	public String transform( String value )
	{
//		GroovyShell shell = new GroovyShell();
//		Object result = shell.evaluate( params.get( "transformScript" ).toString() );
		return "Transforming the line " + value.toUpperCase() + "\n";
	}

}
