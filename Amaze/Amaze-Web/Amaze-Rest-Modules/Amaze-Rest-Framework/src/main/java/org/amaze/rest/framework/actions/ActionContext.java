package org.amaze.rest.framework.actions;

import java.util.List;

import org.amaze.rest.framework.screen.Context;

public class ActionContext implements Context
{
	private String screenName;
	private List<SelectedRow> selectedRows;

	public ActionContext( String screenName, List<SelectedRow> selectedRows )
	{
		this.screenName = screenName;
		this.selectedRows = selectedRows;
	}

	public List<SelectedRow> getSelectedRows()
	{
		return selectedRows;
	}

	public void setSelectedRows( List<SelectedRow> selectedRows )
	{
		this.selectedRows = selectedRows;
	}

	@Override
	public String getScreenName()
	{
		return this.screenName;
	}

	@Override
	public void setScreenName( String screenName )
	{
		this.screenName = screenName;
	}
}
