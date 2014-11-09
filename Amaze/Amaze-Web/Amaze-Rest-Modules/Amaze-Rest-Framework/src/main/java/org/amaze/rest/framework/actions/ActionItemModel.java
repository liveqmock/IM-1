package org.amaze.rest.framework.actions;

import org.amaze.rest.framework.models.Model;

public interface ActionItemModel extends Model
{
	public Boolean isRendered( ActionContext context );
	public void execute( ActionContext context, String url );
}
