package org.amaze.db.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.amaze.commons.exceptions.AmazeException;
//import org.amaze.commons.io.SafeBufferedWriter;
//import org.amaze.commons.utils.AmazeString;
//import org.amaze.commons.utils.StringUtils;
import org.amaze.commons.xml.XMLTransform;
//import org.amaze.database.hibernate.reference.TableColumn;
//import org.amaze.database.hibernate.reference.TableDfn;
//import org.amaze.database.hibernate.types.DateTimeType;
//import org.amaze.database.hibernate.types.DecimalType;
//import org.amaze.database.hibernate.utils.HibernateSession;
//import org.amaze.database.schema.AmazeType;
//import org.amaze.database.schema.AmazeTypeUtils;
//import org.amaze.database.schema.Column;
//import org.amaze.database.schema.Database;
//import org.amaze.database.schema.Index;
//import org.amaze.database.schema.Schema;
//import org.amaze.database.schema.Table;
//import org.amaze.database.utils.bulkloader.BulkLoadRowWriter;
//import org.amaze.database.utils.bulkloader.BulkLoadTextRowWriter;
//import org.amaze.database.utils.bulkloader.RowSource;
//import org.amaze.database.utils.exceptions.DataSourceException;
//import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;

public abstract class AbstractDataSource implements DataSource
{
	public static final Logger logger = LogManager.getLogger( AbstractDataSource.class );
	protected static XMLTransform transform = new XMLTransform();

//	protected String username;
//	protected String password;
//	protected String url;
//	protected String driverName;
//	protected String database;
//	protected Schema schema;
//	protected boolean isUnicode;
//	
//	protected BasicDataSource connectionPool = null;
//
//	protected AbstractDataSource( String userName, String password, String url, String driverName, String database, Schema schema, Boolean isUnicode )
//	{
//		this.username = userName;
//		this.password = password;
//		this.url = url;
//		this.driverName = driverName;
//		this.schema = schema;
//		this.database = database;
//		this.isUnicode = isUnicode;
//		connectionPool = new BasicDataSource();
//		try
//		{
//			Class.forName( driverName );
//		}
//		catch ( ClassNotFoundException e )
//		{
//			throw new AmazeException( e );
//		}
//		connectionPool.setDriverClassName( driverName );
//		connectionPool.setUrl( url );
//		connectionPool.setUsername( username );
//		connectionPool.setPassword( password );
//	}
//
//	@Override
//	public Connection getConnection()
//	{
//		try
//		{
//			return connectionPool.getConnection();
//		}
//		catch ( SQLException e )
//		{
//			throw new DataSourceException( e );
//		}
//	}
//
//	@Override
//	public void close()
//	{
//
//	}
//
//	@Override
//	public BulkLoadRowWriter createBulkLoadRowWriter( File file, AmazeType[] amazeTypes )
//	{
//		Writer writer = null;
//		try
//		{
//			writer = new SafeBufferedWriter( new OutputStreamWriter( new FileOutputStream( file ) ) );
//		}
//		catch ( IOException e )
//		{
//			throw new AmazeException( e );
//		}
//		return new BulkLoadTextRowWriter( amazeTypes, writer, DataSource.BCP_FIELD_DELIMITER, DataSource.NEW_LINE, getBcpDateTimeFormatter() );
//	}
//
//	@Override
//	public BulkLoadRowWriter createBulkLoadRowWriter( File file, AmazeType[] amazeTypes, boolean append )
//	{
//		Writer writer;
//		try
//		{
//			writer = new SafeBufferedWriter( new OutputStreamWriter( new FileOutputStream( file, append ) ) );
//		}
//		catch ( IOException e )
//		{
//			throw new AmazeException( e );
//		}
//		return new BulkLoadTextRowWriter( amazeTypes, writer, DataSource.BCP_FIELD_DELIMITER, DataSource.NEW_LINE, getBcpDateTimeFormatter() );
//	}
//
//	@Override
//	public List<String> getTableNames( String database ) throws DataSourceException
//	{
//		return getTableNames( database, "%" );
//	}
//
//	@Override
//	public List<String> getTableNames( String database, String tablePattern ) throws DataSourceException
//	{
//		String query = getTableQuery( database, tablePattern, false );
//
//		Connection connection = null;
//		List<String> tableNames = null;
//		try
//		{
//			connection = getConnection();
//			Statement stmt = null;
//			ResultSet rs = null;
//			try
//			{
//				stmt = connection.createStatement();
//				rs = stmt.executeQuery( query );
//				tableNames = new ArrayList<String>();
//
//				while ( rs.next() )
//				{
//					String tableName = rs.getString( 1 );
//					tableNames.add( tableName );
//				}
//			}
//			finally
//			{
//				try
//				{
//					if ( rs != null )
//						rs.close();
//					if ( stmt != null )
//						stmt.close();
//				}
//				catch ( SQLException e )
//				{
//					logger.error( "Failed to clean up statement/result set", e );
//				}
//			}
//		}
//		catch ( SQLException e )
//		{
//			throw new DataSourceException( e );
//		}
//		finally
//		{
//			if ( connection != null )
//			{
//				try
//				{
//					connection.close();
//				}
//				catch ( Exception e )
//				{
//
//				}
//			}
//		}
//		return tableNames;
//	}
//
//	@Override
//	public void createTable( Table table, String dataLocation, String indexLocation ) throws DataSourceException
//	{
//		createTable( table, dataLocation, indexLocation, true );
//	}
//
//	public void createTable( Table table, String dataLocation, String indexLocation, boolean createIndexes ) throws DataSourceException
//	{
//		executeList( getCreateTableDDL( table, dataLocation ) );
//		if ( createIndexes )
//			applyAfterTableCreateIndexes( table, dataLocation, indexLocation );
//	}
//
//	@Override
//	public void dropTable( Table table ) throws DataSourceException
//	{
//		executeList( getDropTableDDL( table ) );
//	}
//
//	@Override
//	public void dropTableSilent( Table table )
//	{
//		try
//		{
//			dropTable( table );
//		}
//		catch ( DataSourceException e )
//		{
//
//		}
//	}
//
//	@Override
//	public void dropTableSilentInSameSession( Table table, Connection jdbcSession )
//	{
//		dropTableSilent( table );
//	}
//
//	@Override
//	public void recreateTable( Table table, String dataLocation, String indexLocation ) throws DataSourceException
//	{
//		dropTableSilent( table );
//		createTable( table, dataLocation, indexLocation );
//	}
//
//	@Override
//	public void dropTableWarn( Table table )
//	{
//		try
//		{
//			dropTable( table );
//		}
//		catch ( DataSourceException e )
//		{
//			logger.warn( AmazeString.create( "Failed to drop table '%1'", table.TableName ), e );
//		}
//	}
//
//	@Override
//	public void updateTable( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
//	{
//		List<String> sqlList = new ArrayList<String>();
//		String statement;
//		statement = getSelectDatabaseDDL( oldTable.Database.DatabaseName );
//		if ( statement != null && statement.length() > 0 )
//			sqlList.add( statement );
//		TableDfn tableDefn = null;
//		try
//		{
//			tableDefn = HibernateSession.queryExpectOneRow( "select tin.TableDfn from TableInst tin where tin.TinTableName = :tableName", "tableName", oldTable.TableName );
//		}
//		catch ( HibernateException e1 )
//		{
//
//		}
//		if ( oldTable.Columns.size() < newTable.Columns.size() && null != tableDefn && !tableDefn.getTbdSystemFl() )
//		{
//			List<String> alterSqlList = getAlterTableDDL( oldTable, newTable, dataLocation );
//			if ( alterSqlList != null )
//			{
//				sqlList.addAll( alterSqlList );
//				executeList( sqlList );
//				return;
//			}
//		}
//		Table migrationTable = null;
//		try
//		{
//			migrationTable = newTable.clone();
//		}
//		catch ( CloneNotSupportedException e )
//		{
//			throw new DataSourceException( "Clone of table failed", e );
//		}
//
//		migrationTable.TableName = getMigrationName( newTable );
//		if ( newTable.Database.findTable( migrationTable.TableName ) != null )
//			throw new DataSourceException( "The schema already contains the migration table '%1'", migrationTable.TableName );
//		newTable.Database.Tables.add( migrationTable );
//		migrationTable.Database = newTable.Database;
//		List<String> sql = getCreateTableDDL( migrationTable, dataLocation );
//		sqlList.addAll( sql );
//		executeList( sqlList );
//		sqlList.clear();
//		StringBuilder migrationTableSQ = new StringBuilder();
//		migrationTableSQ.append( "select " );
//		for ( int i = 0; i < newTable.Columns.size(); i++ )
//		{
//			Column newColumn = newTable.Columns.get( i );
//			String selectColumn = "";
//			if ( oldTable.findColumn( newColumn.ColumnName ) != null )
//			{
//				Column oldColumn = oldTable.findColumn( newColumn.ColumnName );
//				if ( ( oldColumn.IsMandatory == newColumn.IsMandatory ) || ( !newColumn.IsMandatory ) )
//				{
//					selectColumn = newColumn.ColumnName;
//				}
//				else
//				{
//					selectColumn = getFunctionName( "isnull" ) + "(" + newColumn.ColumnName + ", " + getDefaultConstant( newColumn ) + ")";
//				}
//				if ( !oldColumn.DataType.equals( newColumn.DataType ) )
//				{
//					selectColumn = getSqlConvert( newColumn.DataType, newColumn.Length, oldColumn );
//				}
//			}
//			else
//			{
//				if ( newColumn.IsMandatory )
//				{
//					selectColumn = getDefaultConstant( newColumn );
//				}
//				else
//				{
//					selectColumn = "NULL";
//				}
//			}
//			migrationTableSQ.append( selectColumn + ", " );
//		}
//		if ( newTable.Columns.size() > 0 )
//			migrationTableSQ.delete( migrationTableSQ.length() - 2, migrationTableSQ.length() );
//		migrationTableSQ.append( " from " + oldTable.TableName );
//		String migrationTableIQ = "insert into " + migrationTable.TableName + "(" + StringUtils.merge( migrationTable.getColumnNames(), "," ) + ")" + StringUtils.NEW_LINE + migrationTableSQ.toString();
//		sqlList.add( migrationTableIQ );
//		try
//		{
//			executeList( sqlList );
//		}
//		catch ( DataSourceException e )
//		{
//			List<String> dropDDL = getDropTableDDL( migrationTable );
//			executeList( dropDDL );
//			throw e;
//		}
//		sqlList.clear();
//		sqlList.addAll( getDropTableDDL( oldTable, false ) );
//		executeList( sqlList );
//		migrationTable.Database.removeTable( migrationTable.TableName );
//		execute( getRenameTableDDL( migrationTable.TableName, newTable.TableName ) );
//	}
//
//	@Override
//	public void updateIndex( Index oldIndex, Index newIndex, String indexLocation ) throws DataSourceException
//	{
//		List<String> sqlList = new ArrayList<String>();
//		if ( oldIndex == null )
//		{
//			sqlList.addAll( getCreateIndexDDL( newIndex, indexLocation ) );
//		}
//		else if ( newIndex == null )
//		{
//			sqlList.addAll( getDropIndexDDL( oldIndex, true ) );
//		}
//		else
//		{
//			sqlList.addAll( getDropIndexDDL( oldIndex, true ) );
//			sqlList.addAll( getCreateIndexDDL( newIndex, indexLocation ) );
//		}
//		executeList( sqlList );
//	}
//
//	@Override
//	public void renameTable( String oldTableName, String newTableName ) throws DataSourceException
//	{
//		execute( getRenameTableDDL( oldTableName, newTableName ) );
//	}
//
//	@Override
//	public void copyTable( Table oldTable, String newTableName ) throws DataSourceException
//	{
//		try
//		{
//			Table newTable = oldTable.clone();
//			newTable.TableName = newTableName;
//			newTable.Indexes.clear();
//			createTable( newTable, null, null );
//			execute( "insert into " + newTableName + " select * from " + oldTable.TableName );
//		}
//		catch ( CloneNotSupportedException e )
//		{
//			throw new DataSourceException( e );
//		}
//	}
//
//	@Override
//	public boolean tableExists( String tableName ) throws DataSourceException
//	{
//		List<Object[]> results = executeQuery( getTableQuery( database, tableName, true ), new AmazeType[]
//		{ AmazeType.String } );
//		return results.size() > 0;
//	}
//
//	public void dropAllIndexes( List<Index> indexes ) throws DataSourceException
//	{
//		for ( Index index : indexes )
//		{
//			if ( !index.IsClustered )
//			{
//				List<String> sqlList = getDropIndexDDL( index );
//				executeList( sqlList );
//			}
//		}
//		for ( Index index : indexes )
//		{
//			if ( index.IsClustered )
//			{
//				List<String> sqlList = getDropIndexDDL( index );
//				executeList( sqlList );
//				break;
//			}
//		}
//	}
//
//	@Override
//	public void dropAllIndexes( Table table ) throws DataSourceException
//	{
//		dropAllIndexes( table.Indexes );
//	}
//
//	@Override
//	public void dropAllIndexes() throws DataSourceException
//	{
//		Schema currentSchema = getSchema( this.database );
//		Database database = currentSchema.findDatabase( this.database );
//		for ( Table table : database.Tables )
//		{
//			List<Index> indexesToDrop = new ArrayList<Index>();
//			for ( Index index : table.Indexes )
//			{
//				indexesToDrop.add( index );
//			}
//			dropAllIndexes( indexesToDrop );
//		}
//	}
//
//	@Override
//	public void applyIndexes( Table table, String indexLocation ) throws DataSourceException
//	{
//		applyIndexes( table, indexLocation, true );
//	}
//
//	@Override
//	public void applyIndexes( String indexLocation ) throws DataSourceException
//	{
//		Database database = schema.findDatabase( this.database );
//		Schema currentSchema = getSchema( this.database );
//		for ( Table table : database.Tables )
//		{
//			Table currentTable = currentSchema.findTable( this.database, table.TableName );
//			if ( currentTable == null )
//				continue;
//			List<Index> indexesToApply = new ArrayList<Index>();
//			for ( Index index : table.Indexes )
//			{
//				Index currentIndex = currentTable.findIndex( index.IndexName );
//				if ( currentIndex == null )
//				{
//					indexesToApply.add( index );
//				}
//			}
//			applyIndexes( indexesToApply, indexLocation, true );
//		}
//	}
//
//	public void applyIndexes( List<Index> indexes, String indexLocation, boolean applyClustered ) throws DataSourceException
//	{
//		if ( applyClustered )
//		{
//			for ( Index index : indexes )
//			{
//				if ( index.IsClustered )
//				{
//					applyIndex( index, indexLocation );
//					break;
//				}
//			}
//		}
//		for ( Index index : indexes )
//		{
//			if ( !index.IsClustered )
//			{
//				applyIndex( index, indexLocation );
//			}
//		}
//	}
//
//	@Override
//	public void applyIndex( Index index, String indexLocation ) throws DataSourceException
//	{
//		List<String> sqlList = getCreateIndexDDL( index, indexLocation );
//		executeList( sqlList );
//	}
//
//	public void applyIndexes( Table table, String indexLocation, boolean applyClustered ) throws DataSourceException
//	{
//		applyIndexes( table.Indexes, indexLocation, applyClustered );
//	}
//
//	@Override
//	public void updateNextNumber( Table table, String objectName ) throws DataSourceException
//	{
//		updateNextNumber( table.TableName, table.getPrimaryKeyColumn().ColumnName, objectName );
//	}
//
//	@Override
//	public void updateNextNumber( String tableName, String primaryKeyColumn, String objectName ) throws DataSourceException
//	{
//		String selectMax = "select max( " + primaryKeyColumn + " ) from " + tableName;
//		List<Object[]> tableMax = executeQuery( selectMax, new AmazeType[]
//		{ AmazeType.Int } );
//		Object maxValue = tableMax.get( 0 )[0];
//		if ( maxValue == null )
//		{
//			maxValue = new Integer( 0 );
//		}
//		String update = "update next_object_id set noi_current_no = " + maxValue + " where noi_object_name = '" + objectName + "'";
//		int updateCount = execute( update );
//		if ( updateCount < 1 )
//		{
//			String insert = "insert into next_object_id ( noi_current_no, noi_object_name ) values ( " + maxValue + ", '" + objectName + "' )";
//			execute( insert );
//		}
//	}
//
//	@Override
//	public void updateAllNextNumber() throws DataSourceException
//	{
//		for ( Database db : schema.Databases )
//		{
//			for ( Table tab : db.Tables )
//			{
//				Table table = schema.findTable( db.DatabaseName, tab.TableName );
//				updateNextNumber( table, table.TableName );
//			}
//		}
//	}
//
//	@Override
//	public String getDatabase()
//	{
//		return database;
//	}
//
//	@Override
//	public void attachSchema( Schema schema )
//	{
//		this.schema = schema;
//	}
//
//	@Override
//	public Schema getSchema()
//	{
//		return schema;
//	}
//
//	@Override
//	public Schema getSchema( String database ) throws DataSourceException
//	{
//		return getSchema( database, null );
//	}
//
//	public Schema getSchema( String database, String table ) throws DataSourceException
//	{
//		Schema dbSchema = new Schema();
//		Database dbDatabase = new Database();
//		dbDatabase.DatabaseName = database;
//		dbSchema.Databases.add( dbDatabase );
//		List<Object[]> result = executeQuery( getAllTableDetailsQuery( database, table ), new AmazeType[]
//		{ AmazeType.String, AmazeType.String, AmazeType.String, AmazeType.Int, AmazeType.Bool, AmazeType.Int } );
//		Table newTable = null;
//		boolean invalidTable = false;
//		for ( Object[] row : result )
//		{
//			String tableName = ( String ) row[0];
//			String columnName = ( String ) row[1];
//			String dataType = ( String ) row[2];
//			Integer length = ( Integer ) row[3];
//			Boolean isNullable = ( Boolean ) row[4];
//			Integer sequenceNumber = ( Integer ) row[5];
//			if ( newTable == null || !newTable.TableName.equals( tableName ) )
//			{
//				if ( newTable != null )
//				{
//					if ( invalidTable )
//					{
//						logger.warn( AmazeString.create( "Skipping table '%1' with invalid datatypes", newTable.TableName ) );
//					}
//					else
//					{
//						dbDatabase.Tables.add( newTable );
//					}
//				}
//				invalidTable = false;
//				newTable = new Table();
//				newTable.TableName = tableName;
//				newTable.Database = dbDatabase;
//			}
//			Column column = new Column();
//			column.ColumnName = columnName;
//			if ( length != null )
//				column.Length = length;
//			else
//				column.Length = -1;
//			column.DataType = dbTypeToAmazeType( dataType, column.Length );
//			if ( column.DataType == null )
//				invalidTable = true;
//			column.IsMandatory = !isNullable;
//			column.SequenceNumber = sequenceNumber;
//			column.table = newTable;
//			newTable.Columns.add( column );
//		}
//		if ( newTable != null )
//		{
//			if ( invalidTable )
//			{
//				logger.warn( AmazeString.create( "Skipping table '%1' with invalid datatypes", newTable.TableName ) );
//			}
//			else
//			{
//				dbDatabase.Tables.add( newTable );
//			}
//		}
//		result = executeQuery( getAllIndexDetailsQuery( database, table ), new AmazeType[]
//		{ AmazeType.String, AmazeType.String, AmazeType.String, AmazeType.Bool, AmazeType.Bool, AmazeType.Bool, AmazeType.Int } );
//		Index index = null;
//		for ( Object[] row : result )
//		{
//			String tableName = ( String ) row[0];
//			String indexName = ( String ) row[1];
//			String columnName = ( String ) row[2];
//			Boolean isClustered = ( Boolean ) row[3];
//			Boolean isUnique = ( Boolean ) row[4];
//			Table currentTable = dbDatabase.findTable( tableName );
//			if ( currentTable == null )
//				continue;
//			if ( index == null || !index.IndexName.equals( indexName ) || !index.table.TableName.equals( currentTable.TableName ) )
//			{
//				index = new Index();
//				index.IndexName = indexName;
//				index.IsClustered = isClustered;
//				index.IsUnique = isUnique;
//				index.table = currentTable;
//				currentTable.Indexes.add( index );
//			}
//			index.addColumn( columnName );
//		}
//		return dbSchema;
//	}
//
//	@Override
//	public String getUnicodeCharset()
//	{
//		String unicodeCharset;
//		if ( ByteOrder.nativeOrder().equals( ByteOrder.LITTLE_ENDIAN ) )
//		{
//			unicodeCharset = "UTF-16LE";
//		}
//		else
//		{
//			unicodeCharset = "UTF-16BE";
//		}
//		return unicodeCharset;
//	}
//
//	public static String[] getTableColumnNames( Table table )
//	{
//		String[] names = new String[table.Columns.size()];
//		for ( int i = 0; i < table.Columns.size(); i++ )
//		{
//			names[i] = table.Columns.get( i ).ColumnName;
//		}
//		return names;
//	}
//
//	public List<String> getDropTableDDL( Table table ) throws DataSourceException
//	{
//		return getDropTableDDL( table, true );
//	}
//
//	public List<String> getDropIndexDDL( Index index ) throws DataSourceException
//	{
//		return getDropIndexDDL( index, true );
//	}
//
//	protected List<Object[]> executeQuery( String query, AmazeType[] resultTypes ) throws DataSourceException
//	{
//		List<Object[]> result = null;
//		Connection conn = null;
//		try
//		{
//			conn = getConnection();
//			result = executeQuery( conn, query, resultTypes );
//		}
//		finally
//		{
//			if ( conn != null )
//			{
//				try
//				{
//					conn.close();
//				}
//				catch ( Exception e )
//				{
//
//				}
//			}
//		}
//		return result;
//	}
//
//	protected List<Object[]> executeQuery( Connection conn, String query, AmazeType[] resultTypes ) throws DataSourceException
//	{
//		List<Object[]> result = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		try
//		{
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery( query );
//			result = unwrapResultSet( rs, resultTypes );
//			conn.commit();
//		}
//		catch ( SQLException e )
//		{
//			try
//			{
//				conn.rollback();
//			}
//			catch ( SQLException e1 )
//			{
//				throw new DataSourceException( e1 );
//			}
//			throw new DataSourceException( e );
//		}
//		finally
//		{
//			if ( stmt != null )
//			{
//				try
//				{
//					stmt.close();
//				}
//				catch ( SQLException e )
//				{
//
//				}
//			}
//		}
//		return result;
//	}
//
//	private List<Object[]> unwrapResultSet( ResultSet rs, AmazeType[] resultTypes )
//	{
//		List<Object[]> results = new ArrayList<Object[]>();
//		try
//		{
//			RowSource<Object[]> rowSource = new ResultSetRowSource( rs, resultTypes );
//
//			while ( rowSource.next() )
//			{
//				results.add( rowSource.get() );
//			}
//		}
//		finally
//		{
//			try
//			{
//				rs.close();
//			}
//			catch ( SQLException e )
//			{
//				logger.error( "Failed to close result set", e );
//			}
//		}
//		return results;
//	}
//
//	public static Object[] unwrapResultSetRow( ResultSet rs, AmazeType[] resultTypes ) throws SQLException
//	{
//		Object[] row = new Object[resultTypes.length];
//		for ( int i = 0; i < row.length; i++ )
//		{
//			AmazeType amazeType = resultTypes[i];
//			switch( amazeType )
//			{
//			case Long:
//				row[i] = rs.getLong( i + 1 );
//				if ( rs.wasNull() )
//					row[i] = null;
//				break;
//			case Int:
//				row[i] = rs.getInt( i + 1 );
//				if ( rs.wasNull() )
//					row[i] = null;
//				break;
//			case String:
//				row[i] = rs.getString( i + 1 );
//				break;
//			case DateTime:
//				row[i] = DateTimeType.nullSafeGet( rs, i + 1 );
//				break;
//			case Bool:
//				row[i] = ( rs.getString( i + 1 ) == null ? null : ( Object ) yesNoToBoolean( rs.getString( i + 1 ) ) );
//				break;
//			case Decimal:
//				row[i] = DecimalType.nullSafeGet( rs, i + 1 );
//				break;
//			default:
//				throw new AmazeException( "Invalid amaze type '%1'", amazeType );
//			}
//		}
//		return row;
//	}
//
//	public static boolean yesNoToBoolean( String value )
//	{
//		return value.equals( "Y" );
//	}
//
//	protected int execute( String query ) throws DataSourceException
//	{
//		int result = -1;
//		Connection conn = null;
//		try
//		{
//			conn = getConnection();
//			Statement stmt = null;
//			try
//			{
//				stmt = conn.createStatement();
//				if ( !stmt.execute( query ) )
//				{
//					result = stmt.getUpdateCount();
//				}
//				conn.commit();
//			}
//			catch ( SQLException e )
//			{
//				try
//				{
//					conn.rollback();
//				}
//				catch ( SQLException e1 )
//				{
//					throw new DataSourceException( e1 );
//				}
//				throw new DataSourceException( e );
//			}
//			finally
//			{
//				if ( stmt != null )
//				{
//					try
//					{
//						stmt.close();
//					}
//					catch ( SQLException e )
//					{
//
//					}
//				}
//			}
//		}
//		finally
//		{
//			if ( conn != null )
//			{
//				try
//				{
//					conn.close();
//				}
//				catch ( Exception e )
//				{
//
//				}
//			}
//		}
//		return result;
//	}
//
//	public int executeUpdate( String query, Object... params ) throws DataSourceException
//	{
//		int result = -1;
//		Connection conn = null;
//		try
//		{
//			conn = getConnection();
//			PreparedStatement stmt = null;
//			try
//			{
//				stmt = conn.prepareStatement( query );
//				int index = 1;
//				for ( Object param : params )
//				{
//					stmt.setObject( index, param );
//					index++;
//				}
//				result = stmt.getUpdateCount();
//				conn.commit();
//			}
//			catch ( SQLException e )
//			{
//				try
//				{
//					conn.rollback();
//				}
//				catch ( SQLException e1 )
//				{
//					throw new DataSourceException( e1 );
//				}
//				throw new DataSourceException( e );
//			}
//			finally
//			{
//				if ( stmt != null )
//				{
//					try
//					{
//						stmt.close();
//					}
//					catch ( SQLException e )
//					{
//
//					}
//				}
//			}
//		}
//		finally
//		{
//			if ( conn != null )
//			{
//				try
//				{
//					conn.close();
//				}
//				catch ( Exception e )
//				{
//
//				}
//			}
//		}
//		return result;
//	}
//
//	public int[] executeList( List<String> sqlList ) throws DataSourceException
//	{
//		int[] results = null;
//		Connection conn = null;
//		try
//		{
//			conn = getConnection();
//			Statement stmt = null;
//			try
//			{
//				stmt = conn.createStatement();
//				for ( String sql : sqlList )
//				{
//					stmt.addBatch( sql );
//				}
//				results = stmt.executeBatch();
//				conn.commit();
//			}
//			catch ( SQLException e )
//			{
//				try
//				{
//					conn.rollback();
//				}
//				catch ( SQLException e1 )
//				{
//					throw new DataSourceException( e1 );
//				}
//				throw new DataSourceException( "Error executing sql: %1", e, sqlList.toString() );
//			}
//			finally
//			{
//				if ( stmt != null )
//					try
//					{
//						stmt.close();
//					}
//					catch ( SQLException e )
//					{
//						throw new DataSourceException( e );
//					}
//			}
//		}
//		finally
//		{
//			if ( conn != null )
//			{
//				try
//				{
//					conn.close();
//				}
//				catch ( Exception e )
//				{
//
//				}
//			}
//		}
//		return results;
//	}
//
//	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max )
//	{
//		return "SELECT " + StringUtils.merge( colNames, ", " ) + " FROM ( SELECT " + tableName + ".*, ROW_NUMBER() OVER (ORDER BY " + orderByClause + ") AS ROWNO FROM " + tableName + ") TMP WHERE TMP.ROWNO between " + min + " and " + max;
//	}
//
//	protected Table createTempTable( Table table ) throws DataSourceException
//	{
//		Table migrationTable = null;
//		try
//		{
//			migrationTable = table.clone();
//		}
//		catch ( CloneNotSupportedException e )
//		{
//			throw new DataSourceException( "Clone of table failed", e );
//		}
//		migrationTable.TableName = getMigrationName( table );
//		if ( table.Database.findTable( migrationTable.TableName ) != null )
//			throw new DataSourceException( "The schema already contains the migration table '%1'", migrationTable.TableName );
//		return migrationTable;
//	}
//
//	public static AmazeType[] getTableColumnTypes( Table table )
//	{
//		AmazeType[] resultTypes = new AmazeType[table.Columns.size()];
//		for ( int i = 0; i < table.Columns.size(); i++ )
//		{
//			resultTypes[i] = table.Columns.get( i ).DataType;
//		}
//		return resultTypes;
//	}
//
//	public static AmazeType[] getTableColumnTypes( int tbdId )
//	{
//		List<TableColumn> tableColumns = null;
//		try
//		{
//			tableColumns = HibernateSession.query( " from TableColumn tcl " + " where tcl.TbdId = :tbdId " + " order by tcl.TclOrderNo ", "tbdId", tbdId );
//		}
//		catch ( HibernateException e )
//		{
//			throw new AmazeException( e );
//		}
//		AmazeType[] resultTypes = new AmazeType[tableColumns.size()];
//		for ( int i = 0; i < tableColumns.size(); i++ )
//		{
//			resultTypes[i] = AmazeTypeUtils.typeStringToAmazeType( tableColumns.get( i ).getTclType() );
//		}
//		return resultTypes;
//	}
//	
//	protected List< String > filterBCPOutput( String output )
//    {
//        String[] lines = StringUtils.split( output, StringUtils.NEW_LINE );
//        List< String > outputLines = new ArrayList< String >();
//        for ( String line : lines )
//        {
//            if ( line.trim().length() == 0 )
//                continue;
//            outputLines.add( line );
//        }
//        return outputLines;
//    }
//
//	protected abstract String getTableQuery( String database, String tablePattern, boolean exact );
//
//	protected abstract AmazeType dbTypeToAmazeType( String dbType, int length ) throws DataSourceException;
//
//	protected abstract String amazeTypeToDbType( AmazeType sparkType, int length ) throws DataSourceException;
//
//	protected abstract String getAllIndexDetailsQuery( String database, String tableName, boolean isViewRequired ) throws DataSourceException;
//
//	protected abstract String getAllIndexDetailsQuery( String database, String tableName ) throws DataSourceException;
//
//	protected abstract String getAllTableDetailsQuery( String database, String tableName, boolean isViewRequired ) throws DataSourceException;
//
//	protected abstract String getAllTableDetailsQuery( String database, String tableName ) throws DataSourceException;
//
//	protected abstract List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException;
//
//	protected abstract List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException;
//
//	protected abstract List<String> getCreateIndexDDL( Index index, String indexLocation ) throws DataSourceException;
//
//	protected abstract List<String> getDropIndexDDL( Index index, boolean checkExists ) throws DataSourceException;
//
//	protected abstract List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException;
//
//	protected abstract List<String> getUpdateTableDDL( Table oldTable, Table newTable, String dataLocation, String indexLocation ) throws DataSourceException;
//
//	protected abstract String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException;
//
//	protected abstract String getSelectDatabaseDDL( String databaseName ) throws DataSourceException;
//
//	protected abstract void applyAfterTableCreateIndexes( Table table, String dataLocation, String indexLocation ) throws DataSourceException;
//
//	protected abstract String getSqlConvert( AmazeType to, int length, Column from ) throws DataSourceException;
//
//	public abstract String getFunctionName( String functionName ) throws DataSourceException;
//
//	protected abstract String getMigrationName( Table table ) throws DataSourceException;
//
//	protected abstract String getDefaultConstant( Column column ) throws DataSourceException;
//
//	public abstract String getSelectString( String tableName, String[] colNames );
//
//	public abstract AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException;
}
