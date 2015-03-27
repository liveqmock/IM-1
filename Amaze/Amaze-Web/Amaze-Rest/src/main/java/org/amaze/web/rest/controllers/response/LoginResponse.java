package org.amaze.web.rest.controllers.response;

import org.amaze.web.security.session.SecuritySession;

public class LoginResponse
{
	private String errorMessage;
	
	private boolean isError;

	private SecuritySession session;
	
	public boolean isError()
	{
		return isError;
	}

	public void setError( boolean isError )
	{
		this.isError = isError;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage( final String errorMessage )
	{
		this.errorMessage = errorMessage;
	}

	public SecuritySession getSession()
	{
		return session;
	}

	public void setSession( final SecuritySession session )
	{
		this.session = session;
	}

}
