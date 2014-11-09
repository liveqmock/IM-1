package org.amaze.rest.framework.columns;

import java.util.List;

public class DefaultColumnModel implements ColumnModel
{
	private String modelName;
	private Integer width;
	private Integer lenght;
	
	private List<Column> columns;
	
	public DefaultColumnModel( String modelName, Integer width, Integer lenght, List<Column> columns )
	{
		this.modelName = modelName;
		this.width = width;
		this.lenght = lenght;
		this.columns = columns;
	}
	
	public Integer getWidth()
	{
		return width;
	}

	public void setWidth( Integer width )
	{
		this.width = width;
	}

	public Integer getLenght()
	{
		return lenght;
	}

	public void setLenght( Integer lenght )
	{
		this.lenght = lenght;
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
