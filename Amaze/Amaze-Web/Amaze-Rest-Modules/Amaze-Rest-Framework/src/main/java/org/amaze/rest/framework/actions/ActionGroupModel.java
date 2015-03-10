package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.rest.framework.models.Model;

public interface ActionGroupModel extends Model
{
	public List<ActionItemModel> getActionItemModels();

	public void setActionItemModels( List<ActionItemModel> actionItems );

	public void actionItemModel( ActionItemModel itemModel );
}