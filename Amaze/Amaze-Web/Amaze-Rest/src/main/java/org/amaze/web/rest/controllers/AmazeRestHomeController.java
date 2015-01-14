package org.amaze.web.rest.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmazeRestHomeController
{

	private static final String template = "Hello, %s!";
    
	@RequestMapping( value = "/{Home}", method = RequestMethod.GET )
	public String renderDashboard( @PathVariable String name )
	{
		String result = "Hello dashboard " + name;
		return result;
	}
	
	

//    @RequestMapping("/greeting")
//    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
//        return String.format(template, name);
//    }
	
}
