package org.amaze.rest.framework.search;

public class SearchButton
{
	private String buttonName;
	private String relativeUrl;

	public SearchButton( String buttonName, String relativeUrl )
	{
		this.buttonName = buttonName;
		this.relativeUrl = relativeUrl;
	}

	public String getButtonName()
	{
		return buttonName;
	}

	public void setButtonName( String buttonName )
	{
		this.buttonName = buttonName;
	}

	public String getRelativeUrl()
	{
		return relativeUrl;
	}

	public void setRelativeUrl( String relativeUrl )
	{
		this.relativeUrl = relativeUrl;
	}

	public String getResponseJSString()
	{
		// TODO get the response String
		return null;
	}

}
