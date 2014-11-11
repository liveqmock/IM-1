package org.amaze.database.utils.oracle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import oracle.jdbc.OracleTypes;

import org.amaze.common.process.AmazeProcess;
import org.amaze.common.process.exceptions.AmazeProcessInterruptedException;
import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.io.SafeBufferedReader;
import org.amaze.commons.io.SafeBufferedWriter;
import org.amaze.commons.listeners.TaskStateListener;
import org.amaze.commons.utils.AmazeString;
import org.amaze.commons.utils.StringUtils;
import org.amaze.database.hibernate.types.DateTimeType;
import org.amaze.database.hibernate.types.DecimalType;
import org.amaze.database.hibernate.types.StringType;
import org.amaze.database.schema.AmazeType;
import org.amaze.database.schema.Column;
import org.amaze.database.schema.Database;
import org.amaze.database.schema.Index;
import org.amaze.database.schema.Schema;
import org.amaze.database.schema.Table;
import org.amaze.database.utils.AbstractDataSource;
import org.amaze.database.utils.DataSource;
import org.amaze.database.utils.DatabaseType;
import org.amaze.database.utils.IdGenerator;
import org.amaze.database.utils.ResultSetRowSource;
import org.amaze.database.utils.bulkloader.AbstractBulkLoader;
import org.amaze.database.utils.bulkloader.BCPConverter;
import org.amaze.database.utils.bulkloader.BulkLoader;
import org.amaze.database.utils.bulkloader.DataLoadProgress;
import org.amaze.database.utils.bulkloader.RowSource;
import org.amaze.database.utils.exceptions.DataSourceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class OracleDataSource extends AbstractDataSource
{
	private static final Logger logger = LogManager.getLogger( OracleDataSource.class );

	private static final String DEFAULT_DATETIME_FORMAT = "YYYYMMDD HH24:MI:SS";

	private static final IdGenerator idGenerator = new OracleIdGenerator();

	private static Map<String, String> functionNameMap = new HashMap<String, String>();
	
    private static String logFileSucceededCountLineSingle = "Row successfully loaded";
    private static String logFileFailedCountLineSingle = "Row not loaded due to data errors";
    private static String logFileSucceededCountLinePlural = "Rows successfully loaded";
    private static String logFileFailedCountLinePlural = "Rows not loaded due to data errors";
    private static Pattern sqlldrErrorPattern = Pattern.compile( "SQL\\*Loader\\-[0-9]+:.*" );

    private static Map< String, Integer > logMessageVsPositionMap = new HashMap< String, Integer >();
    
	public OracleDataSource( String username, String password, String url, String driverClassName, String database, Schema schema, Boolean isUnicode )
	{
		super( username, password, url, driverClassName, database, schema, isUnicode );
	}

	static
	{
		functionNameMap.put( "ISNULL", "NVL" );
	}

	@Override
	public DatabaseType getDatabaseType()
	{
		return DatabaseType.Oracle;
	}

	@Override
	public BulkLoader createBulkLoader( Table table )
	{
		return new OracleBulkLoader( this, table );
	}

	@Override
	public BulkLoader createBulkLoader( Table table, TaskStateListener dataLoadTaskListener )
	{
		return new OracleBulkLoader( this, table, dataLoadTaskListener );
	}

	@Override
	public DateTimeFormatter getBcpDateFormatter()
	{
		return DateTimeFormat.forPattern( "yyyyMMdd" );
	}

	@Override
	public DateTimeFormatter getBcpDateTimeFormatter()
	{
		return DateTimeFormat.forPattern( "yyyyMMdd HH:mm:ss" );
	}

	@Override
	public String getSqlDateConstant( DateTime dateTime )
	{
		return "TO_TIMESTAMP('" + getBcpDateTimeFormatter().print( dateTime ) + "', 'YYYYMMDD HH24:MI:SS')";
	}

	@Override
	public Charset getBcpCharset()
	{
		return ( Charset.defaultCharset() );
	}

	@Override
	public String bcpIn( String tableName, String filename, String delimiter, Table table ) throws DataSourceException
	{
		BulkLoader bulkLoader = createBulkLoader( table );
		return bulkLoader.loadFile( new File( filename ) );
	}

	@Override
	public String bcpOut( String tableName, String filename, String delimiter, Table table ) throws DataSourceException
	{
		File file = new File( filename );
		BufferedWriter bufferedWriter = null;
		try
		{
			bufferedWriter = new SafeBufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), Charset.defaultCharset() ) );
		}
		catch ( IOException e )
		{
			throw new DataSourceException( e );
		}
		PrintWriter printWriter = new PrintWriter( bufferedWriter );

		AmazeType[] resultTypes = getTableColumnTypes( table );
		String[] names = getTableColumnNames( table );

		String sql = "select " + StringUtils.merge( names, ", " ) + " from " + tableName;

		try
		{
			Connection conn = getConnection();
			try
			{
				PreparedStatement ps = conn.prepareStatement( sql );
				try
				{
					ResultSet rs = ps.executeQuery();
					try
					{
						RowSource<Object[]> rowSource = new ResultSetRowSource( rs, resultTypes );

						DateTimeFormatter dateTimeFormatter = getBcpDateTimeFormatter();
						while ( rowSource.next() )
						{
							Object[] row = rowSource.get();

							for ( int i = 0; i < row.length; i++ )
							{
								printWriter.print( BCPConverter.toBCPString( resultTypes[i], dateTimeFormatter, row[i] ) );
								printWriter.print( delimiter );
							}

							// add new line
							printWriter.println();
						}

						printWriter.close();
					}
					finally
					{
						rs.close();
					}
				}
				finally
				{
					ps.close();
				}
			}
			finally
			{
				conn.close();
			}
		}
		catch ( SQLException e )
		{
			throw new DataSourceException( e );
		}

		return null;
	}

	@Override
	public String getUniqueConnectionId( Connection connection ) throws DataSourceException
	{
		List<Object[]> results = executeQuery( connection, "select userenv( 'sessionid' ) from dual", new AmazeType[]
		{ AmazeType.Long } );
		if ( results.size() != 1 )
		{
			throw new DataSourceException( "Got '%1' rows querying for session id, expected 1", results.size() );
		}
		Long sid = ( Long ) results.get( 0 )[0];
		return sid.toString() + "_" + getValue();
	}

	@Override
	public void truncateTable( String database, String tableName ) throws DataSourceException
	{
		String query = "truncate table " + tableName;
		execute( query );
	}

	@Override
	public void dropStoredProcedure( String procName ) throws DataSourceException
	{

	}

	@Override
	public void applyStoredProcedure( Map<String, String> map, String procName, String macro ) throws DataSourceException
	{

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
	public String getDataSourceName()
	{
		return "Oracle";
	}

	@Override
	public IdGenerator getIdGenerator()
	{
		return idGenerator;
	}

	@Override
	public String getSqlToFetchNRows( String tableName, long maxRows )
	{
		return "select * from " + tableName + " where rownum <= " + maxRows;
	}

	@Override
	public String getSelectString( String tableName, String[] colNames, long min, long max )
	{
		return "SELECT " + StringUtils.merge( colNames, ", " ) + " FROM ( SELECT " + tableName + ".*, ROWNUM  AS ROWNO FROM " + tableName + ") TMP WHERE TMP.ROWNO between 0 " + "and " + max;
	}

	@Override
	public String escapeString( String s )
	{
		return s;
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
	protected AmazeType dbTypeToAmazeType( String dbType, int length ) throws DataSourceException
	{
		if ( dbType.equalsIgnoreCase( "char" ) )
			return AmazeType.Bool;
		if ( dbType.toUpperCase().startsWith( "TIMESTAMP" ) )
			return AmazeType.DateTime;
		if ( dbType.equalsIgnoreCase( "number" ) && length <= 10 )
			return AmazeType.Int;
		if ( dbType.equalsIgnoreCase( "number" ) && length <= 19 )
			return AmazeType.Long;
		if ( dbType.equalsIgnoreCase( "nvarchar2" ) )
			return AmazeType.String;
		if ( dbType.equalsIgnoreCase( "varchar2" ) )
			return AmazeType.String;
		if ( dbType.equalsIgnoreCase( "nclob" ) )
			throw new DataSourceException( " nclob for oracle not supported " );
		return null;
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
			return ( isUnicode ? "nvarchar2(" + length + ")" : "varchar2(" + length + ")" );
		case Text:
			throw new DataSourceException( " AmazeType: Text is not supported for Oracle" );
		default:
			throw new DataSourceException( "Unknown '%1': '%2'", "AmazeType", amazeType );
		}
	}

	@Override
	protected String getAllIndexDetailsQuery( String database, String tableName )
	{
		return getAllIndexDetailsQuery( database, tableName, false );
	}

	@Override
	protected String getAllIndexDetailsQuery( String database, String tableName, boolean isViewRequired )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "select  inx.table_name as table_name,                       " );
		sb.append( "        inx.index_name as index_name,                       " );
		sb.append( "        column_name,                                        " );
		sb.append( "        case                                                " );
		sb.append( "            when inx.index_type = 'IOT - TOP' then 'Y'      " );
		sb.append( "            else 'N'                                        " );
		sb.append( "        end,                                                " );
		sb.append( "        case                                                " );
		sb.append( "            when inx.uniqueness = 'UNIQUE' then 'Y'         " );
		sb.append( "            when inx.uniqueness = 'NONUNIQUE' then 'N'      " );
		sb.append( "        end,                                                " );
		sb.append( "        'N' as ignore_dup_key,                              " );
		sb.append( "        cast(inc.column_position as NUMBER(10))             " );
		sb.append( "from    all_indexes         inx,                            " );
		sb.append( "        all_ind_columns     inc                             " );
		sb.append( "where   inx.owner           = '" + database.toUpperCase() + "'        " );
		sb.append( "and     inx.table_name      = inc.table_name                " );
		sb.append( "and     inx.index_name      = inc.index_name                " );
		sb.append( "and     inx.owner           = inc.index_owner               " );
		sb.append( "and     inx.owner           = inc.table_owner               " );
		if ( tableName != null && tableName.length() > 0 )
			sb.append( " and     inx.table_name       = '" + tableName.toUpperCase() + "' " );
		sb.append( "order by inx.table_name, inx.index_name, cast(inc.column_position as NUMBER(10))" );
		return sb.toString();
	}

	@Override
	protected String getAllTableDetailsQuery( String database, String tableName, boolean isViewRequired )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( " select   tab.TABLE_NAME, " );
		sb.append( "          col.COLUMN_NAME, " );
		sb.append( "          col.DATA_TYPE, " );
		sb.append( "          cast( " );
		sb.append( "              case " );
		sb.append( "                  when col.CHAR_COL_DECL_LENGTH is NULL then col.DATA_PRECISION " );
		sb.append( "                  else col.CHAR_LENGTH " );
		sb.append( "              end " );
		sb.append( "          as NUMBER(10)), " );
		sb.append( "          cast( " );
		sb.append( "              col.NULLABLE " );
		sb.append( "          as CHAR(1)), " );
		sb.append( "          cast( " );
		sb.append( "              col.COLUMN_ID " );
		sb.append( "          as NUMBER(10)) " );
		sb.append( " from    all_tab_columns    col, " );
		sb.append( "         all_catalog         tab " );
		sb.append( " where   tab.TABLE_NAME        = col.TABLE_NAME " );
		sb.append( " and     tab.OWNER             = col.OWNER " );
		sb.append( " and     tab.OWNER             = '" + database.toUpperCase() + "' " );
		sb.append( " and     tab.TABLE_TYPE             in ('TABLE', 'VIEW') " );
		if ( tableName != null && tableName.length() > 0 )
		{
			sb.append( " and     tab.TABLE_NAME       = '" + tableName.toUpperCase() + "' " );
		}
		sb.append( " order by tab.TABLE_NAME, cast(col.COLUMN_ID as NUMBER(10))" );
		return sb.toString();
	}

	@Override
	protected String getAllTableDetailsQuery( String database, String tableName )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( " select   tab.TABLE_NAME, " );
		sb.append( "          col.COLUMN_NAME, " );
		sb.append( "          col.DATA_TYPE, " );
		sb.append( "          cast( " );
		sb.append( "              case " );
		sb.append( "                  when col.CHAR_COL_DECL_LENGTH is NULL then col.DATA_PRECISION " );
		sb.append( "                  else col.CHAR_LENGTH " );
		sb.append( "              end " );
		sb.append( "          as NUMBER(10)), " );
		sb.append( "          cast( " );
		sb.append( "              col.NULLABLE " );
		sb.append( "          as CHAR(1)), " );
		sb.append( "          cast( " );
		sb.append( "              col.COLUMN_ID " );
		sb.append( "          as NUMBER(10)) " );
		sb.append( " from    all_tab_columns    col, " );
		sb.append( "         all_tables         tab " );
		sb.append( " where   tab.TABLE_NAME        = col.TABLE_NAME " );
		sb.append( " and     tab.OWNER             = col.OWNER " );
		sb.append( " and     tab.OWNER             = '" + database.toUpperCase() + "' " );
		if ( tableName != null && tableName.length() > 0 )
		{
			sb.append( " and     tab.TABLE_NAME       = '" + tableName.toUpperCase() + "' " );
		}
		sb.append( " order by tab.TABLE_NAME, cast(col.COLUMN_ID as NUMBER(10))" );
		return sb.toString();
	}

	@Override
	protected List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		statement = "create table " + table.TableName + StringUtils.NEW_LINE + "(";
		for ( Column col : table.Columns )
		{
			String mandatory = col.isAuditEnabled ? " default " + getDefaultConstant( col ) + " not null," : col.IsMandatory ? "not null," : "null    ,";
			statement += StringUtils.NEW_LINE + "    " + col.ColumnName + " " + amazeTypeToDbType( col.DataType, col.Length ) + " " + mandatory;
		}
		statement = statement.substring( 0, statement.length() - 1 ) + StringUtils.NEW_LINE + ")" + StringUtils.NEW_LINE;
		if ( dataLocation != null && dataLocation.length() > 0 )
			statement += " tablespace " + dataLocation;
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		if ( checkExists )
		{
			statement = "declare v_count number;" + " begin" + " select count(1) into v_count from all_tables" + " where table_name = '" + table.TableName.toUpperCase() + "' and owner = '" + database.toUpperCase() + "';" + " if v_count > 0 then" + "    execute immediate 'drop table " + table.TableName + "';" + "  end if;" + " end;";
		}
		else
		{
			statement = "drop table " + table.TableName;
		}
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected List<String> getCreateIndexDDL( Index index, String indexLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		sqlList.add( getCreateIndexStatement( index, indexLocation ) );
		return sqlList;
	}

	@Override
	protected List<String> getDropIndexDDL( Index index, boolean checkExists ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		statement = "";
		if ( checkExists )
		{
			statement = "declare v_count number; " + "begin " + "  select count(1) into v_count from all_indexes" + "  where index_name = '" + index.IndexName.toUpperCase() + "'" + "  and owner = '" + database.toUpperCase() + "'" + "  and table_name = '" + index.table.TableName.toUpperCase() + "';" + "  if v_count > 0 then" + "    execute immediate 'drop index " + index.IndexName + "';" + "  end if;" + "end;";
		}
		else
		{
			statement = "drop index " + index.IndexName;
		}
		sqlList.add( statement );
		return sqlList;
	}

	@Override
	protected List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		StringBuilder addColsDDL = new StringBuilder();
		StringBuilder dropDefaultDDL = new StringBuilder();
		boolean hasMandatoryCols = false;
		addColsDDL.append( "ALTER TABLE " + newTable.TableName + " ADD ( " );
		dropDefaultDDL.append( "ALTER TABLE " + newTable.TableName + " MODIFY ( " );
		for ( int i = 0; i < oldTable.Columns.size(); i++ )
		{
			Column oldColumn = oldTable.Columns.get( i );
			Column newColumn = null;
			if ( ( newColumn = newTable.Columns.get( i ) ) != null )
				if ( !oldColumn.equals( newColumn ) )
					return null;
		}
		for ( int i = oldTable.Columns.size(); i < newTable.Columns.size(); i++ )
		{
			Column newColumn = newTable.Columns.get( i );
			String mandatory = "";
			if ( newColumn.IsMandatory )
			{
				hasMandatoryCols = true;
				mandatory = " DEFAULT " + getDefaultConstant( newColumn ) + " NOT NULL ,";
				dropDefaultDDL.append( StringUtils.NEW_LINE + "    " + newColumn.ColumnName + "    " + amazeTypeToDbType( newColumn.DataType, newColumn.Length ) + " DEFAULT NULL ," );
			}
			else
				mandatory = "NULL    ,";
			addColsDDL.append( StringUtils.NEW_LINE + "    " + newColumn.ColumnName + "    " + amazeTypeToDbType( newColumn.DataType, newColumn.Length ) + " " + mandatory );
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
	protected List<String> getUpdateTableDDL( Table oldTable, Table newTable, String dataLocation, String indexLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		Table migrationTable = null;
		try
		{
			migrationTable = ( Table ) newTable.clone();
		}
		catch ( CloneNotSupportedException e )
		{
			throw new DataSourceException( "Unable to clone table %1", e, newTable.TableName );
		}
		migrationTable.TableName = getMigrationName( newTable );
		if ( newTable.Database.Tables.contains( migrationTable ) )
		{
			throw new DataSourceException( "The schema already contains the migration table '%1'", migrationTable.TableName );
		}
		for ( Index index : migrationTable.Indexes )
		{
			String migrationIndexName = "temp_" + index.IndexName.substring( 0, ( ( index.IndexName.length() > 25 ) ? 25 : index.IndexName.length() ) );
			index.IndexName = migrationIndexName;
		}
		newTable.Database.Tables.add( migrationTable );
		sqlList.addAll( getCreateTableDDL( migrationTable, dataLocation ) );
		StringBuilder migrationTableSQ = new StringBuilder();
		migrationTableSQ.append( "select " );
		for ( int i = 0; i < newTable.Columns.size(); i++ )
		{
			Column newColumn = newTable.Columns.get( i );
			String selectColumn = "";
			if ( oldTable.Columns.contains( newColumn ) )
			{
				Column oldColumn = oldTable.findColumn( newColumn.ColumnName );
				if ( ( oldColumn.IsMandatory == newColumn.IsMandatory ) || ( !newColumn.IsMandatory ) )
				{
					selectColumn = newColumn.ColumnName;
				}
				else
				{
					selectColumn = "nvl(" + newColumn.ColumnName + ", " + getDefaultConstant( newColumn ) + ")";
				}
			}
			else
			{
				if ( newColumn.IsMandatory )
				{
					selectColumn = getDefaultConstant( newColumn );
				}
				else
				{
					selectColumn = "NULL";
				}
			}
			migrationTableSQ.append( selectColumn + ", " );
		}
		if ( newTable.Columns.size() > 0 )
		{
			migrationTableSQ.setLength( migrationTableSQ.length() - 2 );
		}
		migrationTableSQ.append( " from " + oldTable.TableName );
		String migrationTableIQ = "insert into " + migrationTable.TableName + "(" + StringUtils.merge( migrationTable.getColumnNames(), "," ) + ")" + StringUtils.NEW_LINE + migrationTableSQ.toString();
		sqlList.add( migrationTableIQ );
		sqlList.addAll( getDropTableDDL( oldTable, false ) );
		sqlList.addAll( getCreateTableDDL( newTable, dataLocation ) );
		String newTableIQ = "insert into " + newTable.TableName + "(" + StringUtils.merge( newTable.getColumnNames(), "," ) + ")" + StringUtils.NEW_LINE + "select * from " + migrationTable.TableName;
		sqlList.add( newTableIQ );
		newTable.Database.Tables.remove( migrationTable );
		sqlList.addAll( getDropTableDDL( migrationTable, true ) );
		return sqlList;
	}

	@Override
	protected String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException
	{
		return "rename " + fromTable + " to " + toTable;
	}

	@Override
	protected String getSelectDatabaseDDL( String databaseName ) throws DataSourceException
	{
		return "";
	}

	@Override
	protected void applyAfterTableCreateIndexes( Table table, String dataLocation, String indexLocation ) throws DataSourceException
	{
		applyIndexes( table, indexLocation, false );
	}

	@Override
	protected String getSqlConvert( AmazeType to, int length, Column from ) throws DataSourceException
	{
		try
		{
			return " " + getFunctionName( "cast" ) + "( " + from.ColumnName + " as " + this.amazeTypeToDbType( to, length ) + " ) ";
		}
		catch ( DataSourceException e )
		{
			throw e;
		}
	}

	@Override
	public String getFunctionName( String functionName ) throws DataSourceException
	{
		String newFunctionName = functionNameMap.get( functionName.toUpperCase() );

		if ( newFunctionName == null )
		{
			throw new DataSourceException( "Unrecognised function %1", functionName );
		}

		return newFunctionName;
	}

	@Override
	protected String getMigrationName( Table table )
	{
		// Return a migration name - temp_ limited to 30 chars
		return "temp_" + table.TableName.substring( 0, ( ( table.TableName.length() > 25 ) ? 25 : table.TableName.length() ) );
	}

	@Override
	protected String getDefaultConstant( Column column )
	{
		switch( column.DataType )
		{
		case Bool:
			return ( isUnicode ? "UNISTR( 'N' )" : "'N'" );
		case DateTime:
			return "to_date('01-JAN-2000')";
		case Int:
		case Long:
		case Decimal:
			return "0";
		case String:
			return ( isUnicode ? "UNISTR( 'X' )" : "'X'" );
		default:
			throw new IllegalArgumentException( "Unknown 'ConstantType': '" + column.DataType + "'" );
		}
	}

	@Override
	public String getSelectString( String tableName, String[] colNames )
	{
		return "select " + StringUtils.merge( colNames, ", " ) + " from " + tableName + " where rownum = 1";
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
	

	class OracleBulkLoader extends AbstractBulkLoader
	{
		String customDelimiter = null;
		TaskStateListener dataLoadTaskListener = null;

		public OracleBulkLoader( DataSource destinationDataSource )
		{
			super( destinationDataSource );
		}

		public OracleBulkLoader( DataSource destinationDataSource, Table tableSchema )
		{
			super( destinationDataSource, tableSchema );
		}

		public OracleBulkLoader( DataSource destinationDataSource, Table tableSchema, TaskStateListener dataLoadTaskListener )
		{
			super( destinationDataSource, tableSchema );
			this.dataLoadTaskListener = ( TaskStateListener ) dataLoadTaskListener;
		}

		public String loadFile( File file, String delimiter )
		{
			this.customDelimiter = delimiter;
			return loadFile( file );
		}

		public String loadFile( File file )
		{
			try
			{
				String delimiter = BCP_FIELD_DELIMITER;
				String tableName = tableSchema.TableName;
				String filename = file.getAbsolutePath();
				if ( customDelimiter != null )
					delimiter = this.customDelimiter;
				boolean isFastBulkLoad = this.targetTableEmpty || this.hasNoIndexes;
				if ( isUnicode )
				{
					isFastBulkLoad = false;
				}
				if ( !file.exists() )
				{
					throw new DataSourceException( AmazeString.create( "The BCP source file '%1' specified does not exist", filename ) );
				}
				String output = null;
				if ( file.length() != 0 )
				{
					File tempControlFile = null;
					File tempLogFile = null;
					File tempParameterFile = null;
					try
					{
						tempControlFile = File.createTempFile( "bcp_" + tableName + "_", ".ctl" );
						tempLogFile = File.createTempFile( "bcp_" + tableName + "_", ".log" );
						tempParameterFile = File.createTempFile( "bcp_" + tableName + "_", ".par" );
					}
					catch ( IOException e )
					{
						throw new DataSourceException( "Error creating control file", e, filename );
					}
					PrintWriter writer = null;
					try
					{
						writer = new PrintWriter( new SafeBufferedWriter( new OutputStreamWriter( new FileOutputStream( tempControlFile ), isUnicode ? Charset.forName( "UTF-16LE" ) : Charset.defaultCharset() ) ) );
					}
					catch ( IOException e )
					{
						throw new DataSourceException( "Error creating control file", e, filename );
					}
					if ( delimiter.equals( "\t" ) )
					{
						delimiter = "\\t";
					}
					writer.println( "LOAD DATA" );
					if ( isUnicode )
						writer.println( "CHARACTERSET UTF16" );
					writer.println( "INFILE '" + filename + "'" );
					writer.println( "APPEND INTO TABLE " + tableName );
					writer.println( "FIELDS TERMINATED BY '" + delimiter + "' TRAILING NULLCOLS " );
					writer.println( "(" );
					List<String> columnData = new ArrayList<String>();
					for ( Column column : tableSchema.Columns )
					{
						if ( column.DataType == AmazeType.DateTime )
						{
							columnData.add( column.ColumnName + " TIMESTAMP \"" + DEFAULT_DATETIME_FORMAT + "\"" );
						}
						else if ( column.DataType == AmazeType.String )
						{
							columnData.add( column.ColumnName + ( isUnicode ? " CHAR(510) " : " CHAR(255) " ) );
						}
						else
						{
							columnData.add( column.ColumnName );
						}
					}
					writer.println( StringUtils.merge( columnData, ", " ) );
					writer.println( ")" );
					writer.close();
					try
					{
						writer = new PrintWriter( new SafeBufferedWriter( new OutputStreamWriter( new FileOutputStream( tempParameterFile ), isUnicode ? Charset.forName( "UTF-16LE" ) : Charset.defaultCharset() ) ) );
					}
					catch ( IOException e )
					{
						throw new DataSourceException( "Error creating control file", e, filename );
					}
					writer.println( "log='" + tempLogFile.getAbsolutePath() + "'" );
					writer.println( "control='" + tempControlFile.getAbsolutePath() + "'" );
					writer.println( ( isFastBulkLoad ? "" : "rows=10000 bindsize=20971520 readsize=20971520" ) );
					writer.println( "direct=" + ( isFastBulkLoad ? "true" : "false" ) );
					writer.println( "parallel=" + ( isFastBulkLoad ? "true" : "false" ) );
					writer.println( "errors=0" );
					writer.println( "userid=" + username + "/" + password + "@" + database );
					writer.close();
					DataLoadProgress dataLoadProgress = null;
					AmazeProcess process = new AmazeProcess();
					process.setCommand( "sqlldr" );
					process.addParameters( "parfile='" + tempParameterFile.getAbsolutePath() + "'" );
					try
					{
						if ( dataLoadTaskListener != null )
						{
							dataLoadProgress = new DataLoadProgress( dataLoadTaskListener );
							dataLoadProgress.setAmazeProcess( process );
							process.setProcessProgressThread( dataLoadProgress );
						}
						process.execute();
						output = process.getOutput();
						List<String> outputLines = filterBCPOutput( output );
						if ( outputLines == null )
						{
							throw new DataSourceException( "Fatal error during BCPing file %1 to datasource %2 and table %3..%4. %5", filename, getDataSourceName(), database, tableName, output );
						}
						String fileContents = getFileContents( tempLogFile, Charset.defaultCharset() );
						int succeededRecordCount = parseLogFileOutput( fileContents );
						tempControlFile.delete();
						tempLogFile.delete();
						tempParameterFile.delete();
					}
					catch ( IOException e )
					{
						throw new DataSourceException( e );
					}
					catch ( InterruptedException e )
					{
						throw new AmazeProcessInterruptedException( e );
					}
				}
				return output;
			}
			catch ( DataSourceException e )
			{
				throw new AmazeException( e );
			}
		}

		public String getFileContents( File file, Charset charset ) throws IOException
	    {
	        Reader reader = new SafeBufferedReader( new InputStreamReader( new FileInputStream( file ), charset ) ); 
	        StringBuilder sb;
	        try
	        {
	            char[] buffer = new char[8 * 1024];
	            int read = -1;
	            sb = new StringBuilder();
	            while ( (read = reader.read( buffer )) != -1 )
	            {
	                sb.append( buffer, 0, read );
	            }
	        }
	        finally
	        {
	            reader.close();
	        }
	        return sb.toString();
	    }
	}

	private int parseLogFileOutput( String logFileString ) throws DataSourceException
	{
		String[] lines = StringUtils.split( logFileString, StringUtils.NEW_LINE );
		int succeededCount = -1;
		int failedCount = -1;
		boolean hasError = false;
		for ( String line : lines )
		{
			if ( line.contains( logFileSucceededCountLineSingle ) )
			{
				succeededCount = getRecordCount( line, logFileSucceededCountLineSingle );
			}
			else if ( line.contains( logFileSucceededCountLinePlural ) )
			{
				succeededCount = getRecordCount( line, logFileSucceededCountLinePlural );
			}
			else if ( line.contains( logFileFailedCountLineSingle ) )
			{
				failedCount = getRecordCount( line, logFileFailedCountLineSingle );
			}
			else if ( line.contains( logFileFailedCountLinePlural ) )
			{
				failedCount = getRecordCount( line, logFileFailedCountLinePlural );
			}
			if ( sqlldrErrorPattern.matcher( line ).matches() )
			{
				hasError = true;
			}
		}
		if ( failedCount == -1 )
		{
			throw new DataSourceException( "Could not found failed record count in log file '%1'", logFileString );
		}
		if ( succeededCount == -1 )
		{
			throw new DataSourceException( "Could not found succeeded record count in log file '%1'", logFileString );
		}
		if ( failedCount > 0 || hasError )
		{
			throw new DataSourceException( "Bulk load failed '%1'", logFileString );
		}
		return succeededCount;
	}

	private int getRecordCount( String line, String logFileLine )
	{
		int recordCount;
		int position;
		String countStr = null;
		if ( logMessageVsPositionMap.containsKey( logFileLine ) )
		{
			position = logMessageVsPositionMap.get( logFileLine );
			countStr = StringUtils.getWord( line, " ", position );
			recordCount = Integer.parseInt( countStr == null ? "0" : countStr );
		}
		else
			recordCount = Integer.parseInt( StringUtils.firstWord( line ) );
		return recordCount;
	}

	private Integer getValue()
	{
		Integer val = ( new Random() ).nextInt( 1000 );
		if ( val < 0 )
		{
			return -val;
		}
		return val;
	}

	private List getColumnNames( String database, String tableName ) throws DataSourceException
	{
		String query = getTableColumnsQuery( database, tableName );
		Connection connection = null;
		List columnNames = null;
		try
		{
			connection = getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( query );

			columnNames = new ArrayList();

			while ( rs.next() )
			{
				String columnName = rs.getString( 1 );
				columnNames.add( columnName );
			}
		}
		catch ( SQLException e )
		{
			throw new DataSourceException( e );
		}
		finally
		{
			if ( connection != null )
			{
				try
				{
					connection.close();
				}
				catch ( Exception e )
				{

				}
			}
		}
		return columnNames;
	}

	private String getTableColumnsQuery( String database, String tableName )
	{
		StringBuilder sb = new StringBuilder();
		sb.append( " select   col.COLUMN_NAME, " );
		sb.append( "          col.COLUMN_ID " );
		sb.append( " from    all_tab_columns    col, " );
		sb.append( "         all_tables         tab " );
		sb.append( " where   tab.TABLE_NAME        = col.TABLE_NAME " );
		sb.append( " and     tab.OWNER             = col.OWNER" );
		sb.append( " and     tab.OWNER             = '" + database.toUpperCase() + "' " );
		if ( tableName != null && tableName.length() > 0 )
		{
			sb.append( " and     tab.TABLE_NAME       = '" + tableName.toUpperCase() + "' " );
		}
		sb.append( " union " );
		sb.append( " select  col.COLUMN_NAME, " );
		sb.append( "          col.COLUMN_ID " );
		sb.append( " from    all_tab_columns    col, " );
		sb.append( "         all_indexes        idx, " );
		sb.append( "         all_tables         tab " );
		sb.append( " where   tab.TABLE_NAME         = col.TABLE_NAME" );
		sb.append( " and     tab.TABLE_NAME         = idx.TABLE_NAME" );
		sb.append( " and     tab.OWNER              = col.OWNER " );
		sb.append( " and     tab.OWNER              = idx.OWNER " );
		sb.append( " and     tab.OWNER              = '" + database.toUpperCase() + "'" );
		if ( tableName != null && tableName.length() > 0 )
		{
			sb.append( " and     tab.TABLE_NAME       = '" + tableName.toUpperCase() + "' " );
		}
		sb.append( " and     idx.index_type         = 'IOT - TOP'" );
		sb.append( " order by 2" );
		return sb.toString();
	}

	private String getCreateIndexStatement( Index index, String indexLocation )
	{
		// For oracle we must check if the index already exists, if it does then we
		// we do not need to add it again
		// Check for existence
		String statement = "declare v_count number;" + " begin" + " select  count(1) into v_count " + " from    all_indexes" + " where   index_name  = '" + index.IndexName.toUpperCase() + "'" + " and     owner       = '" + database.toUpperCase() + "'" + " and     table_name  = '" + index.table.TableName.toUpperCase() + "';" + " if v_count = 0 then" + "    execute immediate '" + "        create" + ( index.IsUnique ? " unique" : "" ) + " index " + index.IndexName + "        on " + index.table.TableName + "(" + index.ColumnList + ") ";
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

	// bit of a hack. patches a schema up for differences in database types. eg we don't use clustered
	// indexes in oracle right now so the source schema needs to have the clustered indexes set to
	// normal indexes.
	public Schema preCompareSchemaPatch( Schema schema ) throws DataSourceException
	{
		Schema patchSchema = null;
		try
		{
			patchSchema = ( Schema ) schema.clone();
		}
		catch ( CloneNotSupportedException e )
		{
			throw new DataSourceException( e );
		}

		// make all indexes non-clustered.
		for ( Database database : patchSchema.Databases )
		{
			for ( Table table : database.Tables )
			{
				for ( Index index : table.Indexes )
				{
					if ( index.IsClustered )
					{
						index.IsClustered = false;
					}
				}
			}
		}
		return patchSchema;
	}

}
