package org.amaze.rest.framework.filters;

import java.util.List;

import org.amaze.rest.framework.models.Model;

public interface FilterModel extends Model 
{
	public List<Filter> getFilters();
	public void getFilters( List<Filter> filters );
	public void addFilter( Filter filter );
	public String getOutputQueryStringForEntityFiltering();
}
