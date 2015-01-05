package org.amaze.server.job.skip;

import java.util.ArrayList;
import java.util.List;

//import org.amaze.db.hibernate.objects.JobStepTaskletChunk;
//import org.amaze.db.hibernate.objects.JobStepTaskletChunkSkippableExceptions;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

@SuppressWarnings( "rawtypes" )
public class AmazeSkipPolicy /*implements SkipPolicy*/
{

//	JobStepTaskletChunk jstChunk;
//	
//	List<Class> skippableIncludeExceptions = new ArrayList<Class>();
//	List<Class> skippableExcludeExceptions = new ArrayList<Class>();
//	
//	public AmazeSkipPolicy( JobStepTaskletChunk jstChunk )
//	{
//		this.jstChunk = jstChunk;
//		List<JobStepTaskletChunkSkippableExceptions> exceptions = jstChunk.getJobStepTaskletChunkSkippableExceptionss();
//		for( JobStepTaskletChunkSkippableExceptions eachException : exceptions )
//			if( eachException.getJtcIncludeExclude() )
//				skippableIncludeExceptions.add( eachException.getJtcClass().getClass() );
//			else
//				skippableExcludeExceptions.add( eachException.getJtcClass().getClass() );
//	}
//	
//	@Override
//	public boolean shouldSkip( Throwable t, int skipCount ) throws SkipLimitExceededException
//	{
//		if( skippableIncludeExceptions.contains( t.getClass() ) )
//			return true;
//		else
//			if( !skippableExcludeExceptions.contains( t.getClass() ) )
//				return true;
//			else
//				return false;
//	}

}
