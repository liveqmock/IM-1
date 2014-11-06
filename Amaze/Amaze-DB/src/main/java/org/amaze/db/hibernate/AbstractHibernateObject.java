package org.amaze.db.hibernate;

public abstract class AbstractHibernateObject
{
    private int id = 0;
    private int versionId = 1;
    private boolean deleteFl = false;
    private int partitionId = 1;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getVersionId()
    {
        return versionId;
    }

    public void setVersionId( int versionId )
    {
        this.versionId = versionId;
    }

    public Boolean getDeleteFl()
    {
        return deleteFl;
    }

    public void setDeleteFl( Boolean deleteFl )
    {
        this.deleteFl = deleteFl;
    }

    public int getPartitionId()
    {
        return partitionId;
    }

    public void setPartitionId( int partitionId )
    {
        this.partitionId = partitionId;
    }

    public AbstractHibernateObject()
    {
    }

    public AbstractHibernateObject( Integer id, int versionId, boolean deleteFl, int partitionId )
    {
        this.id = id;
        this.versionId = versionId;
        this.deleteFl = deleteFl;
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
        AbstractHibernateObject otherHibernateObject = (AbstractHibernateObject)other;
        return ( this.id == otherHibernateObject.getId() );
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
