package org.amaze.db.schema;

public class Column implements Cloneable
{
    public String ColumnName;
    public int SequenceNumber;
    public AmazeType DataType;
    public int Length;
    public boolean IsMandatory;
    public boolean IsPrimaryKey;
    public boolean IsOneToOneNestedObject;
    public String NestedObject;
    public boolean isAuditEnabled;

    public transient Table table;

    public Column()
    {
    }

    public Column( String columnName, AmazeType dataType, boolean isMandatory, Table table )
    {
        ColumnName = columnName;
        DataType = dataType;
        IsMandatory = isMandatory;
        this.table = table;
    }


    public Column( String columnName, AmazeType dataType, int length, boolean isMandatory, Table table )
    {
        ColumnName = columnName;
        DataType = dataType;
        Length = length;
        IsMandatory = isMandatory;
        this.table = table;
    }

    public Column( String columnName, AmazeType dataType, boolean isMandatory, int orderNo, boolean primaryKey, Table table )
    {
        ColumnName = columnName;
        DataType = dataType;
        IsMandatory = isMandatory;
        Length = dataType.getLength();
        SequenceNumber = orderNo;
        IsPrimaryKey = primaryKey;
        this.table = table;
    }

    public Column( String columnName, AmazeType dataType, int length, boolean isMandatory, int orderNo, boolean primaryKey, Table table )
    {
        ColumnName = columnName;
        DataType = dataType;
        Length = length;
        IsMandatory = isMandatory;
        SequenceNumber = orderNo;
        IsPrimaryKey = primaryKey;
        this.table = table;
    }

    public Column( String columnName, AmazeType dataType, boolean isMandatory, Table table, boolean isAuditEnabled )
    {
        ColumnName = columnName;
        DataType = dataType;
        IsMandatory = isMandatory;
        this.isAuditEnabled = isAuditEnabled;
        this.table = table;
    }
	public Column clone() throws CloneNotSupportedException
    {
        Column clone = (Column)super.clone();
        clone.table = null;
        return clone;
    }

    public boolean equals( Object object )
    {
        if ( object == null )
            return false;
        if ( !( object instanceof Column ) )
            return false;
        Column fromColumn = (Column)object;
        if ( !ColumnName.equalsIgnoreCase( fromColumn.ColumnName ) )
            return false;
        if ( !DataType.equals( fromColumn.DataType ) )
        {
            if ( !( ( DataType == AmazeType.Decimal || DataType == AmazeType.Long ) && ( fromColumn.DataType == AmazeType.Decimal || fromColumn.DataType == AmazeType.Long ) ) )
            {
                return false;
            }
        }
        if ( DataType == AmazeType.String )
        {
            if ( Length != fromColumn.Length )
                return false;
        }
        if ( IsMandatory != fromColumn.IsMandatory )
            return false;
        return true;
    }
}
