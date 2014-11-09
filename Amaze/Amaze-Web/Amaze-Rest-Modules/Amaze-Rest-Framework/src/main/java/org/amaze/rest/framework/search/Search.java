package org.amaze.rest.framework.search;

import java.util.List;

import org.amaze.rest.framework.actions.ActionModel;
import org.amaze.rest.framework.columns.ColumnModel;
import org.amaze.rest.framework.filters.FilterModel;
import org.amaze.rest.framework.query.QueryFilter;
import org.amaze.rest.framework.screen.Screen;

public interface Search extends Screen 
{
	public void setSearchEntity( String entity );
	public String getSearchEntity();
	public List<SearchButton> getScreenButtons();
	public void setScreenButtons( List<SearchButton> searchButtons );
	public void setFilterModel( FilterModel model );
	public void setActionModel( ActionModel model );
	public void setDataQueryColsNameMap( ColumnModel model );
	public void setBasicDataQuery( String basicDataQuery );
	public void setBasicDataQueryFilters( List<QueryFilter> basicDataQueryFilters );	// For Static filters like status filter and all For example (All those are Status Accepted)
	public void setSessionDataQueryFilters( List<QueryFilter> sessionDataQueryFilters );	// For dynamic filters where data specific to user (that are stored in the session are applied to resolve the expression given in the filter list) for example user location filter
	public void setIsPagedResult( Boolean isPaggedResult );	// For enabling disabling pagging
}
