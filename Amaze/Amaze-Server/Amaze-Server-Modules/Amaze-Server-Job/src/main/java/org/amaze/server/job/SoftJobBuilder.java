package org.amaze.server.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;

//import org.amaze.db.hibernate.objects.JobListener;
//import org.amaze.db.hibernate.objects.JobStep;
//import org.amaze.db.hibernate.objects.JobStepChunkListener;
//import org.amaze.db.hibernate.objects.JobStepListener;
//import org.amaze.db.hibernate.objects.JobStepNext;
//import org.amaze.db.hibernate.objects.JobStepTasklet;
//import org.amaze.db.hibernate.objects.JobStepTaskletChunk;
//import org.amaze.db.hibernate.objects.JobStepTaskletChunkRetryableExceptionClasses;
//import org.amaze.db.hibernate.objects.JobStepTaskletChunkSkippableExceptions;
//import org.amaze.db.hibernate.objects.JobStepTaskletNoRollbackExceptions;
import org.amaze.server.job.exception.JobBuilderException;
import org.amaze.server.job.retry.AmazeRetryPolicy;
import org.amaze.server.job.skip.AmazeSkipPolicy;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.jsr.step.PartitionStep;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.resource.StepExecutionSimpleCompletionPolicy;
import org.springframework.batch.core.step.AbstractStep;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.PartitionStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

public class SoftJobBuilder implements ApplicationContextAware
{

	@Autowired
	JobRepository jobRepository;

