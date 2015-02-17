package org.amaze.data.file.config;

import org.springframework.beans.factory.annotation.Autowired;

public class DataProperties
{
	@Autowired
	private DataFSProperties fsProperties;
	
	@Autowired
	private S3Properties s3Properties;
	
	@Autowired
	private HDFSProperties hdfsProperties;

	@Autowired
	private FTPProperties ftpProperties;

	public DataFSProperties getFsProperties()
	{
		return fsProperties;
	}

	public void setFsProperties( DataFSProperties fsProperties )
	{
		this.fsProperties = fsProperties;
	}

	public S3Properties getS3Properties()
	{
		return s3Properties;
	}

	public void setS3Properties( S3Properties s3Properties )
	{
		this.s3Properties = s3Properties;
	}

	public HDFSProperties getHdfsProperties()
	{
		return hdfsProperties;
	}

	public void setHdfsProperties( HDFSProperties hdfsProperties )
	{
		this.hdfsProperties = hdfsProperties;
	}

	public FTPProperties getFtpProperties()
	{
		return ftpProperties;
	}

	public void setFtpProperties( FTPProperties ftpProperties )
	{
		this.ftpProperties = ftpProperties;
	}

}
