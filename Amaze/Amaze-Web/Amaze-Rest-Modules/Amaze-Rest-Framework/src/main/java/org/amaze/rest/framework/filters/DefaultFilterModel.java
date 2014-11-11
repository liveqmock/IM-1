package org.amaze.rest.framework.filters;

import java.util.List;

public class DefaultFilterModel implements FilterModel
{

	private String modelName;
	private String width;
	private String lenght;
	private List<Filter> filters;

	public DefaultFilterModel( String modelName, String width, String lenght, List<Filter> filters )
	{
		this.modelName = modelName;
		this.width = width;
		this.lenght = lenght;
		this.filters = filters;
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
	public String getModelWidth()
	{
		return width;
	}

	@Override
	public void setModelWidth( String width )
	{
		this.width = width;

	}

	@Override
	public String getModelLenght()
	{
		return lenght;
	}

	@Override
	public void setModelLenght( String lenght )
	{
		this.lenght = lenght;
	}

	@Override
	public String getResponseJSString()
	{
		// TODO Get the string representation of the Json String that represents the overall Filter panel
		return null;
	}

	public String getOutputQueryStringForEntityFiltering()
	{
		// TODO Get the string representation for filtering the query that queries the entities that are to be rendered in the search
		return null;
	}

}