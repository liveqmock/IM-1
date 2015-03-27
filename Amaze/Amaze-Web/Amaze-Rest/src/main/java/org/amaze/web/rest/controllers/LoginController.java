package org.amaze.web.rest.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amaze.db.hibernate.objects.ModuleRoleMap;
import org.amaze.db.hibernate.objects.UserPassword;
import org.amaze.db.hibernate.objects.UserRoleMap;
import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.usage.objects.LoginEvent;
import org.amaze.db.usage.utils.UsageSession;
import org.amaze.web.defaults.SessionBuilder;
import org.amaze.web.exception.LoginException;
import org.amaze.web.rest.AmazeRestUrls;
import org.amaze.web.rest.controllers.data.ProfileData;
import org.amaze.web.rest.controllers.response.LoginResponse;
import org.amaze.web.rest.controllers.response.LogoutResponse;
import org.amaze.web.security.session.SecuritySession;
import org.amaze.web.security.session.SecurityUser;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@RestController
public class LoginController
{

	/*
	 *	For customized user session, like location details etc extends this bean and build a custom SessionBuilder and then wire it in here in the spring context file 
	 */
	@Autowired
	SessionBuilder sessionBuilder;

	private static final String USER_SESSION_ATTRIBUTE = "UserSession";

	private final static LoadingCache<String, SecuritySession> SESSION_CACHE = CacheBuilder.newBuilder().concurrencyLevel( 32 ).expireAfterAccess( 1, TimeUnit.HOURS ).build( new CacheLoader<String, SecuritySession>()
	{
		@Override
		public SecuritySession load( final String key )
		{
			throw new UnsupportedOperationException( "Use only put in this loading cache" );
		}
	} );

	@RequestMapping( value = AmazeRestUrls.LOGIN_URL, method = RequestMethod.POST, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody LoginResponse login( @RequestParam String name, @RequestParam String password, @RequestParam String accessClient )
	{
		LoginResponse loginResponse = new LoginResponse();
		List<Users> user = HibernateSession.query( "from Users usr where usr.usrName=:UsrName", "UsrName", name );
		if ( user.size() == 1 )
		{
			if ( user.get( 0 ).getUsrDisabled() )
			{
				loginResponse.setError( true );
				loginResponse.setErrorMessage( String.format( "User %s disabled. Contact System Admin.", name ) );
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
					String sessionID = UUID.randomUUID().toString();
					while ( SESSION_CACHE.getIfPresent( sessionID ) != null )
					{
						sessionID = UUID.randomUUID().toString();
					}
					Set<String> roles = new HashSet<String>();
					Set<String> modules = new HashSet<String>();
					Users userEntity = user.get( 0 );
					List<UserRoleMap> userRoleMaps = userEntity.getUserRoleMaps();
					for ( UserRoleMap eachUserRoleMap : userRoleMaps )
					{
						roles.add( eachUserRoleMap.getRolIdRole().getRolName() );
						List<ModuleRoleMap> eachModuleRoleMaps = eachUserRoleMap.getRolIdRole().getModuleRoleMaps();
						for ( ModuleRoleMap eachModuleRoleMap : eachModuleRoleMaps )
						{
							modules.add( eachModuleRoleMap.getModIdModule().getModName() );
						}
					}
					SecurityUser securityUser = new SecurityUser();
					securityUser.setActive( true );
					securityUser.setUserId( user.get( 0 ).getUsrName() );
					securityUser.setFullName( user.get( 0 ).getUsrName() );
					securityUser.setPassword( null );
					securityUser.setUserRoles( roles );
					securityUser.setUserModules( modules );
					sessionBuilder.build( user.get( 0 ).getUsrId(), securityUser.getSessionValues() );
					loginResponse.setError( false );
					SecuritySession session = new SecuritySession();
					session.setSecurityUser( securityUser );
					session.setSessionId( sessionID );
					SESSION_CACHE.put( sessionID, session );

					ServletRequestAttributes currentRequestAttributes = ( ServletRequestAttributes ) RequestContextHolder.currentRequestAttributes();
					HttpServletRequest servletRequest = currentRequestAttributes.getRequest();
					HttpSession httpSession = servletRequest.getSession();
					httpSession.setAttribute( USER_SESSION_ATTRIBUTE, session );
					loginResponse.setSession( session );
					return loginResponse;

				}
				else
				{
					loginResponse.setError( true );
					loginResponse.setErrorMessage( String.format( "INvalid UserName Or Password" ) );
					return loginResponse;
				}
			}
			else
				throw new LoginException( String.format( "Multiple Rows found for the UserName %1", name ) );
		}
		else if ( user.size() == 0 )
		{
			loginResponse.setError( true );
			loginResponse.setErrorMessage( String.format( "INvalid UserName Or Password" ) );
			return loginResponse;
		}
		else
			throw new LoginException( String.format( "Multiple Rows found for the UserName %1", name ) );
	}

	@RequestMapping( value = AmazeRestUrls.LOGOUT_URL, method = RequestMethod.POST, headers = AmazeRestUrls.ACCEPT_JSON_HEADER )
	public @ResponseBody LogoutResponse logout()
	{

		LogoutResponse response = new LogoutResponse();
		ServletRequestAttributes currentRequestAttributes = ( ServletRequestAttributes ) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest servletRequest = currentRequestAttributes.getRequest();
		HttpSession httpSession = servletRequest.getSession();
		httpSession.removeAttribute( USER_SESSION_ATTRIBUTE );
		String sessionId = ( String ) servletRequest.getAttribute( "sessionId" );
		if ( sessionId == null || sessionId.isEmpty() )
		{
			response.setErrorMessage( "Session ID not provided" );
			return response;
		}
		SESSION_CACHE.invalidate( sessionId );
		httpSession.invalidate();
		return response;
	}
	
	@RequestMapping( value = AmazeRestUrls.PROFILE_UPDATE, method = RequestMethod.POST )
	public @ResponseBody String profileUpdate( @RequestParam( value = "file", required = true ) MultipartFile file, @RequestParam( value = "profileData", required = true ) ProfileData profileData )
	{
		
		if ( !file.isEmpty() )
		{
			String fileName = "C://DataDir/Profile/User/Images/" + profileData.getUserName() + ".png" ;
			File oldProfileFile = new File( fileName );
			oldProfileFile.delete();
			String name = profileData.getProfileFileName();
			try
			{
				byte[] bytes = file.getBytes();
				BufferedOutputStream stream = new BufferedOutputStream( new FileOutputStream( fileName ) );
				stream.write( bytes );
				stream.close();
				return "You successfully uploaded " + name + "!";
			}
			catch ( Exception e )
			{
				return "Error uploading file" + name + "... " + e.getMessage();
			}
		}
		else
		{
			return "";
		}
	}
	
	
	@RequestMapping( value = AmazeRestUrls.PASSWORD_UPDATE, method = RequestMethod.POST )
	public @ResponseBody String passwordUpdate( @RequestParam( value = "password", required = true ) String password )
	{
		return "";
	}
	
}
