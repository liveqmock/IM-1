package org.amaze.web.utils;

public class SearchScreenHolder
{

	private String screenName;

	private String screenUrl;
	
	public SearchScreenHolder( String screenName, String screenUrl )
	{
		this.screenName = screenName;
		this.screenUrl = screenUrl;
	}

	public String getScreenName()
	{
		return screenName;
	}

	public void setScreenName( String screenName )
	{
		this.screenName = screenName;
	}

	public String getScreenUrl()
	{
		return screenUrl;
	}

	public void setScreenUrl( String screenUrl )
	{
		this.screenUrl = screenUrl;
	}

}
