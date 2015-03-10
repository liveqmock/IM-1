package org.amaze.rest.framework.search;

import java.util.List;

import org.amaze.rest.framework.actions.ActionModel;
import org.amaze.rest.framework.columns.ColumnModel;
import org.amaze.rest.framework.filters.FilterModel;
import org.amaze.rest.framework.query.QueryFilter;
import org.amaze.rest.framework.screen.Screen;

public interface Search extends Screen
{
	public String getModuleName();
	
	public void setModuleName( String moduleName );
	
	public void setSearchEntity( String entity );

	public String getSearchEntity();

	public List<SearchButton> getScreenButtons();

	public void setScreenButtons( List<SearchButton> searchButtons );

	public void setFilterModel( FilterModel model );

	public void setActionModel( ActionModel model );
	
	public ActionModel getActionModel();

	public void setColumnModel( ColumnModel model );

	public void setBasicDataQuery( String basicDataQuery );

	public void setDataQueryFilters( List<QueryFilter> basicDataQueryFilters ); // For Static filters like status filter and all For example (All those are Status Accepted)

	public Object getData( String limit, String offset, String order );
	
	public List<Object> getData( String limit, String offset, String order, String ascDesc, String filterParams );

}
