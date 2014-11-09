package org.amaze.rest.framework.actions;

import java.util.List;

public class DefaultActionGroupModel implements ActionGroupModel
{
	private String modelName;
	private Integer width;
	private Integer lenght;
	
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
	public Integer getModelWidth()
	{
		return this.width;
	}

	@Override
	public void setModelWidth( Integer width )
	{
		this.width = width;
	}

	@Override
	public Integer getModelLenght()
	{
		return this.lenght;
	}

	@Override
	public void setModelLenght( Integer lenght )
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
