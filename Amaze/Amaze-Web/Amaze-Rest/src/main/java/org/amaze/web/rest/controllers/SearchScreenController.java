package org.amaze.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amaze.web.rest.AmazeRestUrls;
import org.amaze.web.rest.controllers.resource.ResourceManager;
import org.amaze.web.utils.SearchScreenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchScreenController
{

	@Autowired
	private ResourceManager resourceManager;

	@RequestMapping( value = AmazeRestUrls.SEARCHSCREEN_URL + "/search", method = RequestMethod.GET, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody Map<String, List<SearchScreenHolder>> search( HttpServletRequest request, HttpSession httpSession )
	{
		httpSession.setAttribute( "time", "1TIme" );
		Map<String, List<SearchScreenHolder>> allSearches = resourceManager.getAllSearches();
		return allSearches;
	}
	
	@RequestMapping( value = AmazeRestUrls.SEARCHSCREEN_URL + "/{searchName}", method = RequestMethod.GET, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody String search( @PathVariable String searchName )
	{
		return resourceManager.getSearch( searchName ).toString();	
	}

	@RequestMapping( value = AmazeRestUrls.SEARCHSCREEN_URL + "/{searchName}/data", method = RequestMethod.GET, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody List<Object> searchDataPagination( @PathVariable String searchName, @RequestParam( required = false ) String limit, @RequestParam( required = false ) String offset, @RequestParam( required = false ) String sort, @RequestParam(required = false) String order, @RequestParam( required = false) String filterParams )
	{
		return resourceManager.getSearch( searchName ).getData( limit, offset, sort, order, filterParams );		
	}
	
	@RequestMapping( value = AmazeRestUrls.SEARCHSCREEN_URL + "/{searchName}/action/{actionName}/{detail}", method = RequestMethod.GET, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody String search( @PathVariable String searchName, @PathVariable String actionName, @RequestParam( required = false) String idList, @PathVariable String detail )
	{
		List<Integer> actionIds = getActionContext( idList );
		return resourceManager.getAction( searchName, actionName, detail ).action( actionIds );
	}

	private List<Integer> getActionContext( String actionIds )
	{
		List<Integer> idList = new ArrayList<Integer>();
		if( actionIds != null )
		{
			String[] ids = actionIds.split( "," );
			for( String eachId : ids ){
				idList.add( Integer.parseInt( eachId ) );
			}
		}
		return idList;
	}

}