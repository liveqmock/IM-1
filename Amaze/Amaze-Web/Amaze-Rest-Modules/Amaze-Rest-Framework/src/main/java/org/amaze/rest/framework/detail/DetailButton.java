package org.amaze.rest.framework.detail;

public class DetailButton
{

	private String buttonName;
	private String relativeUrl;
	
	public DetailButton( String buttonName, String relativeUrl )
	{
		this.relativeUrl = relativeUrl;
		this.buttonName = buttonName;
	}
	
	public DetailButton()
	{
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

}
