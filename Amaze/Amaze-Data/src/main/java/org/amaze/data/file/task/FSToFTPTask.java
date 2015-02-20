package org.amaze.data.file.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.DataFSProperties;
import org.amaze.data.file.config.FTPProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class FSToFTPTask extends AbstractTask
{

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private DataFSProperties fsProperties;

	@Autowired
	private FTPProperties ftpProperties;

	private String srcPath;

	private String destPath;
	
	public FSToFTPTask( String srcPath, String destPath )
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

	// http://www.codejava.net/java-se/networking/ftp/java-ftp-file-upload-tutorial-and-example
	@Override
	public TaskResult execute()
	{
		String server = "www.myserver.com";
        int port = 21;
        String user = "user";
        String pass = "pass";
 
        FTPClient ftpClient = new FTPClient();
        try {
 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
 
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
 
            // APPROACH #1: uploads first file using an InputStream
            File firstLocalFile = new File("D:/Test/Projects.zip");
 
            String firstRemoteFile = "Projects.zip";
            InputStream inputStream = new FileInputStream(firstLocalFile);
 
            System.out.println("Start uploading first file");
            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("The first file is uploaded successfully.");
            }
 
            // APPROACH #2: uploads second file using an OutputStream
            File secondLocalFile = new File("E:/Test/Report.doc");
            String secondRemoteFile = "test/Report.doc";
            inputStream = new FileInputStream(secondLocalFile);
 
            System.out.println("Start uploading second file");
            OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
            byte[] bytesIn = new byte[4096];
            int read = 0;
 
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();
 
            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                System.out.println("The second file is uploaded successfully.");
            }
 
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
	}

}
