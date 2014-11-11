package org.amaze.rest.framework.actions;

import java.util.List;

public class DefaultActionGroupModel implements ActionGroupModel
{
	private String modelName;
	private String width;
	private String lenght;
	
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
	public String getModelWidth()
	{
		return this.width;
	}

	@Override
	public void setModelWidth( String width )
	{
		this.width = width;
	}

	@Override
	public String getModelLenght()
	{
		return this.lenght;
	}

	@Override
	public void setModelLenght( String lenght )
	{
		this.lenght = lenght;
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
	
	public String getResponseJSString(){
		// TODO Return the response of the Model
		return null;
	}

}