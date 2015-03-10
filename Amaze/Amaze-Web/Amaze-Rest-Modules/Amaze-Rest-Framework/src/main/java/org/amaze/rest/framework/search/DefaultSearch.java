package org.amaze.rest.framework.search;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.rest.framework.actions.ActionModel;
import org.amaze.rest.framework.columns.Column;
import org.amaze.rest.framework.columns.ColumnModel;
import org.amaze.rest.framework.filters.FilterModel;
import org.amaze.rest.framework.query.QueryFilter;
import org.codehaus.jackson.annotate.JsonIgnore;

@org.amaze.rest.framework.annotations.Search
public class DefaultSearch implements Search
{
	
	private String moduleName;
	private String screenName;
	private String searchEntity;

	private List<SearchButton> searchButtons;
	private FilterModel filterModel;
	private ActionModel actionModel;
	private ColumnModel columnModel;
	private String basicDataQuery;
	private List<QueryFilter> dataQueryFilters;
	
	@Override
	public String getModuleName()
	{
		return moduleName;
	}

	@Override
	public void setModuleName( String moduleName )
	{
		this.moduleName = moduleName;
	}

	@Override
	public String getScreenName()
	{
		return screenName;
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

	public List<QueryFilter> getDataQueryFilters()
	{
		return dataQueryFilters;
	}

	public void setSearchButtons( List<SearchButton> searchButtons )
	{
		this.searchButtons = searchButtons;
	}

	public void setColumnModel( ColumnModel columnModel )
	{
		this.columnModel = columnModel;
	}

	@Override
	public void setScreenName( String screenName )
	{
		this.screenName = screenName;
	}

	@Override
	public void setSearchEntity( String entity )
	{
		this.searchEntity = entity;
	}

	@Override
	public String getSearchEntity()
	{
		return searchEntity;
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
	@JsonIgnore
	public void setBasicDataQuery( String basicDataQuery )
	{
		this.basicDataQuery = basicDataQuery;
	}

	@Override
	@JsonIgnore
	public void setDataQueryFilters( List<QueryFilter> dataQueryFilters )
	{
		this.dataQueryFilters = dataQueryFilters;
	}

	@Override
	public String toString()
	{
		return JsonConverter.fromJavaToJson( this );
	}
	
	@Override
	@JsonIgnore
	public Object getData( String limit, String offset, String order )
	{
		return getData( limit, offset, order, null, null );
	}
	
	
	@Override
	@JsonIgnore
	public List<Object> getData( String limit, String offset, String sort, String order, String filterParams )
	{
		StringBuffer sb = new StringBuffer();
		List<Column> cols = this.columnModel.getColumns();
		for( Column eachCol : cols )
		{
			sb.append( eachCol.getDataProperty() + "," ); 
		}
		String hql = " SELECT " + sb.toString().substring( 0, sb.toString().length() - 1 ) + " FROM " + getSearchEntity();
		if( basicDataQuery != null && basicDataQuery.length() > 0 )
			hql = hql + " WHERE " + basicDataQuery;
		Boolean containsFilter = false;
		for( QueryFilter eachFilter : dataQueryFilters )
		{
			if( !eachFilter.getIsSessionValuedFilter() )
			{
				hql = eachFilter.getDataProperty() + " = '" + eachFilter.getDataPropertyValue() + "' and ";
			}
			else
			{
				hql = eachFilter.getDataProperty() + " = '" + eachFilter.getDataPropertyValue() + "' and ";
//				hql = eachFilter.getDataProperty() + " = '" + AmazeSession.getPropertyValue(eachFilter.getDataPropertyValue() ) + "' and ";
			}
			containsFilter = true;
		}
		if( containsFilter )
			hql = hql.substring( 0, hql.lastIndexOf( "and " ) );
		return HibernateSession.pageFind( hql, limit != null ? Integer.valueOf( limit ) : 50, offset != null ? Integer.valueOf( offset ) : 0, sort, order, filterParams );
	}

}
