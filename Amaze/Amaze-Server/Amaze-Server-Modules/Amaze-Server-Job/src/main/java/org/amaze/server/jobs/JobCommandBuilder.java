package org.amaze.server.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.Job;
import org.amaze.db.hibernate.objects.JobDefinition;
import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.PropertyValueGroup;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.jobs.exceptions.JobBuilderException;

public class JobCommandBuilder
{

	public Map<String, String> buildCommand( Job job, JobDefinition jobDefinition )
	{
		Map<String, String> commands = new HashMap<String, String>();
		StringBuffer simpleJobCommand = new StringBuffer();
		if ( jobDefinition.getJbdNeedStreamDeploy() )
		{
			if ( jobDefinition.getJbdCron() != null )
				simpleJobCommand.append( " cron-trigger --cron='" + jobDefinition.getJbdCron() + "' " );
			else if ( jobDefinition.getJbdFixedDelayTrig() != null )
				simpleJobCommand.append( " fixed-delay-trigger --fixedDelay=" + jobDefinition.getJbdFixedDelayTrig() + "' " );
			else if ( jobDefinition.getJbdTriggeringEvent() != null )
				simpleJobCommand.append( " " + jobDefinition.getJbdTriggeringEvent() + " --" + getTriggeringEventWithProperties( jobDefinition.getJbdCompTriggeredPvgIdPropertyValueGroup() ) + " " );
			if ( jobDefinition.getJbdPayloadPmt() != null )
				simpleJobCommand.append( " --payload='" + jobDefinition.getJbdPayloadPmt() + "' " );
			if ( simpleJobCommand.toString().length() > 0 )
			{
				simpleJobCommand.append( " > queue:job: " );
				simpleJobCommand.append( job.getJobName() );
			}
		}
		else
			simpleJobCommand.append( job.getJobName() );
		simpleJobCommand.append( getJobDfnWithProperties( job, jobDefinition ) );
		commands.put( "definition", simpleJobCommand.toString() );
		commands.put( "name", jobDefinition.getJbdName() );
		commands.put( "deploy", jobDefinition.getJbdDeployOnLoad().toString() );
		return commands;
	}

	public List<Map<String, String>> buildCommand()
	{
		List<JobDefinition> jobDefinitions = HibernateSession.query( "from JobDefinition jbd where jbd.jbdCreateOnLoad = :jbdCreateOnLoad", "jbdCreateOnLoad", true );
		return buildCommand( jobDefinitions );
	}

	public List<Map<String, String>> buildCommand( List<JobDefinition> jobDefinitions )
	{
		List<Map<String, String>> commands = new ArrayList<Map<String, String>>();
		for ( JobDefinition eachJobDefinition : jobDefinitions )
			commands.add( buildCommand( getJob( eachJobDefinition ), eachJobDefinition ) );
		return commands;
	}

	public Map<String, String> buildCommand( JobDefinition jobDefinition )
	{
		return buildCommand( getJob( jobDefinition ), jobDefinition );
	}

	public Map<String, String> buildCommand( String jobDefinitionName )
	{
		JobDefinition jobDefinition = HibernateSession.queryExpectExactlyOneRow( "from JobDefinition jbd where jbd.jbdName = :jbdName", "jbdName", jobDefinitionName );
		if ( jobDefinition != null )
		{
			return buildCommand( getJob( jobDefinition ), jobDefinition );
		}
		throw new JobBuilderException( " Job data configured in the DB is incorrect for the Job Definition Name " + jobDefinitionName + "... " );
	}

	private Job getJob( JobDefinition jobDefinition )
	{
		return HibernateSession.get( Job.class, jobDefinition.getJobIdJob().getJobId() );
	}

	private String getTriggeringEventWithProperties( PropertyValueGroup jbdCompTriggeredPvgIdPropertyValueGroup )
	{
		StringBuffer eventProperties = new StringBuffer();
		if ( jbdCompTriggeredPvgIdPropertyValueGroup != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jbdCompTriggeredPvgIdPropertyValueGroup.getPgpId() );
			String prvValue = null;
			for ( PropertyValue eachProperty : propertyValues )
			{
				prvValue = eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null;
				if ( prvValue != null )
					eventProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( prvValue );
			}
		}
		return eventProperties.toString();
	}

	private String getJobDfnWithProperties( Job job, JobDefinition jobDefinition )
	{
		StringBuffer jobProperties = new StringBuffer();
		if ( jobDefinition.getPvgIdPropertyValueGroup() != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jobDefinition.getPvgIdPropertyValueGroup().getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
			{
				String prvValue = eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null;
				if ( prvValue != null )
					jobProperties.append( " --" ).append( eachProperty.getPrtName() ).append( "=" ).append( "'" + prvValue + "'" );
			}
		}
		return jobProperties.toString();
	}

}
