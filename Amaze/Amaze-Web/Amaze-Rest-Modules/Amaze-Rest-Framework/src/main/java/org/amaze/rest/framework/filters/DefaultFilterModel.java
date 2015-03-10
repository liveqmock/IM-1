package org.amaze.rest.framework.filters;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;

public class DefaultFilterModel implements FilterModel
{

	private String modelName;
	private List<Filter> filters;

	public DefaultFilterModel( String modelName, List<Filter> filters )
	{
		this.modelName = modelName;
		this.filters = filters;
	}
	
	public DefaultFilterModel()
	{
		
	}

	public List<Filter> getFilters()
	{
		return filters;
	}

	public void setFilters( List<Filter> filters )
	{
		this.filters = filters;
	}

	public void addFilter( Filter filter )
	{
		this.filters.add( filter );
	}

	@Override
	public String getModelName()
	{
		return modelName;
	}

	@Override
	public void setModelName( String name )
	{
		this.modelName = name;

	}

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}