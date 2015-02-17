package org.amaze.server.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.commons.api.rest.RestApiUtils;
import org.amaze.db.hibernate.objects.JobDefinition;
import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.PropertyValueGroup;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.springframework.beans.factory.annotation.Autowired;

public class JobDeployer
{

	@Autowired
	JobCommandBuilder jobCommandBuilder;

	@Autowired
	RestApiUtils apiUtils;
	
	public JobCommandBuilder getCommandBuilder()
	{
		return jobCommandBuilder;
	}

	public void setCommandBuilder( JobCommandBuilder commandBuilder )
	{
		this.jobCommandBuilder = commandBuilder;
	}

	public RestApiUtils getApiUtils()
	{
		return apiUtils;
	}

	public void setApiUtils( RestApiUtils apiUtils )
	{
		this.apiUtils = apiUtils;
	}

	public void createAndDeploy( JobDefinition jobDefinition )
	{
		Map<String, String> commands = jobCommandBuilder.buildCommand( jobDefinition );
		doStreamJobDeploy( jobDefinition, commands );
	}

	private void doStreamJobDeploy( JobDefinition jobDefinition, Map<String, String> commands )
	{
		if ( !jobDefinition.getJbdNeedStreamDeploy() )
			apiUtils.post( "/jobs/definitions", commands );
		else
			apiUtils.post( "/streams/definition", commands );
		createJobNotifierStream( jobDefinition );
	}
	
	private void createJobNotifierStream( JobDefinition jobDefinition )
	{
		if ( jobDefinition.getJbdNotifSink() != null )
		{
			Map<String, String> commands = new HashMap<String, String>();
			commands.put( "name", jobDefinition.getJbdName() + "-notifs" );
			commands.put( "deploy", "true" );
			commands.put( "definition", "\":" + jobDefinition.getJbdName() + "-notifications > " + jobDefinition.getJbdNotifSink() + " " + getNotifSinkWithProperties( jobDefinition.getJbdNotifSinkPvgIdPropertyValueGroup() ) );
			apiUtils.post( "/streams/definitions", commands );
		}
	}
	
	private String getNotifSinkWithProperties( PropertyValueGroup jbdNotifSinkPvgIdPropertyValueGroup )
	{
		StringBuffer eventProperties = new StringBuffer();
		if ( jbdNotifSinkPvgIdPropertyValueGroup != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jbdNotifSinkPvgIdPropertyValueGroup.getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				eventProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null );
		}
		return eventProperties.toString();
	}

	public void createAndDeploy( List<JobDefinition> jobDefinitions )
	{
		for ( JobDefinition jobDefinition : jobDefinitions )
			createAndDeploy( jobDefinition );
	}

	public void createAndDeploy( String jobDefinitionName )
	{
		Map<String, String> commands = jobCommandBuilder.buildCommand( jobDefinitionName );
		doStreamJobDeploy( ( JobDefinition ) HibernateSession.queryExpectExactlyOneRow( "from JobDefinition jbd where jbd.jbdName = :jbdName", "jbdName", jobDefinitionName ), commands );
	}

	public void createAndDeployAll()
	{
		List<Map<String, String>> commands = jobCommandBuilder.buildCommand();
		for ( Map<String, String> eachJobDefinitionCommand : commands )
			doStreamJobDeploy( ( JobDefinition ) HibernateSession.queryExpectExactlyOneRow( "from JobDefinition jbd where jbd.jbdName = :jbdName", "jbdName", eachJobDefinitionCommand.get( "name" ) ), eachJobDefinitionCommand );
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
