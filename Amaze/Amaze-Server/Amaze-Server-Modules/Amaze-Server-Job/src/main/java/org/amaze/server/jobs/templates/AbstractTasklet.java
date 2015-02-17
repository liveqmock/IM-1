package org.amaze.server.jobs.templates;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class AbstractTasklet implements Tasklet
{

	public abstract RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception;	
	

}
