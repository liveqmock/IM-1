package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;

public class DefaultActionGroupModel implements ActionGroupModel
{
	private String modelName;

	private List<ActionItemModel> actionItemModels;

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
	public List<ActionItemModel> getActionItemModels()
	{
		return this.actionItemModels;
	}

	@Override
	public void setActionItemModels( List<ActionItemModel> actionItems )
	{
		this.actionItemModels = actionItems;
	}

	@Override
	public void actionItemModel( ActionItemModel itemModel )
	{
		this.actionItemModels.add( itemModel );
	}

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}