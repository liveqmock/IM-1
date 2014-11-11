package org.amaze.rest.framework.actions;

import java.util.List;

public class DefaultActionModel implements ActionModel
{
	private String modelName;
	private String width;
	private String lenght;

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