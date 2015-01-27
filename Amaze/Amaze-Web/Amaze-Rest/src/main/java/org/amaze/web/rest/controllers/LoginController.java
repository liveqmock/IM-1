package org.amaze.web.rest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.UserPassword;
import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.usage.objects.LoginEvent;
import org.amaze.db.usage.utils.UsageSession;
import org.amaze.web.defaults.SessionBuilder;
import org.amaze.web.exception.LoginException;
import org.amaze.web.rest.AmazeRestUrls;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController
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
				loginResponse.put( "message", String.format( "User %s disabled. Contact System Admin.", name ) );
				return loginResponse;
			}
			List<UserPassword> userPassword = HibernateSession.query( "from UserPassword upw where upw.usrIdUsers.usrId=:UsrId", "UsrId", user.get( 0 ).getId() );
			if ( userPassword.size() == 1 )
			{
				if ( userPassword.get( 0 ).getUpwPassword().equals( password ) )
				{
					LoginEvent loginEvent = new LoginEvent();
					loginEvent.setLetAccessClient( accessClient );
					DateTime dttm = new DateTime();
					loginEvent.setLetCreatedDttm( dttm );
					loginEvent.setLetLoggedDttm( dttm );
					loginEvent.setUsrId( user.get( 0 ).getUsrId() );
					UsageSession.save( loginEvent );
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

	
}