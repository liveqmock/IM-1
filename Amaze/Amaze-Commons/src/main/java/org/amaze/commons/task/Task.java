package org.amaze.commons.task;

import java.util.Map;
import java.util.concurrent.Future;

public interface Task
{

	public void init();

	public Future<TaskResult> execute();

	public void destroy();

	public void setJobParams( Map<String, Object> params );
}
