package org.amaze.db.schema;

public class Index implements Cloneable
{
	public String indexName;
	public boolean isUnique;
	public boolean isClustered;
	public boolean isBusinessConstraint;
	public String columnList;
	public String condition;

	public transient Table table;

	public Index()
	{
	}

	public Index( String indexName, boolean isUnique, boolean isClustered, boolean isBusinessConstraint, String columnList, String condition, Table table )
	{
		this.indexName = indexName;
		this.isUnique = isUnique;
		this.isClustered = isClustered;
		this.isBusinessConstraint = isBusinessConstraint;
		this.columnList = columnList;
		this.condition = condition;
		this.table = table;
	}

	public Index clone() throws CloneNotSupportedException
	{
		Index clone = ( Index ) super.clone();
		clone.table = null;
		return clone;
	}

	public void addColumn( String columnName )
	{
		if ( columnList == null || columnList.length() == 0 )
		{
			columnList = columnName;
			return;
		}
		columnList = columnList + "," + columnName;
	}

	public boolean equals( Object object )
	{
		if ( object == null )
			return false;
		if ( !( object instanceof Index ) )
			return false;
		Index fromIndex = ( Index ) object;
		if ( !indexName.equalsIgnoreCase( fromIndex.indexName ) )
			return false;
		if ( isUnique != fromIndex.isUnique )
			return false;
		if ( isClustered != fromIndex.isClustered )
			return false;
		if ( !columnList.equalsIgnoreCase( fromIndex.columnList ) )
			return false;
		if ( !condition.equalsIgnoreCase( fromIndex.condition ) )
			return false;
		return true;
	}
}
