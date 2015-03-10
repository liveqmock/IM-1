package org.amaze.rest.framework.filters;

import java.util.Arrays;
import java.util.List;

import org.amaze.commons.converters.JsonConverter;

public class Filter
{
	private String filterName;
	private String column;
	private String columnType;
	private String condition;
	private List<String> extra;

	public Filter( String entity, String column, String columnType, String condition, String... values )
	{
		this.filterName = entity;
		this.column = column;
		this.columnType = columnType;
		this.condition = condition;
		this.extra = Arrays.asList( values );
	}

	public Filter()
	{

	}

	public String getColumn()
	{
		return column;
	}

	public void setColumn( String column )
	{
		this.column = column;
	}

	public String getFilterName()
	{
		return filterName;
	}

	public void setFilterName( String filterName )
	{
		this.filterName = filterName;
	}

	public String getColumnType()
	{
		return columnType;
	}

	public void setColumnType( String columnType )
	{
		this.columnType = columnType;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition( String condition )
	{
		this.condition = condition;
	}

	public List<String> getExtra()
	{
		return extra;
	}

	public void setExtra( List<String> extra )
	{
		this.extra = extra;
	}

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}