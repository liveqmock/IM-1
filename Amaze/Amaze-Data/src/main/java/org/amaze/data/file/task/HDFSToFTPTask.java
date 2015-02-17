package org.amaze.data.file.task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.vfs.provider.ftp.FtpFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.ftp.FTPFileSystem;
import org.apache.hadoop.io.IOUtils;

public class HDFSToFTPTask extends AbstractTask
{

	@Override
	public Future<TaskResult> execute()
	{
		try
		{
			Configuration conf = new Configuration();
			FileSystem ftpfs = FTPFileSystem.get( conf );
			String dest = "test1.txt";
			ftpfs.initialize( new URI( "ftp://username:password@host" ), conf );
			FSDataOutputStream fsdout = ftpfs.create( new Path( dest ) );
			
			FileSystem fileSystem = FileSystem.get( conf );
			FSDataInputStream fsDin = fileSystem.open( new Path( "srcFile" ) );
			IOUtils.copyBytes( fsDin, fsdout, conf, true );
		}
		catch ( IOException | URISyntaxException e )
		{

		}
		return null;
	}

}
