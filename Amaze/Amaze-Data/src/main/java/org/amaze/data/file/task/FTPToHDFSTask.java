package org.amaze.data.file.task;

import java.io.IOException;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.FTPProperties;
import org.amaze.data.file.config.HDFSProperties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.ftp.FTPFileSystem;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;

import java.net.URI;

import java.net.URISyntaxException;

import org.apache.hadoop.fs.FSDataInputStream;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IOUtils;

public class FTPToHDFSTask extends AbstractTask
{

	@Autowired
	private FTPProperties ftpPropeties;

	private HDFSProperties hdfsProperties;

	private String srcPath;

	private String destPath;

	public FTPToHDFSTask( String srcPath, String destPath )
	{
		this.srcPath = srcPath;
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

	public String getDestPath()
	{
		return destPath;
	}

	public void setDestPath( String destPath )
	{
		this.destPath = destPath;
	}

	//http://tutorials.techmytalk.com/2014/08/16/hadoop-hdfs-java-api/
	
	//http://hadoop101.blogspot.in/2011/12/ftp-to-hdfs.html
	@Override
	public TaskResult execute()
	{
		try
		{
			Configuration conf = new Configuration();
			FileSystem ftpfs = FTPFileSystem.get( conf );
			String src = "test1.txt";
			ftpfs.initialize( new URI( "ftp://username:password@host" ), conf );
			FSDataInputStream fsdin = ftpfs.open( new Path( src ), 1000 );
			FileSystem fileSystem = FileSystem.get( conf );
			OutputStream outputStream = fileSystem.create( new Path( destPath ) );
			IOUtils.copyBytes( fsdin, outputStream, conf, true );
		}
		catch ( IOException | URISyntaxException e )
		{

		}
		return null;
	}

}
