package org.amaze.rest.framework.actions;

import org.amaze.rest.framework.models.Model;

public interface ActionItemModel extends Model
{

	String getActionClass();

	void setActionClass( String actionClass );

	String getRelativeUrl();

	void setRelativeUrl( String relativeUrl );
	
	public String getDetail();
	
	public void setDetail( String detail );
	
}
