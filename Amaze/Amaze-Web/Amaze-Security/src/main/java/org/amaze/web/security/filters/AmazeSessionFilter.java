package org.amaze.web.security.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.amaze.web.security.session.SecuritySession;
import org.amaze.web.security.session.SecurityUser;
import org.amaze.web.security.session.SessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AmazeSessionFilter implements Filter
{

	private static final Logger logger = LogManager.getLogger( AmazeSessionFilter.class );
	
	private static final String REQUESTED_URL = "RequestedURL";
	private static final String USER_SESSION = "UserSession";

	private static final String UNAUTHORIZED_JSON_RESPONSE = "{\"errorMessage\":\"Request not authorized. " + "Get token from AmazeLogin service.\"}";
	private SessionManager amazeSessionManager;
	private String loginUrl;
	private List<String> excludes;
	private int sessionTimeout;
	private String restServiceBaseUrl;
	private String homePage;

	public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain ) throws IOException, ServletException
	{

		HttpServletRequest request = ( HttpServletRequest ) req;
		HttpServletResponse response = ( HttpServletResponse ) res;
		if ( "true".equals( request.getParameter( "logout" ) ) )
		{
			logger.debug( "User logout detected. Clearing session data" );
			request.setAttribute( "sessionId", ( (SecuritySession) request.getSession().getAttribute( USER_SESSION ) ).getSessionId() );
			request.getSession().invalidate();
			chain.doFilter( req, res );
			return;
		}
		HttpSession session = request.getSession();
		amazeSessionManager.setSession( session );
		session.setMaxInactiveInterval( sessionTimeout );
		SecuritySession userSession = ( SecuritySession ) session.getAttribute( USER_SESSION );
		String requestUri = request.getRequestURI();
		if ( loginUrl != null && ( request.getContextPath() + "/" + loginUrl ).equalsIgnoreCase( request.getRequestURI() ) )
		{
			if ( userSession != null && homePage != null )
			{
				logger.debug( "Login URL requested but session already valid and present. Re-directing to homepage ", homePage );
				response.sendRedirect( homePage );
			}
			logger.debug( "LoginUrl detected and no valid session present, bypassing security filter" );
			chain.doFilter( req, res );
			return;
		}
		else
		{
			for ( final String exclude : excludes )
			{
				if ( request.getRequestURI().matches( exclude ) )
				{
					logger.debug( "Url {} matched an exclusion, {}, bypassing security filter", requestUri, exclude );
					chain.doFilter( req, res );
					return;
				}
			}
		}

		if ( userSession == null )
		{
			logger.debug( "HTTP Session has not been authenticated" );
			session.setAttribute( REQUESTED_URL, request.getRequestURI() );
			buildErrorOrRedirectResponse( request, response );
		}
		else
		{
			logger.trace( "HTTP session has previously been authenticated = " + session.getId() + ". Continuing processing for {}.", requestUri );
			final String requestedUrl = ( String ) session.getAttribute( REQUESTED_URL );
			if ( requestedUrl != null )
			{
				logger.trace( "Redirecting from " + requestUri + " to " + requestedUrl );
				session.removeAttribute( REQUESTED_URL );
				response.sendRedirect( requestedUrl );
			}
			final SecurityUser user = userSession.getSecurityUser();
			final StringBuffer roleString = new StringBuffer();
			for ( final String role : user.getUserRoles() )
			{
				if ( roleString.length() > 0 )
					roleString.append( ',' );
				roleString.append( role );
			}
			session.setAttribute( "userRole", roleString.toString() );
			final boolean isAdmin = user.getUserRoles().contains( "Administrator" );
			session.setAttribute( "isAdmin", isAdmin );
			logger.trace( "User role is: {}. User is admin: " + isAdmin, roleString.toString() );
			chain.doFilter( req, res );
			return;
		}
		chain.doFilter( req, res );
	}

	private void buildErrorOrRedirectResponse( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		if ( restServiceBaseUrl != null && request.getRequestURI().matches( restServiceBaseUrl ) )
		{
			response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
			response.setContentType( MediaType.APPLICATION_JSON_VALUE );
			response.getOutputStream().print( UNAUTHORIZED_JSON_RESPONSE );
		}
		else
		{
			if ( loginUrl == null )
				response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "LoginUrl not configured" );
			else
				response.sendRedirect( loginUrl );
		}
	}

	public void init( FilterConfig filterConfig ) throws ServletException
	{
		amazeSessionManager = ( SessionManager ) WebApplicationContextUtils.getRequiredWebApplicationContext( filterConfig.getServletContext() ).getBean( "amazeSessionManager" );
		loginUrl = filterConfig.getInitParameter( "LoginUrl" );
		restServiceBaseUrl = filterConfig.getInitParameter( "RestServiceBaseUrl" );
		homePage = filterConfig.getInitParameter( "HomePage" );
		final String excludesString = filterConfig.getInitParameter( "Exclusions" );
		final String timeoutString = filterConfig.getInitParameter( "SessionTimeout" );
		logger.debug( "Security Filter configured with loginUrl = {} Exclusions = {} " + "SessionTimeout = {}", loginUrl, excludesString, timeoutString );
		sessionTimeout = 900;
		if ( timeoutString == null )
			logger.debug( "SessionTimeout not specified, defaulting to 900 seconds" );
		else
		{
			try
			{
				sessionTimeout = Integer.parseInt( timeoutString );
			}
			catch ( NumberFormatException ex )
			{
				logger.warn( "SessionTimeout is set in security filter but not parseable as an integer. Will use default " + "of 900 seconds. Value = " + timeoutString, ex );
			}
		}
		if ( excludesString == null )
			excludes = new ArrayList<>();
		else
			excludes = Arrays.asList( excludesString.split( "\\s*,\\s*" ) );
	}

	public void destroy()
	{
		logger.debug("Destroying the SessionFilter... ");
	}

}
