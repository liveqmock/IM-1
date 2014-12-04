package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database implements Cloneable
{
	public String databaseName;

	public List<Table> tables = new ArrayList<Table>();

	public Schema schema;

	public Database()
	{
	}

	public Database( String databaseName )
	{
		this.databaseName = databaseName;
	}

	public Database( String databaseName, List<Table> tables )
	{
		this.databaseName = databaseName;
		this.tables = tables;
	}

	public Database( String databaseName, List<Table> tables, Schema schema )
	{
		this.databaseName = databaseName;
		this.tables = tables;
		this.schema = schema;
	}

	public Object clone() throws CloneNotSupportedException
	{
		Database database = new Database();
		for ( Table table : tables )
		{
			database.tables.add( table.clone() );
		}
		database.databaseName = databaseName;
		return database;
	}

	public Table findTable( String tablename )
	{
		for ( Table table : tables )
		{
			if ( table.tableName.equalsIgnoreCase( tablename ) )
			{
				return table;
			}
		}
		return null;
	}

	public Table removeTable( String tablename )
	{
		for ( Iterator<Table> it = tables.iterator(); it.hasNext(); )
		{
			Table table = it.next();
			if ( table.tableName.equalsIgnoreCase( tablename ) )
			{
				it.remove();
				return table;
			}
		}
		return null;
	}

}