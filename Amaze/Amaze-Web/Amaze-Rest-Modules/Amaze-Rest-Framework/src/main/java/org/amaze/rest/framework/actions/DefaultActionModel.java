package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;

public class DefaultActionModel implements ActionModel
{
	private String modelName;

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
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}
