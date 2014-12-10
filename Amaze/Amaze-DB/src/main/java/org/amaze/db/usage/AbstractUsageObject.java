package org.amaze.db.usage;

public class AbstractUsageObject
{

	private Integer id;

	public AbstractUsageObject()
	{

	}

	public AbstractUsageObject( Integer id )
	{
		this.id = id;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId( Integer id )
	{
		this.id = id;
	}

	public String toString()
	{
		return this.getClass().toString() + toString() + " ID : " + id;
	}

	public boolean equals( Object other )
	{
		if ( this == other )
			return true;
		if ( !this.getClass().equals( other.getClass() ) )
			return false;
		AbstractUsageObject anotherUsageObject = ( AbstractUsageObject ) other;
		return ( anotherUsageObject.id.equals( this.id ) );
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

}
