package org.amaze.data.file.task;

import java.io.IOException;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSToFSTask extends AbstractTask
{

	private String srcPath;

	private String destPath;

	@Override
	public Future<TaskResult> execute()
	{
		try
		{
			Configuration conf = new Configuration();
			FileSystem fileSystem = FileSystem.get( conf );
			Path src = new Path( srcPath );

			Path dst = new Path( destPath );
			// Check if the file already exists
			if ( !( fileSystem.exists( dst ) ) )
			{
				System.out.println( "No such destination " + dst );
				return null;
			}

			// Get the filename out of the file path
			String filename = srcPath.substring( srcPath.lastIndexOf( '/' ) + 1, srcPath.length() );

			try
			{
				fileSystem.copyToLocalFile( src, dst );
				System.out.println( "File " + filename + "copied to " + dst );
			}
			catch ( Exception e )
			{
				System.err.println( "Exception caught! :" + e );
				System.exit( 1 );
			}
			finally
			{
				fileSystem.close();
			}
		}
		catch ( IOException e )
		{

		}
		return null;
	}
}
