package org.amaze.server.plugins.mr;

import org.springframework.xd.module.options.spi.ModuleOption;

public class MRTaskletOptionsMetadata
{
	
	private String mapper;
	
	private String reducer;
	
	private String combiner = "";
	
	public String getMapper()
	{
		return mapper;
	}

	@ModuleOption( "Map class for the Hadoop MR job" )
	public void setMapper( String mapper )
	{
		this.mapper = mapper;
	}

	public String getReducer()
	{
		return reducer;
	}

	@ModuleOption( "Reducer class for the Hadoop MR job" )
	public void setReducer( String reducer )
	{
		this.reducer = reducer;
	}

	public String getCombiner()
	{
		return combiner;
	}

	@ModuleOption( "Combiner class for the Hadoop MR job" )
	
	public void setCombiner( String combiner )
	{
		this.combiner = combiner;
	}

}
