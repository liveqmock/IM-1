package org.amaze.server.jobs.templates.task;

import java.util.Map.Entry;
import java.util.Set;

import org.amaze.server.jobs.exception.AmazeJobExecutionException;
import org.amaze.server.jobs.templates.AbstractTasklet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Takes One JOb parameter, Task Class.
 * Data read/write handling is internal to the task 
 *
 */
public class TaskJobTasklet extends AbstractTasklet
{

	private static final Logger logger = LogManager.getLogger( TaskJobTasklet.class );

	private Task task;
	
	public TaskJobTasklet( Task task )
	{
		this.task = task;
	}

	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		if ( jobParameters == null || jobParameters.isEmpty() )
		{
			throw new AmazeJobExecutionException( " No TaskJob processors parameters available for the Tasklet " );
		}
		final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();
		for ( Entry<String, JobParameter> jobParameterEntry : parameterEntries )
		{
			if ( jobParameterEntry.getKey().equals( "Task" ) )
				task = ( Task ) Class.forName( ( String ) jobParameterEntry.getValue().getValue() ).newInstance();
			stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
		}
		if ( task == null )
		{
			logger.error( " Error building Task Job " + this );
			throw new AmazeJobExecutionException( " Error building Task Job " + this );
		}
		logger.debug( " Starting the TaskJob using the Task parameters " );
		Boolean isErrored = false;
		try
		{
			task.execute();
		}
		catch ( RuntimeException e )
		{
			isErrored = true;
			logger.error( " The Task job failed... ", e );
			throw new AmazeJobExecutionException( e );
		}
		finally
		{
			logger.debug( " Task Job completed " + ( isErrored ? " with errors.." : " successfully" ) );
		}
		return RepeatStatus.FINISHED;
	}

	@Override
	public String toString()
	{
		return " TaskJob " + this + " - Task " + task.getClass() + " " + task;
	}

}
