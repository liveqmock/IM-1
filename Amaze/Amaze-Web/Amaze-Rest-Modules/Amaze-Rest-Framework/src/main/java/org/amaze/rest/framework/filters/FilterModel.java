package org.amaze.rest.framework.filters;

import java.util.List;

import org.amaze.rest.framework.models.Model;

public interface FilterModel extends Model
{
	public List<Filter> getFilters();

	public void setFilters( List<Filter> filters );

	public void addFilter( Filter filter );

}
