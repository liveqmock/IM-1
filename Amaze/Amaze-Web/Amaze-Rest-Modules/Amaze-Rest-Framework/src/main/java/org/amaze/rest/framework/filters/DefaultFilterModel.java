package org.amaze.rest.framework.filters;

import java.util.List;

public class DefaultFilterModel implements FilterModel
{

	private String modelName;
	private Integer width;
	private Integer lenght;
	private List<Filter> filters;

	public DefaultFilterModel( String modelName, Integer width, Integer lenght, List<Filter> filters )
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

	public void getFilters( List<Filter> filters )
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
	public Integer getModelWidth()
	{
		return width;
	}

	@Override
	public void setModelWidth( Integer width )
	{
		this.width = width;

	}

	@Override
	public Integer getModelLenght()
	{
		return lenght;
	}

	@Override
	public void setModelLenght( Integer lenght )
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