package org.amaze.db.hibernate;

public abstract class AbstractHibernateObject
{
    private int id = 0;
    private boolean deleteFl = false;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Boolean getDeleteFl()
    {
        return deleteFl;
    }

    public void setDeleteFl( Boolean deleteFl )
    {
        this.deleteFl = deleteFl;
    }

    public AbstractHibernateObject()
    {
    }

    public AbstractHibernateObject( Integer id, boolean deleteFl )
    {
        this.id = id;
        this.deleteFl = deleteFl;
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
