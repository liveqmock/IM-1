package org.amaze.server.job;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.core.step.job.JobStep;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        
        SimpleJob job = new SimpleJob();
        JobStep step = new JobStep();
        job.addStep( step );
//        TaskletStep taskletStep = new TaskletStep();
//        taskletStep.setTasklet( tsklet );
//        Chunk<String> chunk  = new Chunk<String>();
        Tasklet tasklet = new Tasklet()
		{
			
			@Override
			public RepeatStatus execute( StepContribution paramStepContribution, ChunkContext paramChunkContext ) throws Exception
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
        
        
    }
}
