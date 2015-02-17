package org.amaze.data.file.task;

import java.io.IOException;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSToHDFSTask extends AbstractTask
{

	@Override
	public Future<TaskResult> execute()
	{
		try
		{
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get( conf );
			Boolean result = fs.rename( new Path( "srcPath" ), new Path( "destPath" ) );
		}
		catch ( IOException e )
		{
			
		}
		finally
		{

		}
		return null;
	}

}
