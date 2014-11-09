package org.amaze.rest.framework.query;

public class QueryFilter
{
	private String entity;
	private String dataProperty;
	private String dataPropertyValue;
	private Boolean isSessionValuedFilter;

	public QueryFilter( String entity, String dataProperty, String dataPropertyValue, Boolean isSessionValuedFilter )
	{
		this.entity = entity;
		this.dataProperty = dataProperty;
		this.dataPropertyValue = dataPropertyValue;
		this.isSessionValuedFilter = isSessionValuedFilter;
	}

	public String getEntity()
	{
		return entity;
	}

	public void setEntity( String entity )
	{
		this.entity = entity;
	}

	public String getDataProperty()
	{
		return dataProperty;
	}

	public void setDataProperty( String dataProperty )
	{
		this.dataProperty = dataProperty;
	}

	public String getDataPropertyValue()
	{
		return dataPropertyValue;
	}

	public void setDataPropertyValue( String dataPropertyValue )
	{
		this.dataPropertyValue = dataPropertyValue;
	}

	public Boolean getIsSessionValuedFilter()
	{
		return isSessionValuedFilter;
	}

	public void setIsSessionValuedFilter( Boolean isSessionValuedFilter )
	{
		this.isSessionValuedFilter = isSessionValuedFilter;
	}

	public String getResponseJSString()
	{
		// TODO return the String response for the Query Filter
		return null;
	}
}
