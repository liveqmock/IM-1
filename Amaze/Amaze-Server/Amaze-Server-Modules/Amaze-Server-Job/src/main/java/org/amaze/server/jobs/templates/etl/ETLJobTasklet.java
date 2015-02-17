package org.amaze.server.jobs.templates.etl;

import java.util.ArrayList;
import java.util.List;
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
 * 
 * @author praneeth ramesh
 *
 * @param <T>
 * @param <V>
 */
public class ETLJobTasklet<T, V> extends AbstractTasklet
{

	private static final Logger logger = LogManager.getLogger( ETLJobTasklet.class );

	private Extract<T> extract;

	private Transform<T, V> transform;

	private Load<V> load;

	public ETLJobTasklet( Extract<T> extract, Transform<T, V> transform, Load<V> load )
	{
		System.out.println( "Starting the ETL Job Context...." );
		this.extract = extract;
		this.transform = transform;
		this.load = load;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		if ( jobParameters == null || jobParameters.isEmpty() )
		{
			throw new AmazeJobExecutionException( " No ETL processors parameters available for the Tasklet " );
		}
		final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();
		for ( Entry<String, JobParameter> jobParameterEntry : parameterEntries )
		{
			if ( jobParameterEntry.getKey().equals( "Extract" ) )
				extract = ( Extract<T> ) Class.forName( ( String ) jobParameterEntry.getValue().getValue() ).newInstance();
			if ( jobParameterEntry.getKey().equals( "Transform" ) )
				transform = ( Transform<T, V> ) Class.forName( ( String ) jobParameterEntry.getValue().getValue() ).newInstance();
			if ( jobParameterEntry.getKey().equals( "Load" ) )
				load = ( Load<V> ) Class.forName( ( String ) jobParameterEntry.getValue().getValue() ).newInstance();
			stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
		}
		if ( extract == null || transform == null || load == null )
		{
			logger.error( " Error building ETL Job - Extract " + extract + " Transform " + transform + " Load " + load + "" );
			throw new AmazeJobExecutionException( " Error building ETL Job - Extract " + extract + " Transform " + transform + " Load " + load );
		}
		logger.debug( " Starting the ETL Job using the parameters " + " Extract " + extract + " Transform " + transform + " Load " + load );
		Boolean isErrored = false;
		try
		{
			Integer commitInterval = 10;
			List<V> processedValues = new ArrayList<V>();
			Integer i = 0;
			List<T> extractedData = extract.extract();
			for ( T eachData : extractedData )
			{
				if ( i < commitInterval )
				{
					processedValues.add( transform.transform( eachData ) );
					i++;
				}
				else
				{
					load.load( processedValues );
					i = 0;
					processedValues = new ArrayList<V>();
					processedValues.add( transform.transform( eachData ) );
				}
			}
			load.load( processedValues );
		}
		catch ( RuntimeException e )
		{
			isErrored = true;
			logger.error( " The ETL job failed... ", e );
			throw new AmazeJobExecutionException( e );
		}
		finally
		{
			logger.debug( " ETL Job completed " + ( isErrored ? " with errors.." : " successfully" ) );
		}
		return RepeatStatus.FINISHED;
	}

	@Override
	public String toString()
	{
		return " ETLJob " + this + " - Extract " + extract.getClass() + " - Transform " + transform.getClass() + " - Load " + load.getClass();
	}
}
