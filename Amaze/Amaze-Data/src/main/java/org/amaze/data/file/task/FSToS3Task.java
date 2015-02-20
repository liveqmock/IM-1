package org.amaze.data.file.task;

import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.DataFSProperties;
import org.amaze.data.file.config.S3Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;

public class FSToS3Task extends AbstractTask
{

	private static final Logger logger = LogManager.getLogger( FSToS3Task.class );

	@Autowired
	private DataFSProperties fsProperties;

	@Autowired
	private S3Properties s3Properties;

	private String srcPath;

	private String destBucket;

	private String file;

	public FSToS3Task( String srcPath, String destBucket, String file )
	{
		this.srcPath = srcPath;
		this.destBucket = destBucket;
		this.file = file;
	}
	
	public String getSrcPath()
	{
		return srcPath;
	}

	public void setSrcPath( String srcPath )
	{
		this.srcPath = srcPath;
	}

	public String getDestBucket()
	{
		return destBucket;
	}

	public void setDestBucket( String destBucket )
	{
		this.destBucket = destBucket;
	}

	public String getFile()
	{
		return file;
	}

	public void setFile( String file )
	{
		this.file = file;
	}

	// http://ceph.com/docs/master/radosgw/s3/java/
	// http://docs.aws.amazon.com/AmazonS3/latest/dev/UploadObjSingleOpJava.html
	@Override
	public TaskResult execute()
	{
		AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider ());
        try {
        	
        	String bucketName     = "*** Provide bucket name ***";
        	String key            = "*** Provide key ***  ";
        	String destinationKey = "*** Provide dest. key ***";
        	
            // Copying object
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
            		bucketName, key, bucketName, destinationKey);
            System.out.println("Copying object.");
            s3client.copyObject(copyObjRequest);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " + 
            		"to Amazon S3, but was rejected with an error " +
                    "response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to " +
                    " communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return null;
	}

}
