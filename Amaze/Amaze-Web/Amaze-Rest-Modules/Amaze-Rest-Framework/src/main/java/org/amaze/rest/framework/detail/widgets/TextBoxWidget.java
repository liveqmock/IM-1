package org.amaze.rest.framework.detail.widgets;

import java.util.Map;

public class TextBoxWidget implements Widget
{

	private String widgetName;
	private String widgetType;
	private String bindField;
	private Map<String,String> options;
	
	
	@Override
	public String getWidgetName()
	{
		return this.widgetName;
	}

	@Override
	public void setWidgetName( String widgetName )
	{
		this.widgetName = widgetName;
	}
	
	@Override
	public String getWidgetType()
	{
		return widgetType;
	}
	
	@Override
	public void setWidgetType( String widgetType )
	{
		this.widgetType = widgetType;
	}

	@Override
	public void setBindField( String bindField )
	{
		this.bindField = bindField;
	}

	@Override
	public String getBindField()
	{
		return bindField;
	}

	@Override
	public Map<String, String> getWidgetOptions()
	{
		return this.options;
	}

	@Override
	public void setWidgetOptions( Map<String, String> options )
	{
		this.options = options;
	}

	@Override
	public void addWidgetOptions( String optionName, String optionValue )
	{
		if( this.options != null )
			this.options.put( optionName, optionValue );
		else
			throw new IllegalStateException( " Options Map cannot be used before initialising" );
	}

}
