package org.amaze.server.task.constants;

public enum TaskStatus
{

	Created( "Created" ), Started( "Started" ), Completed( "Completed" ), Failed( "Failed" ), ReScheduled ( "ReScheduled" );

	private TaskStatus( String status )
	{
		this.status = status;
	}

	String status;

	public String getStatus()
	{
		return status;
	}

	public void setStatus( String status )
	{
		this.status = status;
	}
	
	@Override
	public String toString()
	{
		return this.status;
	}

}
