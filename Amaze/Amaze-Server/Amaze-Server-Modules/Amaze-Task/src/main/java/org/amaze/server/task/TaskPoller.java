package org.amaze.server.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.db.hibernate.objects.JobDefinition;
import org.amaze.db.hibernate.objects.JobInstance;
import org.amaze.db.hibernate.objects.JobTaskParamValue;
import org.amaze.db.hibernate.objects.Task;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.usage.objects.JobEvent;
import org.amaze.db.usage.utils.UsageSession;
import org.amaze.server.jobs.JobDeployer;
import org.amaze.server.task.constants.TaskStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TaskPoller implements Callable<Boolean>
{

	private static final Logger logger = LogManager.getLogger( TaskPoller.class );

	@Autowired
	private JobDeployer jobDeployer;

	@Override
	public Boolean call()
	{
		while ( true )
		{
			List<String> tskStatus = new ArrayList<String>();
			tskStatus.add( TaskStatus.Created.getStatus() );
			tskStatus.add( TaskStatus.ReScheduled.getStatus() );
			List<Task> tasks = HibernateSession.query( "from Task tsk where tsk.tskStatus in ( :tskStatus ) ", "tskStatus", tskStatus );
			for ( Task task : tasks )
			{
				launchJob( task );
			}
			updateJobStatus();
		}
	}

	@Transactional
	private void updateJobStatus()
	{
		List<String> tskStatus = new ArrayList<String>();
		tskStatus.add( TaskStatus.Started.getStatus() );
		List<Task> startedTask = HibernateSession.query( "from Task tsk where tsk.tskStatus in ( :tskStatus )", "tskStatus", tskStatus );
		for( Task eachTask : startedTask )
		{
			
		}
	}

	@Transactional
	private void launchJob( Task task )
	{
		Map<String, String> params = new HashMap<String, String>();
		JobInstance instance = task.getJitIdJobInstance();
		JobDefinition definition = instance.getJbdIdJobDefinition();
		List<JobTaskParamValue> paramsValues = instance.getJobTaskParamValues();
		for ( JobTaskParamValue eachParam : paramsValues )
		{
			params.put( eachParam.getJtpIdJobTaskParam().getJtpName(), eachParam.getJpvValue() != null ? eachParam.getJpvValue() : eachParam.getJtpIdJobTaskParam().getJtpDefault() );
		}
		task.setTskStatus( TaskStatus.Started.getStatus() );
		task.setTskStarted( new DateTime() );
		HibernateSession.update( task );
		createJobEvent( "Created the Job " + definition.getJobIdJob().getJobName() + ", for the definition " + definition.getJbdName() + ", for the instance " + instance.getJitName() + " using the params " + params );
		try
		{
			jobDeployer.launchJob( definition.getJbdName(), params );
		}
		catch ( AmazeException e )
		{
			logger.error( "Job Launching throw a Exception while deploying for the container.. ", e );
		}
	}

	private void createJobEvent( String logs )
	{
		JobEvent event = new JobEvent();
		event.setUsrId( 1 );
		event.setJetEventDttm( new DateTime() );
		event.setJetLogs( logs );
		event.setPtnId( 1 );
		UsageSession.save( event );
	}

	public static void main( String[] args )
	{
		@SuppressWarnings( "resource" )
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "task.xml" );
		ExecutorService executorService = Executors.newFixedThreadPool( 2 );
		executorService.submit( ( TaskPoller ) ctx.getBean( "taskPoller" ) );
	}

}
