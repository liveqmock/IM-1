package org.amaze.rest.framework.filters;

import java.util.Arrays;
import java.util.List;

public class Filter
{
	private String entity;
	private String column;
	private String columnType;
	private String condition;
	private List<String> extra;

	public Filter( String entity, String column, String columnType, String condition, String... values )
	{
		this.entity = entity;
		this.column = column;
		this.columnType = columnType;
		this.condition = condition;
		this.extra = Arrays.asList( values );
	}

	public String getColumn()
	{
		return column;
	}

	public void setColumn( String column )
	{
		this.column = column;
	}

	public String getEntity()
	{
		return entity;
	}

	public void setEntity( String entity )
	{
		this.entity = entity;
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

	public String getResponseJSString()
	{
		// TODO return the response 
		return null;
	}

}