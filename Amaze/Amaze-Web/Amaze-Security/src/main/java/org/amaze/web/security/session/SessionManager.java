package org.amaze.web.security.session;

import javax.servlet.http.HttpSession;

public class SessionManager
{

	public static final ThreadLocal<HttpSession> HTTP_SESSION = new ThreadLocal<>();

	public void setSession( final HttpSession session )
	{
		HTTP_SESSION.set( session );
	}

	public void invalidateSession( final String sessionId )
	{
		HTTP_SESSION.get().removeAttribute( "UserSession" );
	}

}