	ApplicationContext ctx;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Override
	public void setApplicationContext( ApplicationContext ctx ) throws BeansException
	{
		this.ctx = ctx;
	}

//	@SuppressWarnings( "unchecked" )
//	public Job buildJob( org.amaze.db.hibernate.objects.Job job )
//	{
//		org.springframework.batch.core.job.builder.JobBuilder jobBuilder = jobBuilderFactory.get( job.getJobName() );
//		Boolean createOnLoad = job.getJobCreateOnLoad();
//		Boolean deployOnLoad = job.getJobDeployOnLoad();
//		String cron = job.getJobCron();
//		String delayTrigger = job.getJobFixedDelayTrig();
//		SimpleJobBuilder builder = new SimpleJobBuilder( null );
//		SimpleJob fJob = ( SimpleJob ) builder.build();
//		List<org.amaze.db.hibernate.objects.JobStep> steps = job.getJobSteps();
//		Map<JobStep, Step> jobStepsOrganized = new TreeMap<JobStep, Step>();
//		for ( org.amaze.db.hibernate.objects.JobStep eachStep : steps )
//		{
//			List<JobStepTasklet> tasklets = eachStep.getJobStepTasklets();
//			if ( tasklets.size() != 1 )
//				throw new JobBuilderException( "Single Tasklets Configurations are only supported in single Job Step by the platform. Refresh the configuration context" );
//			JobStepTasklet stepTasklet = tasklets.get( 0 );
//			TaskletStep jobStep = null;
//			if ( eachStep.getJspIsChuckProcessing() )
//			{
//				List<JobStepTaskletChunk> stepTaskletChunks = stepTasklet.getJobStepTaskletChunks();
//				if ( stepTaskletChunks.size() != 1 )
//					throw new JobBuilderException( "Single Chunk Configurations are only supported in single Job Step Tasklet by the platform." );
//				JobStepTaskletChunk jstChunk = stepTaskletChunks.get( 0 );				
//				FaultTolerantStepBuilder stepBuilder = stepBuilderFactory.get( eachStep.getJspName() )
//					.chunk( jstChunk.getJtcCommitIntreval() )
//					.reader( ( ItemReader< ? extends Object> ) ctx.getBean( jstChunk.getJtcReaderJobComponent().getJcmType() ) )
//					.processor( ( ItemProcessor<Object, Object> ) ctx.getBean( jstChunk.getJtcProcessorJobComponent().getJcmType() ) )
//					.writer( ( ItemWriter<Object> ) ctx.getBean( jstChunk.getJtcWriterJobComponent().getJcmType() ) )
//					.faultTolerant().skipLimit( jstChunk.getJtcSkipLimit() )
//					.skipPolicy( new AmazeSkipPolicy( jstChunk ) )
//					.retryLimit( jstChunk.getJtcRetryLimit() )
//					.retryPolicy( new AmazeRetryPolicy( jstChunk ) );
//				for( JobStepTaskletChunkRetryableExceptionClasses eachException : jstChunk.getJobStepTaskletChunkRetryableExceptionClassess() )
//				{
//					if( eachException.getJcrIncludeExclude() )
//						stepBuilder.retry( eachException.getJcrClass().getClass() );
//					else
//						stepBuilder.noRetry( eachException.getJcrClass().getClass() );
//				}
//				for( JobStepTaskletNoRollbackExceptions eachException : jstChunk.getJobStepTaskletNoRollbackExceptionss() )
//				{
//					if( eachException.getJtrIncludeExclude() )
//						stepBuilder.noRollback( eachException.getJtrClass().getClass() );
//				}
//				if( jstChunk.getJtcIsReaderTransactionalQueue() )
//					stepBuilder.readerIsTransactionalQueue();
//				stepBuilder.taskExecutor( (TaskExecutor) ctx.getBean( stepTasklet.getJstTaskExecutorJobComponent().getJcmType() ) );
//				jobStep = stepBuilder.build();
//				for ( JobStepChunkListener eachChunkListener : jstChunk.getJobStepChunkListeners() )
//					jobStep.registerChunkListener( ( ChunkListener ) ctx.getBean( eachChunkListener.getJlcTypeJobComponent().getJcmType() ) );
//			}
//			else
//			{
//				jobStep = new TaskletStep();
//				CallableTaskletAdapter adapter = new CallableTaskletAdapter();
//				adapter.setCallable( (Callable<RepeatStatus>) ctx.getBean( eachStep.getJspTaskExecutorJobComponent().getJcmType() ) );
//				jobStep.setTasklet( adapter );
//			}
//			jobStep.setName( eachStep.getJspName() );
//			jobStep.setStartLimit( stepTasklet.getJstStartLimit() );
//			jobStep.setAllowStartIfComplete( stepTasklet.getJstAllowStartIfComplete() );
//			jobStep.setJobRepository( jobRepository );
//			jobStep.setTransactionManager( ( PlatformTransactionManager ) ctx.getBean( stepTasklet.getJstTransactionManagerJobComponent().getJcmType() ) );
//			DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
//			attribute.setPropagationBehaviorName( stepTasklet.getJstTransactionAttributesPropagation() );
//			attribute.setIsolationLevelName( stepTasklet.getJstTransactionAttributesIsolation() );
//			attribute.setTimeout( Integer.valueOf( stepTasklet.getJstTransactionAttributesTimeout() ) );
//			jobStep.setTransactionAttribute( attribute );
//			jobStepsOrganized.put( eachStep, jobStep );
//			
//			
//			for ( JobStepListener eachStepListener : eachStep.getJobStepListeners() )
//			{
//				StepExecutionListener execListener = ( StepExecutionListener ) ctx.getBean( eachStepListener.getJslTypeJobComponent().getJcmType() );
//				( ( AbstractStep ) jobStepsOrganized.get( eachStep.getJspName() ) ).registerStepExecutionListener( execListener );
//			}
//		}
//		sortJobSteps(jobStepsOrganized.keySet());
//		
//		
//		
//		for( Map.Entry<JobStep, Step> eachEntry : jobStepsOrganized.entrySet() )
//		{
//
////			List<JobStepNext> nextStep = eachStep.getJobStepNexts();
////			if( nextStep.size() > 1 )
////				throw new JobBuilderException( "Only Single Step Flow is allowed for Step Next Builders.. Use split Builders for Multiple Job Steps" );
////			JobStepNext currentStepNext = nextStep.get( 0 );
////			FlowBuilder flowBuilder = new FlowBuilder( eachStep.getJspName() );
////			flowBuilder.on( currentStepNext.getJslOn() );
////			flowBuilder.next( jobStepsOrganized.get( currentStepNext.getJslNextJobStep().getJspName() ) );
//		}
//		
//		fJob.setName( job.getJobName() );
//		fJob.setRestartable( job.getJobRestartable() );
//		fJob.setJobRepository( jobRepository );
//
//		List<JobListener> listeners = job.getJobListeners();
//		for ( JobListener listener : listeners )
//		{
//			JobExecutionListener execListener = ( JobExecutionListener ) ctx.getBean( listener.getJltTypeJobComponent().getJcmType() );
//			fJob.registerJobExecutionListener( execListener );
//		}
//		return fJob;
//	}
//
//	private void sortJobSteps( Set<JobStep> keySet )
//	{
//		if( keySet == null)
//			return;
//		return;
//	}
}
