package org.amaze.db.schema;

public class Index implements Cloneable
{
    public String IndexName;
    public boolean IsUnique;
    public boolean IsClustered;
    public boolean IsBusinessConstraint;
    public boolean IsDisplayName;
    public String ColumnList;

    public transient Table table;

    public Index()
    {
    }

    public Index( String indexName, boolean isUnique, boolean isClustered, boolean isBusinessConstraint, boolean isDisplayName, String columnList, Table table )
    {
        IndexName = indexName;
        IsUnique = isUnique;
        IsClustered = isClustered;
        IsBusinessConstraint = isBusinessConstraint;
        IsDisplayName = isDisplayName;
        ColumnList = columnList;
        this.table = table;
    }

    public Index clone() throws CloneNotSupportedException
    {
        Index clone = (Index)super.clone();
        clone.table = null;
        return clone;
    }

    public void addColumn( String columnName )
    {
        if ( ColumnList == null || ColumnList.length() == 0 )
        {
            ColumnList = columnName;
            return;
        }
        ColumnList = ColumnList + "," + columnName;
    }

    public boolean equals( Object object )
    {
        if ( object == null )
            return false;
        if ( !( object instanceof Index ) )
            return false;
        Index fromIndex = (Index)object;
        if ( !IndexName.equalsIgnoreCase( fromIndex.IndexName ) )
            return false;
        if ( IsUnique != fromIndex.IsUnique )
            return false;
        if ( IsClustered != fromIndex.IsClustered )
            return false;
        if ( IsDisplayName != fromIndex.IsDisplayName )
            return false;
        if ( !ColumnList.equalsIgnoreCase( fromIndex.ColumnList ) )
            return false;
        return true;
    }
}
