package org.amaze.web.rest.controllers.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.rest.framework.actions.Action;
import org.amaze.rest.framework.actions.ActionGroupModel;
import org.amaze.rest.framework.actions.ActionItemModel;
import org.amaze.rest.framework.actions.ActionModel;
import org.amaze.rest.framework.detail.Detail;
import org.amaze.rest.framework.search.Search;
import org.amaze.web.exception.ResourceManagerInitializationException;
import org.amaze.web.utils.SearchScreenHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ResourceManager implements ApplicationContextAware
{

	private ApplicationContext ctx;
	private Map<String, Object> searchEntities;
	private Map<String, Map<String, Class< ? >>> actionEntities = new HashMap<String, Map<String, Class< ? >>>();
	private Map<String, List<SearchScreenHolder>> searchNames = new HashMap<String, List<SearchScreenHolder>>();

	@Override
	public void setApplicationContext( ApplicationContext context ) throws BeansException
	{
		try
		{
			this.ctx = context;
			this.searchEntities = ctx.getBeansWithAnnotation( org.amaze.rest.framework.annotations.Search.class );
			for ( Map.Entry<String, Object> eachSearch : searchEntities.entrySet() )
			{
				SearchScreenHolder holder = new SearchScreenHolder( ( ( Search ) eachSearch.getValue() ).getScreenName(), eachSearch.getKey() );
				String moduleName = ( ( Search ) eachSearch.getValue() ).getModuleName();
				if ( searchNames.containsKey( moduleName ) )
				{
					searchNames.get( moduleName ).add( holder );
				}
				else
				{
					List<SearchScreenHolder> holders = new ArrayList<SearchScreenHolder>();
					holders.add( holder );
					searchNames.put( moduleName, holders );
				}
				actionEntities.put( eachSearch.getKey(), getAllActionMap( ( Search ) eachSearch.getValue() ) );
			}
		}
		catch ( Exception e )
		{
			( ( XmlWebApplicationContext ) context ).registerShutdownHook();
			( ( XmlWebApplicationContext ) context ).close();
			throw new ResourceManagerInitializationException( e );
		}
		finally
		{
			
		}
	}

	private Map<String, Class< ? >> getAllActionMap( Search value ) throws ClassNotFoundException
	{
		ActionModel model = value.getActionModel();
		Map<String, Class< ? >> actionClasses = new HashMap<String, Class< ? >>();
		for ( ActionGroupModel groupModel : model.getActionGroupModels() )
		{
			for ( ActionItemModel actionModel : groupModel.getActionItemModels() )
			{
				actionClasses.put( actionModel.getRelativeUrl(), Class.forName( actionModel.getActionClass() ) );
			}
		}
		return actionClasses;
	}

	public Search getSearch( String searchName )
	{
		return ( Search ) searchEntities.get( searchName );
	}

	public Map<String, List<SearchScreenHolder>> getAllSearches()
	{
		return searchNames;
	}

	public Action getAction( String searchName, String actionName, String detail )
	{
		try
		{
			Class< ? > clazz = actionEntities.get( searchName ).get( actionName );
			Action action = ( Action ) clazz.newInstance();
			action.setDetail((Detail)ctx.getBean( detail ));
			return action;
		}
		catch ( Exception e )
		{
			throw new AmazeException( e );
		}
		finally
		{
			
		}
	}

}
