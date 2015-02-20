package org.amaze.data.file.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.DataFSProperties;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FSToFSTask extends AbstractTask
{

	private static final Logger logger = Logger.getLogger( FSToFSTask.class );

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
		logger.debug( "Created Task to move the file from File System to File System... " );
		super.init();
	}

	@Override
	public TaskResult execute()
	{
		logger.debug( "Params nos are changed " + params.size() + " params : " + params );
		logger.debug( " FSProperties " + fsProperties.getFsPath().toString() );
		for ( Map.Entry<String, Object> eachEntry : params.entrySet() )
		{
			logger.debug( "Param " + eachEntry.getKey() + " Value " + eachEntry.getValue() );
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
		if ( srcPath == null || destPath == null || fsProperties == null )
		{
			logger.error( "Invalid Task arguments... " + " Source Path " + srcPath + " Destination Path " + destPath + " FSPath " + fsProperties );
			throw new AmazeException( "Invalid Task arguments... " + " Source Path " + srcPath + " Destination Path " + destPath + " FSPath " + fsProperties );
		}
		File source = new File( fsProperties.getFsPath() + srcPath );
		File dest = new File( fsProperties.getFsPath() + destPath );
		try
		{
			if ( !folder )
				FileUtils.copyFile( source, dest );
			else
				FileUtils.copyDirectory( source, dest );
		}
		catch ( IOException e )
		{
			logger.error( "Invalid Task arguments... " + " Source Path " + srcPath + " Destination Path " + destPath + " FSPath " + fsProperties );
			throw new AmazeException( e );
		}
		return new TaskResult();
	}

}
