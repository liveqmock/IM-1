package org.amaze.server.jobs.templates.mahout;

import org.amaze.server.jobs.templates.AbstractTasklet;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class MahoutJobTasklet extends AbstractTasklet
{

	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		return null;
	}
	
}
