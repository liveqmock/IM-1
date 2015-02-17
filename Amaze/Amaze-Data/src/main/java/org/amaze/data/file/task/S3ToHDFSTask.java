package org.amaze.data.file.task;

import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.exception.NotImplementedException;

public class S3ToHDFSTask extends AbstractTask
{

	@Override
	public Future<TaskResult> execute()
	{
		throw new NotImplementedException( "S3 Stcak not implemented" );
	}
	
}
