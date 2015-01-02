package test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test
{

	public static void main( String[] args )
	{
		String[] springConfig = { "jobexample.xml" };
		ApplicationContext context = new ClassPathXmlApplicationContext( springConfig );
		JobLauncher jobLauncher = ( JobLauncher ) context.getBean( "jobLauncher" );
		Job job = ( Job ) context.getBean( "job" );
		try
		{
			Map<String, JobParameter> map = new HashMap<String, JobParameter>();
			map.put( "Test", new JobParameter( "TestParam" ) );
			JobExecution execution = jobLauncher.run( job, new JobParameters( map ) );
			System.out.println( "Exit Status : " + execution.getStatus() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		System.out.println( "Done" );

	}

}
