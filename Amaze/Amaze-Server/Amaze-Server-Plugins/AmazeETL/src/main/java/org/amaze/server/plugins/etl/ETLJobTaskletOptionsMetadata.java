package org.amaze.server.plugins.etl;

import org.springframework.xd.module.options.spi.ModuleOption;

public class ETLJobTaskletOptionsMetadata
{

	private String extract;

	@ModuleOption( "Extract Component for the ETL Job" )
	public void setExtract( String extract )
	{
		this.extract = extract;
	}

	public String getExtract()
	{
		return this.extract;
	}

	private String transform;

	@ModuleOption( "Transform Component for the ETL Job" )
	public void setTransform( String transform )
	{
		this.transform = transform;
	}

	public String getTransform()
	{
		return this.transform;
	}

	private String load;

	@ModuleOption( "Transform Component for the ETL Job" )
	public void setLoad( String load )
	{
		this.load = load;
	}

	public String getLoad()
	{
		return this.load;
	}

}
