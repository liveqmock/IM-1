package org.amaze.server.job.retry;

import java.util.ArrayList;
import java.util.List;

import org.amaze.db.hibernate.objects.JobStepTaskletChunk;
import org.amaze.db.hibernate.objects.JobStepTaskletChunkRetryableExceptionClasses;
import org.amaze.db.hibernate.objects.JobStepTaskletChunkSkippableExceptions;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

public class AmazeRetryPolicy extends SimpleRetryPolicy
{
	
	JobStepTaskletChunk jstChunk;
	
	List<Class> skippableIncludeExceptions = new ArrayList<Class>();
	List<Class> skippableExcludeExceptions = new ArrayList<Class>();
	
	public AmazeRetryPolicy( JobStepTaskletChunk jstChunk )
	{
		this.jstChunk = jstChunk;
		List<JobStepTaskletChunkRetryableExceptionClasses> exceptions = jstChunk.getJobStepTaskletChunkRetryableExceptionClassess();
		for( JobStepTaskletChunkRetryableExceptionClasses eachException : exceptions )
			if( eachException.getJcrIncludeExclude() )
				skippableIncludeExceptions.add( eachException.getJcrClass().getClass() );
			else
				skippableExcludeExceptions.add( eachException.getJcrClass().getClass() );
	}
	
	@Override
	public void registerThrowable( RetryContext context, Throwable throwable )
	{
		super.registerThrowable( context, throwable );
	}
	

}
