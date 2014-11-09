package org.amaze.rest.framework.columns;

public class Column
{
	private String entity;
	private String columnName;
	private String dataProperty;
	private String dataType;

	public Column( String entity, String columnName, String dataProperty, String dataType )
	{
		this.entity = entity;
		this.columnName = columnName;
		this.dataProperty = dataProperty;
		this.dataType = dataType;
	}

	public String getEntity()
	{
		return entity;
	}

	public void setEntity( String entity )
	{
		this.entity = entity;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName( String columnName )
	{
		this.columnName = columnName;
	}

	public String getDataProperty()
	{
		return dataProperty;
	}

	public void setDataProperty( String dataProperty )
	{
		this.dataProperty = dataProperty;
	}

	public String getDataType()
	{
		return dataType;
	}

	public void setDataType( String dataType )
	{
		this.dataType = dataType;
	}

}
