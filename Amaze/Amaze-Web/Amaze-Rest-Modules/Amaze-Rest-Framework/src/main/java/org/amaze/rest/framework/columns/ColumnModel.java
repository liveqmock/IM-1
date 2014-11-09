package org.amaze.rest.framework.columns;

import java.util.List;

import org.amaze.rest.framework.models.Model;

public interface ColumnModel extends Model
{
	public List<Column> getColumns();
	public void setColumns( List<Column> filters );
	public void addColumn( Column filter );
	
}
