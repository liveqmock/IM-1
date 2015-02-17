package org.amaze.server.jobs.templates.mr;

import java.util.Set;
import java.util.Map.Entry;

import org.amaze.server.jobs.exception.AmazeJobExecutionException;
import org.amaze.server.jobs.templates.AbstractTasklet;
import org.amaze.server.jobs.templates.etl.Extract;
import org.amaze.server.jobs.templates.etl.Load;
import org.amaze.server.jobs.templates.etl.Transform;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HadoopMRJobTasklet extends AbstractTasklet
{

	private static final Logger logger = LogManager.getLogger( HadoopMRJobTasklet.class );

	private String mapper;

	private String reducer;

	private String combiner;

	@SuppressWarnings( "unchecked" )
	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		if ( jobParameters == null || jobParameters.isEmpty() )
		{
			throw new AmazeJobExecutionException( " No Hadoop processors parameters available for the Tasklet " );
		}
		final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();
		for ( Entry<String, JobParameter> jobParameterEntry : parameterEntries )
		{
			if ( jobParameterEntry.getKey().equals( "Mapper" ) )
				mapper = ( String ) jobParameterEntry.getValue().getValue();
			if ( jobParameterEntry.getKey().equals( "Reducer" ) )
				reducer = ( String ) jobParameterEntry.getValue().getValue();
			if ( jobParameterEntry.getKey().equals( "Combiner" ) )
				combiner = ( String ) jobParameterEntry.getValue().getValue();
			stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
		}
		if ( mapper == null || reducer == null )
		{
			logger.error( " Error building Hadoop Job - Mapper " + mapper + " Reducer " + reducer + " Combiner " + combiner + "" );
			throw new AmazeJobExecutionException( " Error building Hadoop Job - Mapper " + mapper + " Reducer " + reducer + " Combiner " + combiner + "" );
		}
		logger.debug( " Starting the Hadoop Job using the parameters " + " Mapper " + mapper + " Reducer " + reducer + " Combiner " + combiner + "" );
		Configuration configuration = new Configuration();
		configuration.set( "yarn.resourcemanager.address", "" );
		configuration.set( "mapreduce.framework.name", "" );
		configuration.set( "fs.default.name", "" );
		Job job = new Job( configuration );
		job.setMapperClass( ( Class< ? extends Mapper> ) Class.forName( mapper ).newInstance() );
		job.setReducerClass( ( Class< ? extends Reducer> ) Class.forName( reducer ).newInstance() );
//		job.setOutputKeyClass( Text.class );
//		job.setOutputValueClass( Text.class );
		// this is setting the format of your input, can be TextInputFormat
//		job.setInputFormatClass( SequenceFileInputFormat.class );
//		// same with output
//		job.setOutputFormatClass( TextOutputFormat.class );
//		// here you can set the path of your input
//		SequenceFileInputFormat.addInputPath( job, new Path( "files/toMap/" ) );
//		// this deletes possible output paths to prevent job failures
//		FileSystem fs = FileSystem.get( conf );
//		Path out = new Path( "files/out/processed/" );
//		fs.delete( out, true );
//		// finally set the empty out path
//		TextOutputFormat.setOutputPath( job, out );

		// this waits until the job completes and prints debug out to STDOUT or whatever
		// has been configured in your log4j properties.
		job.waitForCompletion( true );
		return null;
	}

}
