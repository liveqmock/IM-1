package org.amaze.rest.framework.detail;

import java.util.List;

import org.amaze.commons.converters.JsonConverter;
import org.amaze.db.hibernate.AbstractHibernateObject;
import org.amaze.rest.framework.detail.widgets.Widget;
import org.codehaus.jackson.annotate.JsonIgnore;

public class DefaultDetail implements Detail
{

	private String screenName;
	private String entityName;
	private AbstractHibernateObject entity;
	private List<DetailButton> detailButtons;
	private List<Widget> widgets;
	
	public AbstractHibernateObject getEntity()
	{
		return entity;
	}

	public void setEntity( AbstractHibernateObject entity )
	{
		this.entity = entity;
	}

	@Override
	public String getScreenName()
	{
		return screenName;
	}

	@Override
	public void setScreenName( String screenName )
	{
		this.screenName = screenName;
	}

	@Override
	public void setEntityName( String entityName )
	{
		this.entityName = entityName;
	}

	@Override
	public String getEntityName()
	{
		return this.entityName;
	}

	@Override
	public List<DetailButton> getDetailButtons()
	{
		return this.detailButtons;
	}

	@Override
	public void setDetailButtons( List<DetailButton> detailButtons )
	{
		this.detailButtons = detailButtons;
	}

	@Override
	public List<Widget> getWidgets()
	{
		return this.widgets;
	}

	@Override
	public void setWidgets( List<Widget> widgets )
	{
		this.widgets = widgets;
	}

	@Override
	@JsonIgnore
	public String getRender()
	{
		return JsonConverter.fromJavaToJson( this );
	}

}
