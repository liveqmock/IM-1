package org.amaze.server.plugins.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amaze.server.plugins.etl.exceptions.AmazeJobExecutionException;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * 
 * @author praneeth ramesh
 *
 * @param <T>
 * @param <V>
 */
public class ETLJobTasklet<T, V> implements Tasklet
{

	private static final Logger logger = Logger.getLogger( ETLJobTasklet.class );

	private String extract;

	private Extract<T> extractClass;

	private String transform;

	private Transform<T, V> transformClass;

	private String load;

	private Load<V> loadClass;

	public String getExtract()
	{
		return extract;
	}

	public void setExtract( String extract )
	{
		this.extract = extract;
	}

	public String getTransform()
	{
		return transform;
	}

	public void setTransform( String transform )
	{
		this.transform = transform;
	}

	public String getLoad()
	{
		return load;
	}

	public void setLoad( String load )
	{
		this.load = load;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		if ( extract == null || transform == null || load == null )
		{
			logger.error( " Error building ETL Job - Extract " + extract + " Transform " + transform + " Load " + load + "" );
			throw new AmazeJobExecutionException( " Error building ETL Job - Extract " + extract + " Transform " + transform + " Load " + load );
		}
		Map<String, Object> params = new HashMap<String, Object>();
		if ( jobParameters != null && !jobParameters.isEmpty() )
		{
			for ( Entry<String, JobParameter> jobParameterEntry : jobParameters.getParameters().entrySet() )
			{
				stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
				params.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
			}

		}
		extractClass = ( Extract<T> ) Class.forName( extract ).newInstance();
		extractClass.setJobParams( params );
		transformClass = ( Transform<T, V> ) Class.forName( transform ).newInstance();
		transformClass.setJobParams( params );
		loadClass = ( Load<V> ) Class.forName( load ).newInstance();
		loadClass.setJobParams( params );
		logger.debug( " Starting the ETL Job using the parameters " + " Extract " + extract + " Transform " + transform + " Load " + load );
		Boolean isErrored = false;
		try
		{
			Integer commitInterval = 10;
			List<V> processedValues = new ArrayList<V>();
			Integer i = 0;
			List<T> extractedData = extractClass.extract();
			for ( T eachData : extractedData )
			{
				if ( i < commitInterval )
				{
					processedValues.add( transformClass.transform( eachData ) );
					i++;
				}
				else
				{
					loadClass.load( processedValues );
					i = 0;
					processedValues = new ArrayList<V>();
					processedValues.add( transformClass.transform( eachData ) );
				}
			}
			loadClass.load( processedValues );
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
		return " ETLJob " + this + " - Extract " + extract + " - Transform " + transform + " - Load " + load;
	}
}
