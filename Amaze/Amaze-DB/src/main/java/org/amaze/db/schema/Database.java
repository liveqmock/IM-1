package org.amaze.db.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database implements Cloneable
{
    public String DatabaseName;

    public List< Table > Tables = new ArrayList< Table >();
    
    public Schema Schema;

    public Database()
    {
    }

    public Database( String databaseName )
    {
        DatabaseName = databaseName;
    }

    public Database( String databaseName, List< Table > tables )
    {
        DatabaseName = databaseName;
        Tables = tables;
    }

    public Object clone() throws CloneNotSupportedException
    {
        Database database = new Database();
        for ( Table table : Tables )
        {
            database.Tables.add( table.clone() );
        }
        database.DatabaseName = DatabaseName;
        return database;
    }

    public Table findTable( String tablename )
    {
        for ( Table table : Tables )
        {
            if ( table.TableName.equalsIgnoreCase( tablename ) )
            {
                return table;
            }
        }
        return null;
    }

    public Table removeTable( String tablename )
    {
        for ( Iterator< Table > it = Tables.iterator(); it.hasNext(); )
        {
            Table table = it.next();
            if ( table.TableName.equalsIgnoreCase( tablename ) )
            {
                it.remove();
                return table;
            }
        }
        return null;
    }
    
}
