package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.List;

public class Table implements Cloneable
{
    public String TableName;
    public String TablePrefix;
    public String DisplayName;

    public List< Column > Columns = new ArrayList< Column >();
    public List< Index > Indexes = new ArrayList< Index >();

    public transient Database Database;

    public Table()
    {
    }

    public Table( String tableName )
    {
        TableName = tableName;
    }

    public Table clone() throws CloneNotSupportedException
    {
        Table clone = (Table)super.clone();
        clone.Database = Database;
        clone.Columns = new ArrayList< Column >();
        for ( Column column : Columns )
        {
            Column newColumn = (Column)column.clone();
            newColumn.table = clone;
            clone.Columns.add( newColumn );
        }
        clone.Indexes = new ArrayList< Index >();
        for ( Index index : Indexes )
        {
            Index newIndex = (Index)index.clone();
            newIndex.table = clone;
            clone.Indexes.add( newIndex );
        }
        return clone;
    }

    public Column findColumn( String columnName )
    {
        for ( Column column : Columns )
        {
            if ( column.ColumnName.equalsIgnoreCase( columnName ) )
                return column;
        }
        return null;
    }

    public Index findIndex( String indexName )
    {
        for ( Index index : Indexes )
        {
            if ( index.IndexName.equalsIgnoreCase( indexName ) )
                return index;
        }
        return null;
    }

    public Index getClusteredIndex()
    {
        for ( Index index : Indexes )
        {
            if ( index.IsClustered )
                return index;
        }
        return null;
    }

    public List< String > getColumnNames()
    {
        List< String > columnNameList = new ArrayList< String >();
        for ( Column column : Columns )
        {
            columnNameList.add( column.ColumnName );
        }
        return columnNameList;
    }

    public boolean equals( Object object )
    {
        if ( !isTableStructureEqual( object ) )
            return false;
        if ( !areIndexesEqual( object ) )
            return false;
        return true;
    }

    public boolean isTableStructureEqual( Object object )
    {
        if ( object == null )
            return false;
        if ( !( object instanceof Table ) )
            return false;
        Table fromTable = (Table)object;
        if ( !fromTable.TableName.equalsIgnoreCase( TableName ) )
            return false;
        if ( Columns.size() != fromTable.Columns.size() )
            return false;
        for ( int i = 0; i < Columns.size(); i++ )
        {
            Column column = Columns.get( i );
            Column fromColumn = fromTable.Columns.get( i );
            if ( !column.equals( fromColumn ) )
                return false;
        }
        for ( Column column : fromTable.Columns )
        {
            Column toColumn = findColumn( column.ColumnName );
            if ( toColumn == null )
                return false;
        }
        return true;
    }

    public boolean areIndexesEqual( Object object )
    {
        if ( object == null )
            return false;
        if ( !( object instanceof Table ) )
            return false;
        Table fromTable = (Table)object;
        if ( !fromTable.TableName.equalsIgnoreCase( TableName ) )
            return false;
        for ( Index index : Indexes )
        {
            Index fromIndex = fromTable.findIndex( index.IndexName );
            if ( fromIndex == null )
                return false;
            if ( !index.equals( fromIndex ) )
                return false;
        }
        for ( Index index : fromTable.Indexes )
        {
            Index toIndex = findIndex( index.IndexName );
            if ( toIndex == null )
                return false;
        }
        return true;
    }

    public Column getPrimaryKeyColumn()
    {
        for ( Column column : Columns )
        {
            if ( column.IsPrimaryKey )
                return column;
        }
        return null;
    }

    public void setFieldAuditing( boolean fieldAuditing )
    {
        if ( fieldAuditing )
        {
            if ( findColumn( "modified_dttm" ) == null )
            {
                Columns.add( new Column( "modified_dttm", AmazeType.DateTime, true, this, true ) );
                Columns.add( new Column( "created_dttm", AmazeType.DateTime, true, this, true ) );
                Columns.add( new Column( "modified_usr_id", AmazeType.Int, true, this, true ) );
                Columns.add( new Column( "created_usr_id", AmazeType.Int, true, this, true ) );
            }
        }
        else
        {
            Column col = findColumn( "modified_dttm" );
            if ( col != null )
            {
                Columns.remove( col );
                Columns.remove( findColumn( "created_dttm" ) );
                Columns.remove( findColumn( "modified_usr_id" ) );
                Columns.remove( findColumn( "created_usr_id" ) );
            }
        }
    }
}
