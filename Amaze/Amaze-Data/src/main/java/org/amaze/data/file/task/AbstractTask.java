package org.amaze.data.file.task;

import java.util.Map;

import org.amaze.commons.task.Task;

public abstract class AbstractTask implements Task
{

	protected Map<String, Object> params;

	@Override
	public void setJobParams( Map<String, Object> params )
	{
		this.params = params;
	}

	@Override
	public void init()
	{

	}

	@Override
	public void destroy()
	{

	}

}
