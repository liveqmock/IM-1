package org.amaze.server.jobs.templates.sqoop;

import org.amaze.server.jobs.templates.AbstractTasklet;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class SqoopJobTasklet extends AbstractTasklet
{
	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		return null;
	}

}
