package org.amaze.data.file.task;

import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.DataFSProperties;
import org.amaze.data.file.config.HDFSProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FSToHDFSTask extends AbstractTask
{

	private static final Logger logger = LogManager.getLogger( FSToHDFSTask.class );

	@Autowired
	private DataFSProperties fsProperties;

	@Autowired
	private HDFSProperties hdfsProperties;

	private String destPath;

	private String srcPath;

	public FSToHDFSTask( String srcPath, String destPath )
	{
		this.srcPath = srcPath;
		this.destPath = destPath;
	}

	public String getDestPath()
	{
		return destPath;
	}

	public void setDestPath( String destPath )
	{
		this.destPath = destPath;
	}

	public String getSrcPath()
	{
		return srcPath;
	}

	public void setSrcPath( String srcPath )
	{
		this.srcPath = srcPath;
	}

	//http://blog.rajeevsharma.in/2009/06/using-hdfs-in-java-0200.html

	// https://linuxjunkies.wordpress.com/2011/11/21/a-hdfsclient-for-hadoop-using-the-native-java-api-a-tutorial/
	@Override
	public TaskResult execute()
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
			fileSystem.copyFromLocalFile( src, dst );
			System.out.println( "File " + filename + "copied to " + dst );
			fileSystem.close();
		}
		catch ( Exception e )
		{
			System.err.println( "Exception caught! :" + e );
		}
		finally
		{
			
		}
		return null;
	}

}
