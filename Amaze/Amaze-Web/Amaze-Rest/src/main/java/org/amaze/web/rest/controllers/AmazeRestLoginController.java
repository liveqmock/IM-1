package org.amaze.web.rest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.UserPassword;
import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.web.defaults.SessionBuilder;
import org.amaze.web.exception.LoginException;
import org.amaze.web.rest.AmazeRestUrls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class AmazeRestLoginController
{

	@Autowired
	SessionBuilder sessionBuilder;

	@RequestMapping( value = AmazeRestUrls.LOGIN_URL, method = RequestMethod.POST, headers=AmazeRestUrls.ACCEPT_JSON_HEADER)
	public @ResponseBody Map<String,String> login( @RequestParam String name, @RequestParam String password, @RequestParam String accessClient )
	{
		Map<String,String> loginResponse = new HashMap<String, String>();
		List<Users> user = HibernateSession.query( "from Users usr where usr.usrName=:UsrName", "UsrName", name );
		if ( user.size() == 1 )
		{
			if ( user.get( 0 ).getUsrDisabled() )
			{
				loginResponse.put( "message", String.format( "User %1 disabled. Contact System Admin.", name ) );
				return loginResponse;
			}
			List<UserPassword> userPassword = HibernateSession.query( "from UserPassword upw where upw.usrIdUsers.usrId=:UsrId", "UsrId", user.get( 0 ) );
			if ( userPassword.size() == 1 )
			{
				if ( userPassword.get( 0 ).getUpwPassword().equals( password ) )
				{
//					LoginEvent loginEvent = new LoginEvent();
//					loginEvent.setLetAccessClient( accessClient );
//					DateTime dttm = new DateTime();
//					loginEvent.setLetCreatedDttm( dttm );
//					loginEvent.setLetLoggedDttm( dttm );
//					loginEvent.setUsrId( user.get( 0 ).getUsrId() );
//					UsageSession.save( loginEvent );
					loginResponse.put( "message", "Login Validated" );
					loginResponse.put( "name", name );
					loginResponse.put( "accessClient", accessClient );
					loginResponse = sessionBuilder.build(  user.get( 0 ).getUsrId(), loginResponse );
					return loginResponse;
				}
				else
				{
					loginResponse.put( "message", String.format( "INvalid UserName Or Password" ) );
					return loginResponse;
				}
			}
			else
				throw new LoginException( String.format( "Multiple Rows found for the UserName %1", name ) );
		}
		else if ( user.size() == 0 )
		{
			loginResponse.put( "message", String.format( "Invalid UserName Or Password" ) );
			return loginResponse;
		}
		else
			throw new LoginException( String.format( "Multiple Rows found for the UserName %1", name ) );
	}

	public static void main( String[] args )
	{
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		template.getMessageConverters().add(new StringHttpMessageConverter());
		String uri = new String("http://" + "http://localhost:8080/Amaze-Rest-0.0.1-SNAPSHOT/AmazeRest");
		Vars v = new Vars( "Test", "Test", "Test" );
		template.postForLocation( uri, v , String.class, new Object[]{} );
	}
	
}

class Vars {
	
	public Vars(String name, String password, String accessClient){
		this.name= name;
		this.password = password;
		this.accessClient = accessClient;
	}
	String name;
	String password;
	String accessClient;
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword( String password )
	{
		this.password = password;
	}
	public String getAccessClient()
	{
		return accessClient;
	}
	public void setAccessClient( String accessClient )
	{
		this.accessClient = accessClient;
	}
	
	
}
