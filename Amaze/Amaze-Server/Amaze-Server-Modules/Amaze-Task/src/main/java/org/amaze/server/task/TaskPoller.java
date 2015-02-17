package org.amaze.server.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.amaze.db.hibernate.objects.JobDefinition;
import org.amaze.db.hibernate.objects.JobInstance;
import org.amaze.db.hibernate.objects.JobTaskParamValue;
import org.amaze.db.hibernate.objects.Task;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.jobs.JobDeployer;
import org.amaze.server.task.constants.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TaskPoller implements Callable<Boolean>
{

	@Autowired
	private JobDeployer jobDeployer;

	@Override
	public Boolean call()
	{
		List<String> tskStatus = new ArrayList<String>();
		tskStatus.add( TaskStatus.Created.getStatus() );
		tskStatus.add( TaskStatus.ReScheduled.getStatus() );
		List<Task> tasks = HibernateSession.query( "from Task tsk where tsk.tskStatus in ( :tskStatus ) ", "tskStatus", tskStatus );
		for ( Task task : tasks )
		{
			Map<String, String> params = new HashMap<String, String>();
			JobInstance instance = task.getJitIdJobInstance();
			JobDefinition definition = instance.getJbdIdJobDefinition();
			List<JobTaskParamValue> paramsValues = instance.getJobTaskParamValues();
			for ( JobTaskParamValue eachParam : paramsValues )
			{
				params.put( eachParam.getJtpIdJobTaskParam().getJtpName(), eachParam.getJpvValue() != null ? eachParam.getJpvValue() : eachParam.getJtpIdJobTaskParam().getJtpDefault() );
			}
			jobDeployer.launchJob( definition.getJbdName(), params );
		}
		return true;
	}

	public static void main( String[] args )
	{
		@SuppressWarnings( "resource" )
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "task.xml" );
		ExecutorService executorService = Executors.newFixedThreadPool( 2 );
		executorService.submit( ( TaskPoller ) ctx.getBean( "taskPoller" ) );
	}

}
