package org.amaze.rest.framework.actions;

import java.util.List;

public class DefaultActionModel implements ActionModel
{
	private String modelName;
	private Integer width;
	private Integer lenght;

	private List<ActionGroupModel> actionGroupModels;

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
	public List<ActionGroupModel> getActionGroupModels()
	{
		return this.actionGroupModels;
	}

	@Override
	public void setActionGroupModels( List<ActionGroupModel> actionGroups )
	{
		this.actionGroupModels = actionGroups;
	}

	@Override
	public void actionGroupModel( ActionGroupModel groupModel )
	{
		this.actionGroupModels.add( groupModel );
	}

	@Override
	public String getResponseJSString()
	{
		// TODO generate the String for the response
		return null;
	}
	
}
