package org.amaze.server.plugins.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.task.Task;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class SimpleTasklet implements Tasklet
{

	private static final Logger logger = Logger.getLogger(SimpleTasklet.class);
	
	private String className;

	private Task task;

	public String getClassName()
	{
		return className;
	}

	public void setClassName( String className )
	{
		this.className = className;
	}

	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		logger.error("Starting the Task SimpleTasklet...");
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		final Map<String, Object> params = new HashMap<String, Object>();
		if( className != null )
		{
			task = ( Task ) Class.forName( className ).newInstance();
		}
		if ( jobParameters != null && !jobParameters.isEmpty() )
		{
			final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();
			for ( Entry<String, JobParameter> jobParameterEntry : parameterEntries )
			{
				if ( jobParameterEntry.getKey().startsWith( "context" ) )
				{
					stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
				}
				params.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
			}
			if ( task != null )
			{
				task.setJobParams( params );
				task.execute();
				return RepeatStatus.FINISHED;
			}
			logger.error( "Invalid or no arguments passed to the Job.." );
			throw new AmazeException( "Invalid or no arguments passed to the Job.." );
		}
		else
		{
			logger.error( " No Job Parameters configured.. Could not create the Job" );
			throw new AmazeException( " No Job Parameters configured.. Could not create the Job" );
		}
	}

}
