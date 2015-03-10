package org.amaze.rest.framework.actions.defaults;

import java.util.List;

import org.amaze.db.hibernate.AbstractHibernateObject;
import org.amaze.rest.framework.actions.Action;
import org.amaze.rest.framework.detail.Detail;

@org.amaze.rest.framework.annotations.Action
public class NewAction implements Action
{
	private String screenName;
	private Detail detail;
	
	@Override
	public String action( List<Integer> actionIds )
	{
		try
		{
			detail.setEntity( ( AbstractHibernateObject ) Class.forName( detail.getEntityName() ).newInstance() );
		}
		catch( InstantiationException | IllegalAccessException | ClassNotFoundException e )
		{
			
		}
		return detail.getRender();
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
	public void setDetail( Detail detail )
	{
		this.detail = detail;
	}
	
	public Detail getDetail()
	{
		return detail;
	}
}
