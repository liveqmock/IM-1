package org.amaze.db.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.amaze.commons.utils.StringUtils;
import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.amaze.db.utils.exceptions.NotImplementedException;
import org.hibernate.SessionFactory;

public class CQLDataSource extends AbstractDataSource
{

	public CQLDataSource( String dataBase, String dataSourceName, SessionFactory sessionFactory, DataSource dataSource, Connection connection )
	{
		super( dataBase, "Cassandra", dataSourceName, sessionFactory, dataSource, connection );
	}

	@Override
	public void truncateTable( String database, String tableName ) throws DataSourceException
	{
		String query = "truncate " + tableName;
		execute( query );
	}

	@Override
	protected String getTableQuery( String database, String tableName, boolean exact )
	{
		throw new NotImplementedException( "Table Dfn operations are not supported for the Usage Data Source.. Use System Data Source instead..." );
	}

	@Override
	protected String getDefaultConstant( Column column ) throws DataSourceException
	{
		return null;
	}

	@Override
	protected String amazeTypeToDbType( AmazeType amazeType, int length ) throws DataSourceException
	{
		return null;
	}

	@Override
	protected List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException
	{
		String primaryKey = "PRIMARY KEY (";
		List<String> sqlList = new ArrayList<String>();
		String statement = null;
		if( dataLocation != null && dataLocation.length() > 0 )
			statement = " use " + dataLocation + ";";
		else
			statement = "";
		statement = statement + "create table " + table.tableName + StringUtils.NEW_LINE + "(";
		for ( Column col : table.columns )
		{
			String mandatory = " default " + getDefaultConstant( col ) + " " + ( col.isMandatory ? "not null," : "null    ," );
			statement += StringUtils.NEW_LINE + "    " + col.columnName + " " + amazeTypeToDbType( col.dataType, col.length ) + " " + mandatory;
		}
		statement = StringUtils.NEW_LINE + statement + primaryKey + " )";
		statement = statement + ")" + StringUtils.NEW_LINE;
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected String getCreateIndexStatement( Index index, String indexLocation )
	{
		String statement = "";
		if( indexLocation != null && indexLocation.length() > 0 )
			statement = " use " + indexLocation + ";";
		statement = statement + StringUtils.NEW_LINE + " create index " + index.indexName + " on " + index.table.tableName + " ( " + index.columnList + " )";
		return statement;
	}

	@Override
	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		sqlList.add( "drop table " + table.tableName );
		return sqlList;
	}

	@Override
	protected String getSelectDatabaseDDL( String databaseName ) throws DataSourceException
	{
		return "use " + databaseName;
	}

	@Override
	protected List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
	{
		throw new NotImplementedException( "Alter Table statement not supported for usage databases like cassandra" );
	}

	@Override
	protected String getMigrationName( Table table ) throws DataSourceException
	{
		return "temp_" + table.tableName.substring( 0, ( ( table.tableName.length() > 25 ) ? 25 : table.tableName.length() ) );
	}

	@Override
	protected String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException
	{
		throw new NotImplementedException( "Renaming the table or the partition swapping is not allowed for the usage database like Cassandra" );
	}

	@Override
	public String getFunctionName( String functionName ) throws DataSourceException
	{
		return null;
	}

	@Override
	protected String getSqlConvert( AmazeType to, int length, Column from ) throws DataSourceException
	{
		return null;
	}

	@Override
	protected List<String> getDropIndexDDL( Index index, boolean checkExists ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		sqlList.add( "drop index " + index.indexName );
		return sqlList;
	}

	@Override
	public void dropStoredProcedure( String procName ) throws DataSourceException
	{
		throw new NotImplementedException( "Stored procedures not supported for the Cassandra database" );
	}

	@Override
	public void applyStoredProcedure( String procName, String procedure ) throws DataSourceException
	{
		throw new NotImplementedException( "Stored procedures not supported for the Cassandra database" );
	}

	@Override
	public List<String[]> executeStoredProcedure( String procName, List args ) throws DataSourceException
	{
		throw new NotImplementedException( "Stored procedures not supported for the Cassandra database" );
	}

	@Override
	public IdGenerator getIdGenerator()
	{
		throw new NotImplementedException( " Id generator for the Cassandra is not supported" );
	}

	@Override
	public String getSelectString( String tableName, String[] colNames )
	{
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName ;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, long min, long max )
	{
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName + " limit " + max + "," + min + ";";
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max )
	{
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName + " " + orderByClause + " limit " + max + "," + min + ";";
	}

	@Override
	public String getSqlToFetchNRows( String tableName, long maxRows )
	{
		return "select * from " + tableName + " limit " + maxRows;
	}

	@Override
	public AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException
	{
		if ( dbType.toUpperCase().startsWith( "TIMESTAMP" ) || dbType.equalsIgnoreCase( "date" ) )
			return AmazeType.DateTime;
		if ( dbType.equalsIgnoreCase( "float" ) || ( dbType.equalsIgnoreCase( "double" ) && scale != 0 && ( dbType.equalsIgnoreCase( "decimal" ) ) ) ) 
			return AmazeType.Decimal;
		if ( dbType.equalsIgnoreCase( "integers" ) && precision <= 10 )
			return AmazeType.Int;
		if ( dbType.equalsIgnoreCase( "integers" ) && precision <= 19 )
			return AmazeType.Long;
		if ( dbType.equalsIgnoreCase( "strings" ) )
			return AmazeType.String;
		if ( dbType.equalsIgnoreCase( "varchar" ) || dbType.equalsIgnoreCase( "text" ) || dbType.equalsIgnoreCase( "nchar" ) )
			return AmazeType.String;
		if ( dbType.equalsIgnoreCase( "booleans" ) )
			return AmazeType.Bool;
		if ( ignoreError )
			return AmazeType.Unknown;
		else
			throw new DataSourceException( "DB Type '" + dbType + "' not supported" );
	}

	@Override
	public String getOrderByClause( List<Object[]> colNames, AmazeType[] resultTypes ) throws Exception
	{
		List<String> columnNames = new ArrayList<String>();
		int i = 0;
		for ( Object[] column : colNames )
		{
			if ( !AmazeType.Unknown.equals( resultTypes[i++] ) )
				columnNames.add( column[0].toString() );
		}
		return StringUtils.merge( columnNames, "," );
	}

}
