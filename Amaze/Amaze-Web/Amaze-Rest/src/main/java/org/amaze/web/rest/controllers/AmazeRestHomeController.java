package org.amaze.web.rest.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/Home" )
public class AmazeRestHomeController
{

	@RequestMapping( value = "/{dashboardName}", method = RequestMethod.GET )
	public String renderDashboard( @PathVariable String name )
	{
		String result = "Hello dashboard " + name;
		return result;
	}
	
}
