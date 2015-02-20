package org.amaze.commons.task;

import java.util.Map;

public interface Task
{

	public void init();

	public TaskResult execute();

	public void destroy();

	public void setJobParams( Map<String, Object> params );
}
