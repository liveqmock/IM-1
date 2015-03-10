package org.amaze.rest.framework.detail.widgets;

import java.util.Map;

public interface Widget
{

	public String getWidgetName();

	public void setWidgetName( String widgetName );

	public String getBindField();

	public void setBindField( String widgetType );
	
	public String getWidgetType();
	
	public void setWidgetType( String widgetType );

	public Map<String, String> getWidgetOptions();

	public void setWidgetOptions( Map<String, String> options );
	
	public void addWidgetOptions( String optionName, String optionValue );
	
}
