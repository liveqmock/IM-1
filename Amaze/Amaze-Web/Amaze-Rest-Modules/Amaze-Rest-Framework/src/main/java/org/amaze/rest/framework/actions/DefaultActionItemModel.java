package org.amaze.rest.framework.actions;

import org.amaze.commons.converters.JsonConverter;

public class DefaultActionItemModel implements ActionItemModel
{
	private String modelName;
	private String relativeUrl;
	private String actionClass;
	private String detail;
	
	@Override
	public String getDetail()
	{
		return detail;
	}

	@Override
	public void setDetail( String detail )
	{
		this.detail = detail;
	}

	@Override
	public String getActionClass()
	{
		return actionClass;
	}

	@Override
	public void setActionClass( String actionClass )
	{
		this.actionClass = actionClass;
	}

	@Override
	public String getRelativeUrl()
	{
		return relativeUrl;
	}

	@Override
	public void setRelativeUrl( String relativeUrl )
	{
		this.relativeUrl = relativeUrl;
	}

	@Override
	public String getModelName()
	{
		return modelName;
	}

	@Override
	public void setModelName( String name )
	{
		this.modelName = name;
	}

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}
