package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
* Sample tasklet.
*
* @author Gunnar Hillert
* @author Ilayaperumal Gopinathan
*/
public class HelloSpringXDTasklet implements Tasklet, StepExecutionListener
{
	private volatile AtomicInteger counter = new AtomicInteger( 0 );

	/**
	*
	*/
	public HelloSpringXDTasklet()
	{
		super();
	}

	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext ) throws Exception
	{
		final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
		final ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
		System.out.println( "Hello Spring XD!" );
		if ( jobParameters != null && !jobParameters.isEmpty() )
		{
			final Set<Entry<String, JobParameter>> parameterEntries = jobParameters.getParameters().entrySet();
			System.out.println( String.format( "The following %s Job Parameter(s) is/are present:", parameterEntries.size() ) );
			for ( Entry<String, JobParameter> jobParameterEntry : parameterEntries )
			{
				System.out.println( String.format( "Parameter name: %s; isIdentifying: %s; type: %s; value: %s", jobParameterEntry.getKey(), jobParameterEntry.getValue().isIdentifying(), jobParameterEntry.getValue().getType().toString(), jobParameterEntry.getValue().getValue() ) );
				if ( jobParameterEntry.getKey().startsWith( "context" ) )
				{
					stepExecutionContext.put( jobParameterEntry.getKey(), jobParameterEntry.getValue().getValue() );
				}
			}
			if ( jobParameters.getString( "throwError" ) != null && Boolean.TRUE.toString().equalsIgnoreCase( jobParameters.getString( "throwError" ) ) )
			{
				if ( this.counter.compareAndSet( 3, 0 ) )
				{
					System.out.println( "Counter reset to 0. Execution will succeed." );
				}
				else
				{
					this.counter.incrementAndGet();
					throw new IllegalStateException( "Exception triggered by user." );
				}
			}
		}
		int i = 1;
		if( i == 1 )
			throw new IllegalStateException();
		File f = new File( "D:\\Abc.txt" );
		BufferedWriter w = new BufferedWriter( new FileWriter( f ) );
		while( i == 1 )
		{
			System.out.println( "Hello Spring XD!" );
			w.write( "Test data " + jobParameters + "    " + new Date() + "\n" );
		}
			
		return RepeatStatus.FINISHED;
	}

	@Override
	public void beforeStep( StepExecution stepExecution )
	{
	}

	@Override
	public ExitStatus afterStep( StepExecution stepExecution )
	{
		// To make the job execution fail, set the step execution to fail
		// and return failed ExitStatus
		// stepExecution.setStatus(BatchStatus.FAILED);
		// return ExitStatus.FAILED;
		return ExitStatus.COMPLETED;
	}
}
