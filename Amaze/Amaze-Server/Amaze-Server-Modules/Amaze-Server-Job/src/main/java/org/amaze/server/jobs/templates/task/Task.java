package org.amaze.server.jobs.templates.task;

import java.io.Serializable;

/**
 * This is the Mark Up interface for the Task that can be plugged in as the Task implementations for the TaskJob
 */
public interface Task extends Serializable
{
	
	public void execute();

}
