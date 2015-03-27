package org.amaze.web.security.session;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class SecuritySession
{
	private String sessionId;
	private SecurityUser securityUser;

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId( final String sessionId )
	{
		this.sessionId = sessionId;
	}

	@JsonDeserialize( as = SecurityUser.class )
	public SecurityUser getSecurityUser()
	{
		return securityUser;
	}

	public void setSecurityUser( final SecurityUser securityUser )
	{
		this.securityUser = securityUser;
	}

}
