package org.amaze.data.file.task;

import java.io.IOException;
import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.config.FTPProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;

public class FTPToFTPTask extends AbstractTask
{

	@Autowired
	private FTPProperties ftpProperties;

	private String srcPath;

	private String destPath;
	
	public FTPToFTPTask( String srcPath, String destPath )
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

	@Override
	public Future<TaskResult> execute()
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
            
            ftpClient.rename( srcPath, destPath );
            Boolean success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File #2 has been downloaded successfully.");
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
