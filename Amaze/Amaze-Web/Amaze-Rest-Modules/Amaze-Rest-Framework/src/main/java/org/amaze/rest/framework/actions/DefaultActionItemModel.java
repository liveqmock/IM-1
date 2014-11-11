package org.amaze.rest.framework.actions;

public class DefaultActionItemModel implements ActionItemModel
{
	private String modelName;
	private String width;
	private String lenght;

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
		return lenght;
	}

	@Override
	public void setModelLenght( String lenght )
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
