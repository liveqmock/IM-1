package org.amaze.db.utils;

import java.sql.Connection;
import java.util.List;

import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.hibernate.SessionFactory;

public class MySQLDataSource extends AbstractDataSource
{

	public MySQLDataSource( String dataBase, String dataSourceName, SessionFactory sessionFactory, javax.sql.DataSource dataSource, Connection connection )
	{
		super( dataBase, "MySQL", dataSourceName, sessionFactory, dataSource, connection );
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void truncateTable( String database, String tableName ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyIndexes( String indexLocation ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getTableQuery( String database, String tablePattern, boolean exact )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDefaultConstant( Column column ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String amazeTypeToDbType( AmazeType amazeType, int length ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getCreateIndexStatement( Index index, String indexLocation )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSelectDatabaseDDL( String databaseName ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getMigrationName( Table table ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionName( String functionName ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSqlConvert( AmazeType to, int length, Column from ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<String> getDropIndexDDL( Index index, boolean checkExists ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dropStoredProcedure( String procName ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyStoredProcedure( String procName, String procedure ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String[]> executeStoredProcedure( String procName, List args ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IdGenerator getIdGenerator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, long min, long max )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSqlToFetchNRows( String tableName, long maxRows )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOrderByClause( List<Object[]> colNames, AmazeType[] resultTypes ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
//	@Override
//	protected String getTableQuery( String database, String tableName, boolean exact )
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append( " select  tab.TABLE_NAME " );
//		sb.append( " from      information_schema.tables         tab " );
//		sb.append( " where     tab.table_schema            = '" + database.toUpperCase() + "' " );
//		if ( tableName != null && tableName.length() > 0 )
//		{
//			if ( exact )
//			{
//				sb.append( " and     tab.table_name      = '" + tableName.toUpperCase() + "' " );
//			}
//			else
//			{
//				sb.append( " and     tab.table_name     like '" + tableName.toUpperCase() + "' " );
//			}
//		}
//		sb.append( " order by tab.table_name" );
//		return sb.toString();
//	}
//	
//	@Override
//	public void truncateTable( String database, String tableName ) throws DataSourceException
//	{
//		String query = "truncate table " + tableName;
//		execute( query );
//	}
//	
//	@Override
//	protected List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException
//	{
//		
////		CREATE TABLE tutorials_tbl(
////		   -> tutorial_id INT NOT NULL AUTO_INCREMENT,
////		   -> tutorial_title VARCHAR(100) NOT NULL,
////		   -> tutorial_author VARCHAR(40) NOT NULL,
////		   -> submission_date DATE,
////		   -> PRIMARY KEY ( tutorial_id )
//		List<String> sqlList = new ArrayList<String>();
//		String statement;
//		statement = "create table " + table.TableName + StringUtils.NEW_LINE + "(";
//		for ( Column col : table.Columns )
//		{
//			String mandatory = " default " + getDefaultConstant( col ) + " not null," + ( col.IsMandatory ? "not null," : "null    ," );
//			statement += StringUtils.NEW_LINE + "    " + col.ColumnName + " " + amazeTypeToDbType( col.DataType, col.Length ) + " " + mandatory;
//		}
//		statement = statement.substring( 0, statement.length() - 1 ) + StringUtils.NEW_LINE + ")" + StringUtils.NEW_LINE;
//		if ( dataLocation != null && dataLocation.length() > 0 )
//			statement += " tablespace " + dataLocation;
//		sqlList.add( statement );
//		return sqlList;
//	}
//	
//	@Override
//	protected String getDefaultConstant( Column column )
//	{
//		switch( column.DataType )
//		{
//		case Bool:
//			return ( "'N'" );
//		case DateTime:
//			return "to_date('01-JAN-2000')";
//		case Int:
//		case Long:
//		case Decimal:
//			return "0";
//		case String:
//			return ( "''" );
//		default:
//			throw new IllegalArgumentException( "Unknown 'ConstantType': '" + column.DataType + "'" );
//		}
//	}
//
//	@Override
//	protected String amazeTypeToDbType( AmazeType amazeType, int length ) throws DataSourceException
//	{
//		switch( amazeType )
//		{
//		case Bool:
//			return "char";
//		case DateTime:
//			return "timestamp";
//		case Int:
//			return "number(10)";
//		case Long:
//		case Decimal:
//			return "number(19)";
//		case String:
//			return ( "varchar2(" + length + ")" );
//		case Text:
//			throw new DataSourceException( " AmazeType: Text is not supported for Oracle" );
//		default:
//			throw new DataSourceException( "Unknown '%1': '%2'", "AmazeType", amazeType );
//		}
//	}
//	
//	@Override
//	protected String getCreateIndexStatement( Index index, String indexLocation )
//	{
//		// For oracle we must check if the index already exists, if it does then we
//		// we do not need to add it again
//		// Check for existence
//		String statement = "declare v_count number;" + " begin" + " select  count(1) into v_count " + " from    all_indexes" + " where   index_name  = '" + index.IndexName.toUpperCase() + "'" + " and     owner       = '" + getDatabase().toUpperCase() + "'" + " and     table_name  = '" + index.table.TableName.toUpperCase() + "';" + " if v_count = 0 then" + "    execute immediate '" + "        create" + ( index.IsUnique ? " unique" : "" ) + " index " + index.IndexName + "        on " + index.table.TableName + "(" + index.ColumnList + ") ";
//		// Add the storage options if specified
//		if ( indexLocation != null && indexLocation.length() > 0 )
//			statement += " tablespace " + indexLocation + " ";
//		// Add the last part
//		statement += " nologging " + "    ';" + "  end if;" + " end;";
//		return statement;
//		// NOTE: There should only be ONE index option defined
//		// We don't do any validation here - we allow the SQL to fail since
//		// there will be more than one 'with ...'
//		// The SQL failure will alert the user to the problem in the schema
//	}
//	
//	@Override
//	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
//	{
//		List<String> sqlList = new ArrayList<String>();
//		String statement;
//		if ( checkExists )
//		{
//			statement = "declare v_count number;" + " begin" + " select count(1) into v_count from all_tables" + " where table_name = '" + table.TableName.toUpperCase() + "' and owner = '" + getDatabase().toUpperCase() + "';" + " if v_count > 0 then" + "    execute immediate 'drop table " + table.TableName + "';" + "  end if;" + " end;";
//		}
//		else
//		{
//			statement = "drop table " + table.TableName;
//		}
//		sqlList.add( statement );
//		return sqlList;
//	}
//	
	
	
}