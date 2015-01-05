package org.amaze.server.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.JobInstance;
import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.PropertyValueGroup;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.streams.rest.StreamApiUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class JobDeployer
{

	@Autowired
	JobCommandBuilder jobCommandBuilder;

	@Autowired
	StreamApiUtils apiUtils;
	
	public JobCommandBuilder getCommandBuilder()
	{
		return jobCommandBuilder;
	}

	public void setCommandBuilder( JobCommandBuilder commandBuilder )
	{
		this.jobCommandBuilder = commandBuilder;
	}

	public StreamApiUtils getApiUtils()
	{
		return apiUtils;
	}

	public void setApiUtils( StreamApiUtils apiUtils )
	{
		this.apiUtils = apiUtils;
	}

	public void createAndDeploy( JobInstance jobInstance )
	{
		Map<String, String> commands = jobCommandBuilder.buildCommand( jobInstance );
		doStreamJobDeploy( jobInstance, commands );
	}

	private void doStreamJobDeploy( JobInstance jobInstance, Map<String, String> commands )
	{
		if( !jobInstance.getJbdIdJobDefinition().getJbdNeedStreamDeploy() )
			apiUtils.post( "/jobs/definitions", commands );
		else
			apiUtils.post( "/streams/definition", commands );
		createJobNotifierStream( jobInstance );
	}
	
	private void createJobNotifierStream( JobInstance jobInstance )
	{
		if( jobInstance.getJitNotifSink() != null )
		{
			Map<String, String> commands = new HashMap<String, String>();
			commands.put( "name", jobInstance.getJitName() + "-notifs" );
			commands.put( "deploy", "true" );
			commands.put( "definition", "\":" + jobInstance.getJitName() + "-notifications > " + jobInstance.getJitNotifSink() + " " + getNotifSinkWithProperties( jobInstance.getJitNotifSinkPvgIdPropertyValueGroup() ) );
			apiUtils.post( "/streams/definitions", commands );
		}
	}
	
	private String getNotifSinkWithProperties( PropertyValueGroup jitNotifSinkPvgIdPropertyValueGroup )
	{
		StringBuffer eventProperties = new StringBuffer();
		if ( jitNotifSinkPvgIdPropertyValueGroup != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jitNotifSinkPvgIdPropertyValueGroup.getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				eventProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null );
		}
		return eventProperties.toString();
	}

	public void createAndDeploy( List<JobInstance> jobInstances )
	{
		for ( JobInstance jobInstance : jobInstances )
			createAndDeploy( jobInstance );
	}

	public void createAndDeploy( String jobInstanceName )
	{
		Map<String, String> commands = jobCommandBuilder.buildCommand( jobInstanceName );
		doStreamJobDeploy( (JobInstance) HibernateSession.queryExpectExactlyOneRow( "from JobInstance jit where jit.jitName = :jitName", "jitName", jobInstanceName ), commands );
	}

	public void createAndDeployAll()
	{
		List<Map<String, String>> commands = jobCommandBuilder.buildCommand();
		for ( Map<String, String> eachJobInstanceCommand : commands )
			doStreamJobDeploy( (JobInstance) HibernateSession.queryExpectExactlyOneRow( "from JobInstance jit where jit.jitName = :jitName", "jitName", eachJobInstanceCommand.get( "name" ) ), eachJobInstanceCommand );
	}
	
	public void deploy( JobInstance jobInstance )
	{
		apiUtils.post( "/jobs/deployments" + jobInstance.getJitName(), null );
	}
	
	public void deploy( String jobInstanceName )
	{
		apiUtils.post( "/jobs/deployments/" + jobInstanceName, null );
	}
	
	public void unDeploy( JobInstance jobInstance )
	{
		apiUtils.delete( "/jobs/deployments/" + jobInstance.getJitName() );
	}
	
	public void unDeploy( String jobInstanceName )
	{
		apiUtils.delete( "/jobs/deployments/" + jobInstanceName );
	}
	
	public void unDeployAll()
	{
		apiUtils.delete( "/jobs/deployments/" );
	}
	
	public String getJob( JobInstance jobInstance )
	{
		return apiUtils.get( "/jobs/deployments/" + jobInstance.getJitName() );
	}
	
	public String getJob( String jobInstanceName )
	{
		return apiUtils.get( "/jobs/deployments/" + jobInstanceName );
	}
	
	public String getJobAll()
	{
		return apiUtils.get( "/jobs/deployments/" );
	}
}
