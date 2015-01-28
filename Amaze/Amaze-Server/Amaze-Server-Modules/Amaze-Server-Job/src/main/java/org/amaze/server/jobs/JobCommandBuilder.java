package org.amaze.server.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amaze.db.hibernate.objects.JobDefinition;
import org.amaze.db.hibernate.objects.JobInstance;
import org.amaze.db.hibernate.objects.PropertyValue;
import org.amaze.db.hibernate.objects.PropertyValueGroup;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.jobs.exceptions.JobBuilderException;

public class JobCommandBuilder
{

	public Map<String, String> buildCommand( JobDefinition jobDefinition, JobInstance jobInstance )
	{
		Map<String, String> commands = new HashMap<String, String>();
		StringBuffer simpleJobCommand = new StringBuffer();
		simpleJobCommand.append( "\"" );
		if( jobInstance.getJitCron() != null )
			simpleJobCommand.append( " cron-trigger --cron='" + jobInstance.getJitCron() + "' "  );
		else if( jobInstance.getJitFixedDelayTrig() != null )
			simpleJobCommand.append( " fixed-delay-trigger --fixedDelay=" + jobInstance.getJitFixedDelayTrig() + "' "  );
		else if( jobInstance.getJitTriggeringEvent() != null )
			simpleJobCommand.append( " " + jobInstance.getJitTriggeringEvent() + " --" + getTriggeringEventWithProperties( jobInstance.getJitCompTriggeredPvgIdPropertyValueGroup() ) + " " );
		if( jobInstance.getJitPayloadPmt() != null )
			simpleJobCommand.append( " --payload='" + jobInstance.getJitPayloadPmt() + "' " );
		if( simpleJobCommand.toString().length() > 0 )
		{
			simpleJobCommand.append( " > queue:job: " );
			simpleJobCommand.append( jobDefinition.getJbdName() );
		}
		else
			simpleJobCommand.append( jobDefinition.getJbdName() );
		simpleJobCommand.append( getJobDfnWithProperties( jobDefinition, jobInstance ) );
		simpleJobCommand.append( "\"" );
		commands.put( "definition", "\"" + simpleJobCommand.toString() );
		commands.put( "name", jobInstance.getJitName() );
		commands.put( "deploy", jobInstance.getJitDeployOnLoad().toString() );
		return commands;
	}
	
	public List<Map<String, String>> buildCommand()
	{
		List<JobInstance> jobInstances = HibernateSession.query( "from JobInstance jit where jit.jitCreateOnLoad = :jitCreateOnLoad", "jitCreateOnLoad", true ); 
		return buildCommand( jobInstances );
	}
	
	public List<Map<String, String>> buildCommand( List<JobInstance> jobInstances )
	{
		List<Map<String, String>> commands = new ArrayList<Map<String, String>>();
		for ( JobInstance eachJobInstance : jobInstances )
			commands.add( buildCommand( getJobDfn( eachJobInstance ), eachJobInstance ) );
		return commands;
	}
	
	public Map<String, String> buildCommand( JobInstance jobInstance )
	{
		return buildCommand( getJobDfn( jobInstance ) , jobInstance );
	}
	
	public Map<String, String> buildCommand( String jobInstanceName )
	{
		JobInstance jobInstance = HibernateSession.queryExpectExactlyOneRow( "from JobInstance jit where jit.jitName = :jitName", "jitName", jobInstanceName );
		if( jobInstance != null )
		{
			return buildCommand( getJobDfn( jobInstance ) , jobInstance );
		}
		throw new JobBuilderException( " Job data configured in the DB is incorrect for the JobName " + jobInstanceName + "... " );
	}
	
	private JobDefinition getJobDfn( JobInstance jobInstance )
	{
		return HibernateSession.get( JobDefinition.class, jobInstance.getJbdIdJobDefinition().getJbdId() );
	}
	
	private String getTriggeringEventWithProperties( PropertyValueGroup jitCompTriggeredPvgIdPropertyValueGroup )
	{
		StringBuffer eventProperties = new StringBuffer();
		if ( jitCompTriggeredPvgIdPropertyValueGroup != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jitCompTriggeredPvgIdPropertyValueGroup.getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				eventProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null );
		}
		return eventProperties.toString();
	}

	private String getJobDfnWithProperties( JobDefinition jobDefinition, JobInstance jobInstance )
	{
		StringBuffer jobProperties = new StringBuffer();
		String moduleName = jobDefinition.getJbdName();
		if ( jobInstance.getPvgIdPropertyValueGroup() != null )
		{
			List<PropertyValue> propertyValues = HibernateSession.query( "from PropertyValue prv where prv.pvgId = :pvgId", "pvgId", jobInstance.getPvgIdPropertyValueGroup().getPgpId() );
			for ( PropertyValue eachProperty : propertyValues )
				jobProperties.append( " --" ).append( eachProperty.getPrtName() ).append( " " ).append( eachProperty.getPrvValue() != null ? eachProperty.getPrvValue() : eachProperty.getPrtIdProperty() != null ? eachProperty.getPrtIdProperty().getPrtDefault() : null );
		}
		return moduleName + " " + jobProperties;
	}
	
}

