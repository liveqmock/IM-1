package org.amaze.rest.framework.models;

public interface Model 
{
	public String getModelName();
	public void setModelName( String name );
	public Integer getModelWidth();
	public void setModelWidth( Integer width );
	public Integer getModelLenght();
	public void setModelLenght( Integer lenght );
	public String getResponseJSString();
}

