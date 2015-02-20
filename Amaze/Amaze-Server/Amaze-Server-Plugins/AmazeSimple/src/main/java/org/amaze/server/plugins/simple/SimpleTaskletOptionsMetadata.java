package org.amaze.server.plugins.simple;

import org.springframework.xd.module.options.spi.ModuleOption;

public class SimpleTaskletOptionsMetadata
{
	
	private String className;

	@ModuleOption( "Task Class that is used for the Creation of the Amaze Simple Job" )
	public void setClassName( String className )
	{
		this.className = className;
	}

	public String getClassName()
	{
		return this.className;
	}

}
