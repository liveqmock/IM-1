package org.amaze.data.file.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.DataFSProperties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FSToFSTask extends AbstractTask
{

	private static final Logger logger = LogManager.getLogger( FSToFSTask.class );

	@Autowired
	private DataFSProperties fsProperties;

	private String srcPath;

	private String destPath;

	private Boolean folder;

	public DataFSProperties getFsProperties()
	{
		return fsProperties;
	}

	public void setFsProperties( DataFSProperties fsProperties )
	{
		this.fsProperties = fsProperties;
	}

	@Override
	public void init()
	{
		logger.info( "Created Task to move the file from File System to FIle System... " );
		super.init();
	}

	@Override
	public Future<TaskResult> execute()
	{
		for ( Map.Entry<String, Object> eachEntry : params.entrySet() )
		{
			if ( eachEntry.getKey().equals( "srcPath" ) )
			{
				srcPath = ( String ) eachEntry.getValue();
			}
			else if ( eachEntry.getKey().equals( "destPath" ) )
			{
				destPath = ( String ) eachEntry.getValue();
			}
			else if ( eachEntry.getKey().equals( "folder" ) )
			{
				folder = Boolean.valueOf( ( String ) eachEntry.getValue() );
			}
		}
		if( srcPath == null || destPath == null )
		{
			logger.error( "Invalid Task arguments... " + " Source Path " + srcPath + " Destination Path " + destPath );
			throw new AmazeException( "Invalid Task arguments... " + " Source Path " + srcPath + " Destination Path " + destPath );
		}
		File source = new File( srcPath );
		File dest = new File( destPath );
		try
		{
			if( !folder )
				FileUtils.copyFile( source, dest );
			else
				FileUtils.copyDirectory( source, dest );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void main( String[] args )
	{
		System.out.println( "Starting" );
		System.out.println( System.currentTimeMillis() );
		//		new FSToFSTask( "D:\\Vids\\Single Page Applications with jQuery or Angular JS - YouTube[via torchbrowser.com].mp4", "d:\\Sample\\" ).execute();
		System.out.println( System.currentTimeMillis() );
		System.out.println( "Ending" );
	}

}
