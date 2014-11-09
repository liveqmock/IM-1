package org.amaze.rest.framework.actions;

import java.util.Map;

public class SelectedRow
{
	private Integer rowNum;
	private Map<String, String> rowProperties;

	public SelectedRow( Integer rowNum, Map<String, String> rowProperties )
	{
		this.rowNum = rowNum;
		this.rowProperties = rowProperties;
	}

	public String getRowProperty( String property )
	{
		return rowProperties.get( property );
	}

	public Integer getRowNum()
	{
		return rowNum;
	}

	public void setRowNum( Integer rowNum )
	{
		this.rowNum = rowNum;
	}

	public Map<String, String> getRowProperties()
	{
		return rowProperties;
	}

	public void setRowProperties( Map<String, String> rowProperties )
	{
		this.rowProperties = rowProperties;
	}

}
