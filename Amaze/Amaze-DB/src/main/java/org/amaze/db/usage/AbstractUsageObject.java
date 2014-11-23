package org.amaze.db.usage;

public class AbstractUsageObject
{
	private int id = 0;
	private int partitionId = 1;

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getPartitionId()
	{
		return partitionId;
	}

	public void setPartitionId( int partitionId )
	{
		this.partitionId = partitionId;
	}

	public AbstractUsageObject()
	{
		
	}

	public AbstractUsageObject( Integer id, int partitionId )
	{
		this.id = id;
		this.partitionId = partitionId;
	}

	public String toString()
	{
		return this.getClass().toString() + " " + id;
	}

	public boolean equals( Object other )
	{
		if ( this == other )
			return true;
		if ( !this.getClass().equals( other.getClass() ) )
			return false;
		AbstractUsageObject otherUsageObject = ( AbstractUsageObject ) other;
		return ( this.id == otherUsageObject.getId() );
	}

	public int hashCode()
	{
		return id;
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

}
