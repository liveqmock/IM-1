package org.amaze.db.utils;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleTypes;

import org.amaze.commons.utils.AmazeString;
import org.amaze.commons.utils.StringUtils;
import org.amaze.db.hibernate.types.DateTimeType;
import org.amaze.db.hibernate.types.DecimalType;
import org.amaze.db.hibernate.types.StringType;
import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;

public class OracleDataSource extends AbstractDataSource
{

	public OracleDataSource( String dataBase, String dataSourceName, SessionFactory sessionFactory, javax.sql.DataSource dataSource, Connection connection )
	{
		super( dataBase, "Oracle", dataSourceName, sessionFactory, dataSource, connection );
	}

	@Override
	public void truncateTable( String database, String tableName ) throws DataSourceException
	{
		String query = "truncate table " + tableName;
		execute( query );
	}

	@Override
	protected String getTableQuery( String database, String tableName, boolean exact )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( " select  tab.TABLE_NAME " );
		sb.append( " from      all_tables         tab " );
		sb.append( " where     tab.OWNER             = '" + database.toUpperCase() + "' " );
		if ( tableName != null && tableName.length() > 0 )
		{
			if ( exact )
			{
				sb.append( " and     tab.TABLE_NAME       = '" + tableName.toUpperCase() + "' " );
			}
			else
			{
				sb.append( " and     tab.TABLE_NAME       like '" + tableName.toUpperCase() + "' " );
			}
		}
		sb.append( " order by tab.TABLE_NAME" );
		return sb.toString();
	}

	@Override
	protected String getDefaultConstant( Column column ) throws DataSourceException
	{
		switch( column.dataType )
		{
		case Bool:
			return ( "'N'" );
		case DateTime:
			return "to_date('01-JAN-2000')";
		case Int:
		case Long:
		case Decimal:
			return "0";
		case String:
			return ( "''" );
		default:
			throw new IllegalArgumentException( "Unknown 'ConstantType': '" + column.dataType + "'" );
		}
	}

	@Override
	protected String amazeTypeToDbType( AmazeType amazeType, int length ) throws DataSourceException
	{
		switch( amazeType )
		{
		case Bool:
			return "char";
		case DateTime:
			return "timestamp";
		case Int:
			return "number(10)";
		case Long:
		case Decimal:
			return "number(19)";
		case String:
			return ( "varchar2(" + length + ")" );
		case Text:
			throw new DataSourceException( " AmazeType: Text is not supported for Oracle" );
		default:
			throw new DataSourceException( "Unknown '%1': '%2'", "AmazeType", amazeType );
		}
	}

	@Override
	protected List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		statement = "create table " + table.tableName + StringUtils.NEW_LINE + "(";
		for ( Column col : table.columns )
		{
			String mandatory = " default " + getDefaultConstant( col ) + " " + ( col.isMandatory ? "not null," : "null    ," );
			statement += StringUtils.NEW_LINE + "    " + col.columnName + " " + amazeTypeToDbType( col.dataType, col.length ) + " " + mandatory;
		}
		statement = statement.substring( 0, statement.length() - 1 ) + StringUtils.NEW_LINE + ")" + StringUtils.NEW_LINE;
		if ( dataLocation != null && dataLocation.length() > 0 )
			statement += " tablespace " + dataLocation;
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected String getCreateIndexStatement( Index index, String indexLocation )
	{
		// For oracle we must check if the index already exists, if it does then we
		// we do not need to add it again
		// Check for existence
		String statement = "declare v_count number;" + " begin" + " select  count(1) into v_count " + " from    all_indexes" + " where   index_name  = '" + index.indexName.toUpperCase() + "'" + " and     owner       = '" + getDatabase().toUpperCase() + "'" + " and     table_name  = '" + index.table.tableName.toUpperCase() + "';" + " if v_count = 0 then" + "    execute immediate '" + "        create" + ( index.isUnique ? " unique" : "" ) + " index " + index.indexName + "        on " + index.table.tableName + "(" + index.columnList + ") ";
		// Add the storage options if specified
		if ( indexLocation != null && indexLocation.length() > 0 )
			statement += " tablespace " + indexLocation + " ";
		// Add the last part
		statement += " nologging " + "    ';" + "  end if;" + " end;";
		return statement;
		// NOTE: There should only be ONE index option defined
		// We don't do any validation here - we allow the SQL to fail since
		// there will be more than one 'with ...'
		// The SQL failure will alert the user to the problem in the schema
	}

	@Override
	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		if ( checkExists )
		{
			statement = "declare v_count number;" + " begin" + " select count(1) into v_count from all_tables" + " where table_name = '" + table.tableName.toUpperCase() + "' and owner = '" + getDatabase().toUpperCase() + "';" + " if v_count > 0 then" + "    execute immediate 'drop table " + table.tableName + "';" + "  end if;" + " end;";
		}
		else
		{
			statement = "drop table " + table.tableName;
		}
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected String getSelectDatabaseDDL( String databaseName ) throws DataSourceException
	{
		return "";
	}

	@Override
	protected List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		StringBuilder addColsDDL = new StringBuilder();
		StringBuilder dropDefaultDDL = new StringBuilder();
		boolean hasMandatoryCols = false;
		addColsDDL.append( "ALTER TABLE " + newTable.tableName + " ADD ( " );
		dropDefaultDDL.append( "ALTER TABLE " + newTable.tableName + " MODIFY ( " );
		for ( int i = 0; i < oldTable.columns.size(); i++ )
		{
			Column oldColumn = oldTable.columns.get( i );
			Column newColumn = null;
			if ( ( newColumn = newTable.columns.get( i ) ) != null )
				if ( !oldColumn.equals( newColumn ) )
					return null;
		}
		for ( int i = oldTable.columns.size(); i < newTable.columns.size(); i++ )
		{
			Column newColumn = newTable.columns.get( i );
			String mandatory = "";
			if ( newColumn.isMandatory )
			{
				hasMandatoryCols = true;
				mandatory = " DEFAULT " + getDefaultConstant( newColumn ) + " NOT NULL ,";
				dropDefaultDDL.append( StringUtils.NEW_LINE + "    " + newColumn.columnName + "    " + amazeTypeToDbType( newColumn.dataType, newColumn.length ) + " DEFAULT NULL ," );
			}
			else
				mandatory = "NULL    ,";
			addColsDDL.append( StringUtils.NEW_LINE + "    " + newColumn.columnName + "    " + amazeTypeToDbType( newColumn.dataType, newColumn.length ) + " " + mandatory );
		}
		String statement = addColsDDL.toString();
		statement = statement.substring( 0, statement.length() - 1 ) + "  ) " + StringUtils.NEW_LINE;
		if ( dataLocation != null && dataLocation.length() > 0 )
			statement += " tablespace " + dataLocation;
		sqlList.add( statement );
		if ( hasMandatoryCols )
		{
			statement = dropDefaultDDL.toString();
			if ( !"".equals( statement ) )
				statement = statement.substring( 0, statement.length() - 1 ) + "  ) " + StringUtils.NEW_LINE;
			if ( dataLocation != null && dataLocation.length() > 0 )
				statement += " tablespace " + dataLocation;
			sqlList.add( statement );
		}
		return sqlList;
	}

	@Override
	protected String getMigrationName( Table table ) throws DataSourceException
	{
		// Return a migration name - temp_ limited to 30 chars
		return "temp_" + table.tableName.substring( 0, ( ( table.tableName.length() > 25 ) ? 25 : table.tableName.length() ) );
	}

	@Override
	protected String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException
	{
		return "rename " + fromTable + " to " + toTable;
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
		List<String> sqlList = new ArrayList<String>();
		String statement;
		statement = "";
		if ( checkExists )
		{
			statement = "declare v_count number; " + "begin " + "  select count(1) into v_count from all_indexes" + "  where index_name = '" + index.indexName.toUpperCase() + "'" + "  and owner = '" + getDatabase().toUpperCase() + "'" + "  and table_name = '" + index.table.tableName.toUpperCase() + "';" + "  if v_count > 0 then" + "    execute immediate 'drop index " + index.indexName + "';" + "  end if;" + "end;";
		}
		else
		{
			statement = "drop index " + index.indexName;
		}
		sqlList.add( statement );
		return sqlList;
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
		List<String[]> results = new ArrayList<String[]>();
		java.sql.Connection conn = null;
		try
		{
			conn = getConnection();
			DatabaseMetaData dbMetaData = conn.getMetaData();
			ResultSet resultSetMetaData = dbMetaData.getProcedureColumns( null, null, procName.toUpperCase(), null );
			resultSetMetaData.next();
			String queryStr = "begin ? := " + procName + "( ";
			if ( args.size() > 0 )
			{
				StringBuilder sb = new StringBuilder();
				for ( int i = 0; i < args.size(); i++ )
				{
					sb.append( "?," );
				}
				sb.setLength( sb.length() - 1 );
				queryStr += sb.toString();
			}
			queryStr += "); end;";
			CallableStatement stmt = conn.prepareCall( queryStr );
			stmt.registerOutParameter( 1, OracleTypes.CURSOR );
			try
			{
				int index = 2;
				for ( Object arg : args )
				{
					try
					{
						Integer paramIndex = index++;
						Object paramValue = arg;
						if ( arg == null )
						{
							stmt.setNull( paramIndex, getSqlType( resultSetMetaData.getObject( 7 ) ) );
						}
						else if ( paramValue instanceof Integer )
						{
							stmt.setInt( paramIndex, ( ( Integer ) paramValue ) );
						}
						else if ( paramValue instanceof Long )
						{
							stmt.setLong( paramIndex, ( ( Long ) paramValue ) );
						}
						else if ( paramValue instanceof String )
						{
							StringType.STRING_TYPE.nullSafeSet( stmt, paramValue, paramIndex, null );
						}
						else if ( paramValue instanceof DateTime )
						{
							DateTimeType.DATE_TIME_TYPE.nullSafeSet( stmt, paramValue, paramIndex, null );
						}
						else if ( paramValue instanceof Boolean )
						{
							stmt.setString( paramIndex, booleanToYesNo( ( ( Boolean ) paramValue ).booleanValue() ) );
						}
						else if ( paramValue instanceof BigDecimal )
						{
							DecimalType.DECIMAL_TYPE.nullSafeSet( stmt, paramValue, paramIndex, null );
						}
						else
						{
							throw new IllegalArgumentException( AmazeString.create( "Unknown paramValue type '%1'.", paramValue.getClass() ) );
						}
					}
					catch ( HibernateException e )
					{
						throw new SQLException( "Hibernate exception: ", e );
					}
				}
				stmt.execute();
				ResultSet rs = ( ResultSet ) stmt.getObject( 1 );
				try
				{
					int colCount = rs.getMetaData().getColumnCount();
					while ( rs.next() )
					{
						String[] row = new String[colCount];
						results.add( row );
						for ( int i = 0; i < colCount; i++ )
							row[i] = rs.getString( i + 1 );
					}
				}
				finally
				{
					rs.close();
				}
			}
			finally
			{
				stmt.close();
			}
			conn.commit();
		}
		catch ( Exception e )
		{
			try
			{
				if ( conn != null )
					conn.rollback();
			}
			catch ( Exception e1 )
			{
				logger.error( "Error rolling back transaction", e1 );
			}
			throw new DataSourceException( e );
		}
		finally
		{
			try
			{
				if ( conn != null )
					conn.close();
			}
			catch ( Exception e )
			{
				logger.error( "Error closing session", e );
			}
		}
		return results;
	}

	private static int getSqlType( Object type )
	{
		if ( type.toString().equals( "NUMBER" ) )
			return java.sql.Types.NUMERIC;
		if ( type.toString().equals( "TIMESTAMP" ) )
			return java.sql.Types.TIMESTAMP;
		if ( type.toString().equals( "BOOLEAN" ) )
			return java.sql.Types.BOOLEAN;
		if ( type.toString().equals( "VARCHAR" ) )
			return java.sql.Types.VARCHAR;
		if ( type.toString().equals( "DECIMAL" ) )
			return java.sql.Types.DECIMAL;
		if ( type.toString().equals( "BIGINT" ) )
			return java.sql.Types.BIGINT;
		return 0;
	}

	public static String booleanToYesNo( boolean value )
	{
		return ( value ? "Y" : "N" );
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
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName + " where rownum = 1";
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, long min, long max )
	{
		return "SELECT " + StringUtils.merge( colNames, ", " ) + " FROM ( SELECT " + tableName + ".*, ROWNUM  AS ROWNO FROM " + tableName + ") TMP WHERE TMP.ROWNO between 0 " + "and " + max;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max )
	{
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName + " where rownum = 1" + " order by " + orderByClause;
	}

	@Override
	public String getSqlToFetchNRows( String tableName, long maxRows )
	{
		return "select * from " + tableName + " where rownum <= " + maxRows;
	}

	@Override
	public AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException
	{
		if ( dbType.toUpperCase().startsWith( "TIMESTAMP" ) || dbType.equalsIgnoreCase( "date" ) )
			return AmazeType.DateTime;
		if ( dbType.equalsIgnoreCase( "float" ) || ( dbType.equalsIgnoreCase( "number" ) && scale != 0 ) )
			return AmazeType.Decimal;
		if ( dbType.equalsIgnoreCase( "number" ) && precision <= 10 )
			return AmazeType.Int;
		if ( dbType.equalsIgnoreCase( "number" ) && precision <= 19 )
			return AmazeType.Long;
		if ( dbType.equalsIgnoreCase( "nvarchar2" ) )
			return AmazeType.String;
		if ( dbType.equalsIgnoreCase( "varchar2" ) || dbType.equalsIgnoreCase( "char" ) || dbType.equalsIgnoreCase( "nchar" ) )
			return AmazeType.String;
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
