package org.amaze.web.security.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SecurityUser
{

	private String userId;
	private String fullName;
	private boolean active;
	private Set<String> userRoles;
	private Set<String> userModules;
	private String password;
	private Map<String, String> sessionValues;

	public SecurityUser()
	{
		super();
	}

	public SecurityUser( String userID, String fullName, Set<String> userRoles, final Set<String> userModules, final boolean active )
	{
		this.userId = userID;
		this.fullName = fullName;
		this.userRoles = userRoles;
		this.userModules = userModules;
		this.active = active;
	}
	
	public Map<String, String> getSessionValues()
	{
		if( sessionValues == null )
			sessionValues = new HashMap<String, String>();
		return sessionValues;
	}

	public void setSessionValues( Map<String, String> sessionValues )
	{
		this.sessionValues = sessionValues;
	}
	
	public Set<String> getUserModules()
	{
		return userModules;
	}

	public void setUserModules( Set<String> userModules )
	{
		this.userModules = userModules;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( final String userId )
	{
		this.userId = userId;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName( final String fullName )
	{
		this.fullName = fullName;
	}

	public Set<String> getUserRoles()
	{
		if ( userRoles == null )
			userRoles = new HashSet<String>();
		return userRoles;
	}

	public void setUserRoles( final Set<String> userRoles )
	{
		this.userRoles = userRoles;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive( final boolean active )
	{
		this.active = active;
	}

	public void setPassword( final String password )
	{
		this.password = password;

	}

	public String getPassword()
	{
		return password;
	}

}
