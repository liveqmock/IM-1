package org.amaze.rest.framework.columns;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;

public class DefaultColumnModel implements ColumnModel
{
	private String modelName;

	private List<Column> columns;

	public DefaultColumnModel( String modelName, List<Column> columns )
	{
		this.modelName = modelName;
		this.columns = columns;
	}
	
	public DefaultColumnModel()
	{
		
	}

	@Override
	public String getModelName()
	{
		return this.modelName;
	}

	@Override
	public void setModelName( String name )
	{
		this.modelName = name;
	}

	@Override
	public List<Column> getColumns()
	{
		return columns;
	}

	@Override
	public void setColumns( List<Column> columns )
	{
		this.columns = columns;
	}

	@Override
	public void addColumn( Column column )
	{
		this.columns.add( column );
	}
	
	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}