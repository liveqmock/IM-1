package org.amaze.rest.framework.columns;

import java.util.List;

public class DefaultColumnModel implements ColumnModel
{
	private String modelName;
	private String width;
	private String lenght;
	
	private List<Column> columns;
	
	public DefaultColumnModel( String modelName, String width, String lenght, List<Column> columns )
	{
		this.modelName = modelName;
		this.width = width;
		this.lenght = lenght;
		this.columns = columns;
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
		// TODO Auto-generated method stub
		return null;
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

}