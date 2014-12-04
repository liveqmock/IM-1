package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.List;

import org.amaze.db.hibernate.objects.Tables;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.installer.exceptions.AmazeInstallerException;
import org.amaze.db.utils.DataSource;

public class Table implements Cloneable
{
	public String tableName;
	public String tablePrefix;
	public String displayName;

	public List<Column> columns = new ArrayList<Column>();
	public List<Index> indexes = new ArrayList<Index>();

	public transient Database database;

	public Table()
	{
	}

	public Table( String tableName )
	{
		this.tableName = tableName;
	}

	public Table( String tableName, String tablePrefix, String displayName )
	{
		this.tableName = tableName;
		this.tablePrefix = tablePrefix;
		this.displayName = displayName;
	}

	public Table clone() throws CloneNotSupportedException
	{
		Table clone = ( Table ) super.clone();
		clone.database = database;
		clone.columns = new ArrayList<Column>();
		for ( Column column : columns )
		{
			Column newColumn = ( Column ) column.clone();
			newColumn.table = clone;
			clone.columns.add( newColumn );
		}
		clone.indexes = new ArrayList<Index>();
		for ( Index index : indexes )
		{
			Index newIndex = ( Index ) index.clone();
			newIndex.table = clone;
			clone.indexes.add( newIndex );
		}
		return clone;
	}

	public Column findColumn( String columnName )
	{
		for ( Column column : columns )
		{
			if ( column.columnName.equalsIgnoreCase( columnName ) )
				return column;
		}
		return null;
	}

	public Index findIndex( String indexName )
	{
		for ( Index index : indexes )
		{
			if ( index.indexName.equalsIgnoreCase( indexName ) )
				return index;
		}
		return null;
	}

	public Index getClusteredIndex()
	{
		for ( Index index : indexes )
		{
			if ( index.isClustered )
				return index;
		}
		return null;
	}

	public List<String> getColumnNames()
	{
		List<String> columnNameList = new ArrayList<String>();
		for ( Column column : columns )
		{
			columnNameList.add( column.columnName );
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
		Table fromTable = ( Table ) object;
		if ( !fromTable.tableName.equalsIgnoreCase( tableName ) )
			return false;
		if ( columns.size() != fromTable.columns.size() )
			return false;
		for ( int i = 0; i < columns.size(); i++ )
		{
			Column column = columns.get( i );
			Column fromColumn = fromTable.columns.get( i );
			if ( !column.equals( fromColumn ) )
				return false;
		}
		for ( Column column : fromTable.columns )
		{
			Column toColumn = findColumn( column.columnName );
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
		Table fromTable = ( Table ) object;
		if ( !fromTable.tableName.equalsIgnoreCase( tableName ) )
			return false;
		for ( Index index : indexes )
		{
			Index fromIndex = fromTable.findIndex( index.indexName );
			if ( fromIndex == null )
				return false;
			if ( !index.equals( fromIndex ) )
				return false;
		}
		for ( Index index : fromTable.indexes )
		{
			Index toIndex = findIndex( index.indexName );
			if ( toIndex == null )
				return false;
		}
		return true;
	}

	public Column getPrimaryKeyColumn()
	{
		for ( Column column : columns )
		{
			if ( column.isPrimaryKey )
				return column;
		}
		return null;
	}

	public static Table loadTableFromDbTable( Database database, Tables tables )
	{
		Table table = new Table();
		table.tableName = tables.getTabName();
		table.tablePrefix = tables.getTabPrefix();
		table.displayName = tables.getTabDisplayName();
		table.database = database;
		List<Column> columnsList = new ArrayList<Column>();
		table.columns = columnsList;
		for ( org.amaze.db.hibernate.objects.Columns eachCol : tables.getColumnss() )
		{
			columnsList.add( new Column( eachCol.getColumnName(), AmazeType.valueOf( eachCol.getDataType() ), eachCol.getLenght(), eachCol.getIsMandatory(), eachCol.getIsPrimaryKey(), eachCol.getNestedObject(), table ) );
		}
		List<Index> indexesList = new ArrayList<Index>();
		table.indexes = indexesList;
		for ( org.amaze.db.hibernate.objects.Indexes eachIdx : tables.getIndexess() )
		{
//			indexesList.add( new Index( eachIdx.getIndexName(), eachIdx.getIsUnique(), eachIdx.getIsClustered(), eachIdx.getIsBusinessConstraint(), eachIdx.getColumnList(), eachIdx.getCondition(), table ) );
		}
		return table;
	}

	public static void UpdateDBTableFromSchemaTable( String database, Table eachTable )
	{
		List<Tables> tables = HibernateSession.find( "from Tables tab where tab.tabName='" + eachTable.tableName );
		if ( tables.size() == 1 )
		{

		}
		else
			throw new AmazeInstallerException( "Data Corrupted for the Table Dfn " + eachTable.tableName );
	}

	public static Table getTableFromTableName( DataSource dataSource, String tableName )
	{
		for ( Database eachDatabase : dataSource.getSchema().databases )
		{
			for ( Table eachTable : eachDatabase.tables )
			{
				if ( eachTable.tableName == tableName )
				{
					return eachTable;
				}
			}
		}
		return null;
	}
}
