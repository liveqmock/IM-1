package org.amaze.server.jobs.templates.file;

import org.amaze.server.jobs.templates.AbstractTasklet;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * 
 * @author 
 * Takes two Job Parameters Task Class( Fully qualified name) and the fil 
 */
public class FileTasklet extends AbstractTasklet
{
	
	@Override
	public RepeatStatus execute( StepContribution step, ChunkContext chunkContext ) throws Exception
	{
		return null;
	}

}
