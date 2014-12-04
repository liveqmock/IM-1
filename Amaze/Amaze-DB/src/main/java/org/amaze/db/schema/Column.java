package org.amaze.db.schema;

public class Column implements Cloneable
{
	public String columnName;
	public AmazeType dataType;
	public int length;
	public boolean isMandatory;
	public boolean isPrimaryKey;
	public String nestedObject;

	public transient Table table;

	public Column()
	{
	}

	public Column( String columnName, AmazeType dataType, Integer length, boolean isMandatory, boolean isPrimaryKey, String nestedObject, Table table )
	{
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.isMandatory = isMandatory;
		this.isPrimaryKey = isPrimaryKey;
		this.nestedObject = nestedObject;
		this.table = table;
	}

	public Column( String columnName, AmazeType dataType, int length, boolean isPrimaryKey, Table table )
	{
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.isPrimaryKey = isPrimaryKey;
		this.table = table;
	}
	
	

	public Column( String columnName, AmazeType dataType, boolean isPrimaryKey, Table table )
	{
		this.columnName = columnName;
		this.dataType = dataType;
		this.isPrimaryKey = isPrimaryKey;
		this.table = table;
	}

	public Column clone() throws CloneNotSupportedException
	{
		Column clone = ( Column ) super.clone();
		clone.table = null;
		return clone;
	}

	public boolean equals( Object object )
	{
		if ( object == null )
			return false;
		if ( !( object instanceof Column ) )
			return false;
		Column fromColumn = ( Column ) object;
		if ( !columnName.equalsIgnoreCase( fromColumn.columnName ) )
			return false;
		if ( !dataType.equals( fromColumn.dataType ) )
		{
			if ( !( ( dataType == AmazeType.Decimal || dataType == AmazeType.Long ) && ( fromColumn.dataType == AmazeType.Decimal || fromColumn.dataType == AmazeType.Long ) ) )
			{
				return false;
			}
		}
		if ( dataType == AmazeType.String )
		{
			if ( length != fromColumn.length )
				return false;
		}
		if ( isMandatory != fromColumn.isMandatory )
			return false;
		return true;
	}
}
