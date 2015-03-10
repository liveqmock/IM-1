package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.rest.framework.models.Model;

public interface ActionModel extends Model
{
	public List<ActionGroupModel> getActionGroupModels();

	public void setActionGroupModels( List<ActionGroupModel> actionGroups );

	public void actionGroupModel( ActionGroupModel groupModel );
}
