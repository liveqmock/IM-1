package org.amaze.rest.framework.query;

import org.amaze.commons.converters.JsonConverter;

public class QueryFilter
{
	private String dataProperty;
	private String dataPropertyValue;
	private Boolean isSessionValuedFilter;

	public QueryFilter( String dataProperty, String dataPropertyValue, Boolean isSessionValuedFilter )
	{
		this.dataProperty = dataProperty;
		this.dataPropertyValue = dataPropertyValue;
		this.isSessionValuedFilter = isSessionValuedFilter;
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

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}
