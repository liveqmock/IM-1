package org.amaze.rest.framework.actions;

public class DefaultActionItemModel implements ActionItemModel
{
	private String modelName;
	private Integer width;
	private Integer lenght;

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
		return lenght;
	}

	@Override
	public void setModelLenght( Integer lenght )
	{
		this.lenght = lenght;
	}

	@Override
	public Boolean isRendered( ActionContext context )
	{
		//TODO Check for the validation condition
		return null;
	}

	@Override
	public void execute( ActionContext context, String url )
	{
		
	}
	
	public String getResponseJSString(){
		// TODO Return the reponse for the model
		return null;
	}

}
