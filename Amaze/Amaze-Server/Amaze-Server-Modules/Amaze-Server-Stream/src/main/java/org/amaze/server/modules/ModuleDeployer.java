package org.amaze.server.modules;

import java.util.Map;

import org.amaze.commons.api.rest.RestApiUtils;
import org.amaze.db.hibernate.objects.JobDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ModuleDeployer
{
	
	@Autowired
	ModuleCommandBuilder moduleCommandBuilder;

	@Autowired
	RestApiUtils apiUtils;
	
	public ModuleCommandBuilder getCommandBuilder()
	{
		return moduleCommandBuilder;
	}

	public void setCommandBuilder( ModuleCommandBuilder commandBuilder )
	{
		this.moduleCommandBuilder = commandBuilder;
	}

	public RestApiUtils getApiUtils()
	{
		return apiUtils;
	}

	public void setApiUtils( RestApiUtils apiUtils )
	{
		this.apiUtils = apiUtils;
	}
	
	public void deploy( JobDefinition jobDefinition )
	{
		apiUtils.post( "/jobs/deployments" + jobDefinition.getJbdName(), null );
	}
	
	public void deploy( String jobDefinitionName )
	{
		apiUtils.post( "/jobs/deployments/" + jobDefinitionName, null );
	}
	
	public void unDeploy( JobDefinition jobDefinition )
	{
		apiUtils.delete( "/jobs/deployments/" + jobDefinition.getJbdName() );
	}
	
	public void unDeploy( String jobDefinitionName )
	{
		apiUtils.delete( "/jobs/deployments/" + jobDefinitionName );
	}
	
	public void unDeployAll()
	{
		apiUtils.delete( "/jobs/deployments/" );
	}
	
	public String getJob( JobDefinition jobDefinition )
	{
		return apiUtils.get( "/jobs/deployments/" + jobDefinition.getJbdName() );
	}
	
	public String getJob( String jobDefinitionName )
	{
		return apiUtils.get( "/jobs/deployments/" + jobDefinitionName );
	}
	
	public String getJobAll()
	{
		return apiUtils.get( "/jobs/deployments/" );
	}
	
	public String launchJob( String jobName, Map<String, String> params )
	{
		return apiUtils.post( "/jobs/executions?jobname=" + jobName, params );
	}
	
}
