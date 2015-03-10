package org.amaze.rest.framework.search;

import org.amaze.commons.converters.JsonConverter;

public class SearchButton
{
	private String buttonName;
	private String style;
	private String relativeUrl;

	public SearchButton( String buttonName, String relativeUrl, String style )
	{
		this.style = style;
		this.buttonName = buttonName;
		this.relativeUrl = relativeUrl;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle( String style )
	{
		this.style = style;
	}

	public SearchButton()
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

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}
