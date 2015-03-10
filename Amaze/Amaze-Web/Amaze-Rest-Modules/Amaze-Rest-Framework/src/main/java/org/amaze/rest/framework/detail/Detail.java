package org.amaze.rest.framework.detail;

import java.util.List;

import org.amaze.db.hibernate.AbstractHibernateObject;
import org.amaze.rest.framework.detail.widgets.Widget;
import org.amaze.rest.framework.screen.Screen;

public interface Detail extends Screen
{

	public void setEntityName( String entity );

	public String getEntityName();
	
	public AbstractHibernateObject getEntity();
	
	public void setEntity( AbstractHibernateObject entity );
	
	public List<DetailButton> getDetailButtons();

	public void setDetailButtons( List<DetailButton> searchButtons );
	
	public List<Widget> getWidgets();

	public void setWidgets( List<Widget> searchButtons );
	
	public String getRender();
	
}
