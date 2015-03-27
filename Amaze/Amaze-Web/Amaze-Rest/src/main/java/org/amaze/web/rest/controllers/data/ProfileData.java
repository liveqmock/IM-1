package org.amaze.web.rest.controllers.data;

import java.util.List;

public class ProfileData
{
	private String userName;
	private String password;
	private String displayName;
	private String designation;
	private String location;
	private List<String> contactNos;
	private List<String> emails;
	private String webPage;
	private String status;
	private List<String> currentRoles;
	private String profileFileName;
	
	public String getProfileFileName()
	{
		return profileFileName;
	}

	public void setProfileFileName( String profileFileName )
	{
		this.profileFileName = profileFileName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName( String displayName )
	{
		this.displayName = displayName;
	}

	public String getDesignation()
	{
		return designation;
	}

	public void setDesignation( String designation )
	{
		this.designation = designation;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation( String location )
	{
		this.location = location;
	}

	public List<String> getContactNos()
	{
		return contactNos;
	}

	public void setContactNos( List<String> contactNos )
	{
		this.contactNos = contactNos;
	}

	public List<String> getEmails()
	{
		return emails;
	}

	public void setEmails( List<String> emails )
	{
		this.emails = emails;
	}

	public String getWebPage()
	{
		return webPage;
	}

	public void setWebPage( String webPage )
	{
		this.webPage = webPage;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus( String status )
	{
		this.status = status;
	}

	public List<String> getCurrentRoles()
	{
		return currentRoles;
	}

	public void setCurrentRoles( List<String> currentRoles )
	{
		this.currentRoles = currentRoles;
	}
	
}
