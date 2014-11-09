package org.amaze.rest.framework.search;

import java.util.List;

import org.amaze.rest.framework.actions.ActionModel;
import org.amaze.rest.framework.columns.ColumnModel;
import org.amaze.rest.framework.filters.FilterModel;
import org.amaze.rest.framework.query.QueryFilter;

public class DefaultSearch implements Search
{
	private String screenName;
	private String entity;

	private List<SearchButton> searchButtons;
	private FilterModel filterModel;
	private ActionModel actionModel;
	private ColumnModel columnModel;
	private String basicDataQuery;
	private List<QueryFilter> basicDataQueryFilters;
	private List<QueryFilter> sessionDataQueryFilters;
	private Boolean isPaggedResult;

	@Override
	public String getScreenName()
	{
		return screenName;
	}

	public String getEntity()
	{
		return entity;
	}

	public List<SearchButton> getSearchButtons()
	{
		return searchButtons;
	}

	public FilterModel getFilterModel()
	{
		return filterModel;
	}

	public ActionModel getActionModel()
	{
		return actionModel;
	}

	public ColumnModel getColumnModel()
	{
		return columnModel;
	}

	public String getBasicDataQuery()
	{
		return basicDataQuery;
	}

	public List<QueryFilter> getBasicDataQueryFilters()
	{
		return basicDataQueryFilters;
	}

	public List<QueryFilter> getSessionDataQueryFilters()
	{
		return sessionDataQueryFilters;
	}

	public Boolean getIsPaggedResult()
	{
		return isPaggedResult;
	}

	@Override
	public void setScreenName( String screenName )
	{
		this.screenName = screenName;
	}

	@Override
	public void setSearchEntity( String entity )
	{
		this.entity = entity;
	}

	@Override
	public String getSearchEntity()
	{
		return entity;
	}

	@Override
	public List<SearchButton> getScreenButtons()
	{
		return searchButtons;
	}

	@Override
	public void setScreenButtons( List<SearchButton> searchButtons )
	{
		this.searchButtons = searchButtons;
	}

	@Override
	public void setFilterModel( FilterModel model )
	{
		this.filterModel = model;
	}

	@Override
	public void setActionModel( ActionModel model )
	{
		this.actionModel = model;
	}

	@Override
	public void setDataQueryColsNameMap( ColumnModel model )
	{
		this.columnModel = model;
	}

	@Override
	public void setBasicDataQuery( String basicDataQuery )
	{
		this.basicDataQuery = basicDataQuery;
	}

	@Override
	public void setBasicDataQueryFilters( List<QueryFilter> basicDataQueryFilters )
	{
		this.basicDataQueryFilters = basicDataQueryFilters;
	}

	@Override
	public void setSessionDataQueryFilters( List<QueryFilter> sessionDataQueryFilters )
	{
		this.sessionDataQueryFilters = sessionDataQueryFilters;
	}

	@Override
	public void setIsPagedResult( Boolean isPaggedResult )
	{
		this.isPaggedResult = isPaggedResult;
	}

	public String getResponseJSString()
	{
		// TODO return the response
		return null;
	}

}
