package org.amaze.data.file.task;

import java.util.concurrent.Future;

import org.amaze.commons.task.TaskResult;
import org.amaze.data.file.exception.NotImplementedException;

public class HDFSToS3Task extends AbstractTask
{

	@Override
	public Future<TaskResult> execute()
	{
		throw new NotImplementedException(" S3 Not implemented");
	}

}
