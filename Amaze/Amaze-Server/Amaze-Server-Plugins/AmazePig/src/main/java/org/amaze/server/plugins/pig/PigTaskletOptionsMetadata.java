package org.amaze.server.plugins.pig;

import org.springframework.xd.module.options.spi.ModuleOption;

public class PigTaskletOptionsMetadata
{

	private String script;
	
	public String getScript()
	{
		return script;
	}

	@ModuleOption( "Hadoop Pig Script location" )
	public void setScript( String script )
	{
		this.script = script;
	}
	
}
