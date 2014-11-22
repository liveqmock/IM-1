package org.amaze.db.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.commons.utils.StringUtils;
import org.amaze.commons.xml.XMLTransform;
import org.amaze.db.hibernate.objects.Columns;
import org.amaze.db.hibernate.objects.Datasource;
import org.amaze.db.hibernate.objects.Indexes;
import org.amaze.db.hibernate.objects.TableType;
import org.amaze.db.hibernate.objects.Tables;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.installer.exceptions.AmazeInstallerException;
import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Schema;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.dom4j.Document;

public abstract class AbstractDataSource implements DataSource, ApplicationContextAware
{
	public static final Logger logger = LogManager.getLogger( AbstractDataSource.class );
	protected static XMLTransform transform = new XMLTransform();

	public String dataBase; //Amaze
	public String databaseType; //Oracle
	public String dataSourceName; //Ref

	public SessionFactory sessionFactory;
	public javax.sql.DataSource dataSource;
	public Connection connection;

	public Schema schema;

	public ApplicationContext context;

	@Override
	public void setApplicationContext( ApplicationContext context ) throws BeansException
	{
		this.context = context;
	}

	public AbstractDataSource( String dataBase, String databaseType, String dataSourceName, SessionFactory sessionFactory, javax.sql.DataSource dataSource, Connection connection )
	{
		this.dataBase = dataBase;
		this.databaseType = databaseType;
		this.dataSourceName = dataSourceName;
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
		this.connection = connection;
	}

	public String getDatabase()
	{
		return dataBase;
	}

	public String getDatabaseType()
	{
		return databaseType;
	}

	public String getDataSourceName()
	{
		return dataSourceName;
	}

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public javax.sql.DataSource getDataSource()
	{
		return dataSource;
	}

	public Connection getConnection()
	{
		try
		{
			return dataSource.getConnection();
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
	}

	@Override
	public void close()
	{
		try
		{
			connection.close();
		}
		catch ( SQLException e )
		{

		}
	}

	protected abstract String getTableQuery( String database, String tablePattern, boolean exact );

	@Override
	public List<String> getTableNames( String database ) throws DataSourceException
	{
		return getTableNames( database, "%" );
	}

	@Override
	public List<String> getTableNames( String database, String tablePattern ) throws DataSourceException
	{
		String query = getTableQuery( database, tablePattern, false );
		Connection connection = null;
		List<String> tableNames = null;
		try
		{
			connection = getConnection();
			Statement stmt = null;
			ResultSet rs = null;
			try
			{
				stmt = connection.createStatement();
				rs = stmt.executeQuery( query );
				tableNames = new ArrayList<String>();

				while ( rs.next() )
				{
					String tableName = rs.getString( 1 );
					tableNames.add( tableName );
				}
			}
			finally
			{
				try
				{
					if ( rs != null )
						rs.close();
					if ( stmt != null )
						stmt.close();
				}
				catch ( SQLException e )
				{
					logger.error( "Failed to clean up statement/result set", e );
				}
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
		return tableNames;
	}

	protected int execute( String query ) throws DataSourceException
	{
		int result = -1;
		Connection conn = null;
		try
		{
			conn = getConnection();
			Statement stmt = null;
			try
			{
				stmt = conn.createStatement();
				if ( !stmt.execute( query ) )
				{
					result = stmt.getUpdateCount();
				}
				conn.commit();
			}
			catch ( SQLException e )
			{
				try
				{
					conn.rollback();
				}
				catch ( SQLException e1 )
				{
					throw new DataSourceException( e1 );
				}
				throw new DataSourceException( e );
			}
			finally
			{
				if ( stmt != null )
				{
					try
					{
						stmt.close();
					}
					catch ( SQLException e )
					{

					}
				}
			}
		}
		finally
		{
			if ( conn != null )
			{
				try
				{
					conn.close();
				}
				catch ( Exception e )
				{

				}
			}
		}
		return result;
	}

	public int[] executeList( List<String> sqlList ) throws DataSourceException
	{
		int[] results = null;
		Connection conn = null;
		try
		{
			conn = getConnection();
			Statement stmt = null;
			try
			{
				stmt = conn.createStatement();
				for ( String sql : sqlList )
				{
					stmt.addBatch( sql );
				}
				results = stmt.executeBatch();
				conn.commit();
			}
			catch ( SQLException e )
			{
				try
				{
					conn.rollback();
				}
				catch ( SQLException e1 )
				{
					throw new DataSourceException( e1 );
				}
				throw new DataSourceException( "Error executing sql: %1", e, sqlList.toString() );
			}
			finally
			{
				if ( stmt != null )
					try
					{
						stmt.close();
					}
					catch ( SQLException e )
					{
						throw new DataSourceException( e );
					}
			}
		}
		finally
		{
			if ( conn != null )
			{
				try
				{
					conn.close();
				}
				catch ( Exception e )
				{

				}
			}
		}
		return results;
	}

	public void createTable( Table table, String dataLocation, String indexLocation, boolean createIndexes ) throws DataSourceException
	{
		executeList( getCreateTableDDL( table, dataLocation ) );
		if ( createIndexes )
			applyAfterTableCreateIndexes( table, dataLocation, indexLocation );
	}

	protected abstract String getDefaultConstant( Column column ) throws DataSourceException;

	protected abstract String amazeTypeToDbType( AmazeType amazeType, int length ) throws DataSourceException;

	protected abstract List<String> getCreateTableDDL( Table table, String dataLocation ) throws DataSourceException;

	protected void applyAfterTableCreateIndexes( Table table, String dataLocation, String indexLocation ) throws DataSourceException
	{
		applyIndexes( table, indexLocation, false );
	}

	protected List<String> getCreateIndexDDL( Index index, String indexLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		sqlList.add( getCreateIndexStatement( index, indexLocation ) );
		return sqlList;
	}

	@Override
	public void applyIndexes( Table table, String indexLocation ) throws DataSourceException
	{
		applyIndexes( table, indexLocation, true );
	}

	public void applyIndexes( List<Index> indexes, String indexLocation, boolean applyClustered ) throws DataSourceException
	{
		if ( applyClustered )
		{
			for ( Index index : indexes )
			{
				if ( index.IsClustered )
				{
					applyIndex( index, indexLocation );
					break;
				}
			}
		}
		for ( Index index : indexes )
		{
			if ( !index.IsClustered )
			{
				applyIndex( index, indexLocation );
			}
		}
	}

	@Override
	public void applyIndex( Index index, String indexLocation ) throws DataSourceException
	{
		List<String> sqlList = getCreateIndexDDL( index, indexLocation );
		executeList( sqlList );
	}

	protected abstract String getCreateIndexStatement( Index index, String indexLocation );

	@Override
	public void dropTable( Table table ) throws DataSourceException
	{
		executeList( getDropTableDDL( table, true ) );
	}

	protected abstract List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException;

	@Override
	public void recreateTable( Table table, String dataLocation, String indexLocation, Boolean createIndexes ) throws DataSourceException
	{
		dropTable( table );
		createTable( table, dataLocation, indexLocation, createIndexes );
	}

	@Override
	public void updateTable( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		String statement;
		statement = getSelectDatabaseDDL( oldTable.Database.DatabaseName );
		if ( statement != null && statement.length() > 0 )
			sqlList.add( statement );
		if ( oldTable.Columns.size() < newTable.Columns.size() )
		{
			List<String> alterSqlList = getAlterTableDDL( oldTable, newTable, dataLocation );
			if ( alterSqlList != null )
			{
				sqlList.addAll( alterSqlList );
				executeList( sqlList );
				return;
			}
		}
		Table migrationTable = null;
		try
		{
			migrationTable = newTable.clone();
		}
		catch ( CloneNotSupportedException e )
		{
			throw new DataSourceException( "Clone of table failed", e );
		}

		migrationTable.TableName = getMigrationName( newTable );
		if ( newTable.Database.findTable( migrationTable.TableName ) != null )
			throw new DataSourceException( "The schema already contains the migration table '%1'", migrationTable.TableName );
		newTable.Database.Tables.add( migrationTable );
		migrationTable.Database = newTable.Database;
		List<String> sql = getCreateTableDDL( migrationTable, dataLocation );
		sqlList.addAll( sql );
		executeList( sqlList );
		sqlList.clear();
		StringBuilder migrationTableSQ = new StringBuilder();
		migrationTableSQ.append( "select " );
		for ( int i = 0; i < newTable.Columns.size(); i++ )
		{
			Column newColumn = newTable.Columns.get( i );
			String selectColumn = "";
			if ( oldTable.findColumn( newColumn.ColumnName ) != null )
			{
				Column oldColumn = oldTable.findColumn( newColumn.ColumnName );
				if ( ( oldColumn.IsMandatory == newColumn.IsMandatory ) || ( !newColumn.IsMandatory ) )
				{
					selectColumn = newColumn.ColumnName;
				}
				else
				{
					selectColumn = getFunctionName( "isnull" ) + "(" + newColumn.ColumnName + ", " + getDefaultConstant( newColumn ) + ")";
				}
				if ( !oldColumn.DataType.equals( newColumn.DataType ) )
				{
					selectColumn = getSqlConvert( newColumn.DataType, newColumn.Length, oldColumn );
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
			migrationTableSQ.delete( migrationTableSQ.length() - 2, migrationTableSQ.length() );
		migrationTableSQ.append( " from " + oldTable.TableName );
		String migrationTableIQ = "insert into " + migrationTable.TableName + "(" + StringUtils.merge( migrationTable.getColumnNames(), "," ) + ")" + StringUtils.NEW_LINE + migrationTableSQ.toString();
		sqlList.add( migrationTableIQ );
		try
		{
			executeList( sqlList );
		}
		catch ( DataSourceException e )
		{
			List<String> dropDDL = getDropTableDDL( migrationTable, true );
			executeList( dropDDL );
			throw e;
		}
		sqlList.clear();
		sqlList.addAll( getDropTableDDL( oldTable, false ) );
		executeList( sqlList );
		migrationTable.Database.removeTable( migrationTable.TableName );
		execute( getRenameTableDDL( migrationTable.TableName, newTable.TableName ) );
	}

	protected abstract String getSelectDatabaseDDL( String databaseName ) throws DataSourceException;

	protected abstract List<String> getAlterTableDDL( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException;

	protected abstract String getMigrationName( Table table ) throws DataSourceException;

	protected abstract String getRenameTableDDL( String fromTable, String toTable ) throws DataSourceException;

	public abstract String getFunctionName( String functionName ) throws DataSourceException;

	protected abstract String getSqlConvert( AmazeType to, int length, Column from ) throws DataSourceException;

	@Override
	public void updateIndex( Index oldIndex, Index newIndex, String indexLocation ) throws DataSourceException
	{
		List<String> sqlList = new ArrayList<String>();
		if ( oldIndex == null )
		{
			sqlList.addAll( getCreateIndexDDL( newIndex, indexLocation ) );
		}
		else if ( newIndex == null )
		{
			sqlList.addAll( getDropIndexDDL( oldIndex, true ) );
		}
		else
		{
			sqlList.addAll( getDropIndexDDL( oldIndex, true ) );
			sqlList.addAll( getCreateIndexDDL( newIndex, indexLocation ) );
		}
		executeList( sqlList );
	}

	protected abstract List<String> getDropIndexDDL( Index index, boolean checkExists ) throws DataSourceException;

	@Override
	public void renameTable( String oldTableName, String newTableName ) throws DataSourceException
	{
		execute( getRenameTableDDL( oldTableName, newTableName ) );
	}

	@Override
	public void copyTable( Table oldTable, String newTableName ) throws DataSourceException
	{
		try
		{
			Table newTable = oldTable.clone();
			newTable.TableName = newTableName;
			newTable.Indexes.clear();
			createTable( newTable, null, null, true );
			execute( "insert into " + newTableName + " select * from " + oldTable.TableName );
		}
		catch ( CloneNotSupportedException e )
		{
			throw new DataSourceException( e );
		}
	}

	@Override
	public boolean tableExists( String tableName ) throws DataSourceException
	{
		List<Object[]> results = executeQuery( getConnection(), getTableQuery( getDatabase(), tableName, true ), new AmazeType[]
		{ AmazeType.String } );
		return results.size() > 0;
	}

	protected List<Object[]> executeQuery( Connection conn, String query, AmazeType[] resultTypes ) throws DataSourceException
	{
		List<Object[]> result = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = conn.createStatement();
			rs = stmt.executeQuery( query );
			result = unwrapResultSet( rs, resultTypes );
			conn.commit();
		}
		catch ( SQLException e )
		{
			try
			{
				conn.rollback();
			}
			catch ( SQLException e1 )
			{
				throw new DataSourceException( e1 );
			}
			throw new DataSourceException( e );
		}
		finally
		{
			if ( stmt != null )
			{
				try
				{
					stmt.close();
				}
				catch ( SQLException e )
				{

				}
			}
		}
		return result;
	}

	private List<Object[]> unwrapResultSet( ResultSet rs, AmazeType[] resultTypes )
	{
		List<Object[]> results = new ArrayList<Object[]>();
		try
		{
			RowSource<Object[]> rowSource = new ResultSetRowSource( rs, resultTypes );

			while ( rowSource.next() )
			{
				results.add( rowSource.get() );
			}
		}
		finally
		{
			try
			{
				rs.close();
			}
			catch ( SQLException e )
			{
				logger.error( "Failed to close result set", e );
			}
		}
		return results;
	}

	public void dropAllIndexes( List<Index> indexes ) throws DataSourceException
	{
		for ( Index index : indexes )
		{
			if ( !index.IsClustered )
			{
				List<String> sqlList = getDropIndexDDL( index, true );
				executeList( sqlList );
			}
		}
		for ( Index index : indexes )
		{
			if ( index.IsClustered )
			{
				List<String> sqlList = getDropIndexDDL( index, true );
				executeList( sqlList );
				break;
			}
		}
	}

	@Override
	public void dropAllIndexes( Table table ) throws DataSourceException
	{
		dropAllIndexes( table.Indexes );
	}

	public void applyIndexes( Table table, String indexLocation, boolean applyClustered ) throws DataSourceException
	{
		applyIndexes( table.Indexes, indexLocation, applyClustered );
	}

	@Override
	public abstract void dropStoredProcedure( String procName ) throws DataSourceException;

	@Override
	public abstract void applyStoredProcedure( String procName, String procedure ) throws DataSourceException;

	@Override
	public abstract List<String[]> executeStoredProcedure( String procName, List args ) throws DataSourceException;

	@Override
	public void attachSchema( Schema schema )
	{
		this.schema = schema;
	}

	@Override
	public void attachSchema( String doc )
	{
		Schema schema = new Schema();
		schema.loadSchema( doc );
		this.schema = schema;
	}

	@Override
	public void attachSchema( Document doc )
	{
		Schema schema = new Schema();
		schema.loadSchema( doc );
		this.schema = schema;
	}

	@Override
	public Schema getSchema()
	{
		return schema;
	}

	public abstract IdGenerator getIdGenerator();

	public abstract String getSelectString( String tableName, String[] colNames );

	public abstract String getSelectString( String tableName, String[] colNames, long min, long max );

	public abstract String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max );

	public abstract String getSqlToFetchNRows( String tableName, long maxRows );

	public abstract AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException;

	public abstract String getOrderByClause( List<Object[]> colNames, AmazeType[] resultTypes ) throws Exception;
	
	public void createTableEntry( Table eachTable, String tableType, String dataSourceType )
	{
		DateTime dttm = new DateTime();
		try{
//			HibernateSession.get( TableType.class, new Integer( 1 ) );
			TableType ttp = ( TableType ) HibernateSession.query( "from TableType ttp" /*where ttp.ttpName='" + tableType + "'"*/, new String[]{}, new Object[]{} );//, "ttpName", tableType );
			Datasource dts = ( Datasource ) HibernateSession.query( "from Datasource dts where dts.dtsType=:dtsType", new String[]{"dtsType"}, new Object[]{dataSourceType} ).get( 0 );
		}catch(Exception e){
			System.out.println();
			System.out.println(e);
		}
		List<Object> objectsToSave = new ArrayList<Object>();
		Tables table = HibernateSession.createObject( Tables.class );
		table.setTabName( eachTable.TableName );
		table.setTabPrefix( eachTable.TablePrefix );
		table.setTabDisplayName( eachTable.DisplayName );
//		table.setTableType( ttp );
//		table.setDatasource( dts );
		table.setTabCreatedDttm( dttm );
		table.setDeleteFl( false );
		table.setTabVersion( 1 );
		table.setPartitionId( 1 );
		objectsToSave.add( table );
		for ( Column eachColumn : eachTable.Columns )
		{
			Columns column = HibernateSession.createObject( Columns.class );
			column.setTables( table );
			column.setColumnName( eachColumn.ColumnName );
			column.setSequenceNo( eachColumn.SequenceNumber );
			column.setDataType( eachColumn.DataType.toString() );
			column.setLenght( eachColumn.Length );
			column.setIsMandatory( eachColumn.IsMandatory );
			column.setIsPrimaryKey( eachColumn.IsPrimaryKey );
			column.setIsOneToOneNestedObject( eachColumn.IsOneToOneNestedObject );
			column.setNestedObject( eachColumn.NestedObject );
			column.setColCreatedDttm( dttm );
			column.setDeleteFl( false );
			column.setColVersion( 1 );
			column.setPartitionId( 1 );
			objectsToSave.add( column );
		}
		for ( Index eachIndex : eachTable.Indexes )
		{
			Indexes index = HibernateSession.createObject( Indexes.class );
			index.setTables( table );
			index.setIndexName( eachIndex.IndexName );
			index.setIsUnique( eachIndex.IsUnique );
			index.setIsClustered( eachIndex.IsClustered );
			index.setIsBusinessConstraint( eachIndex.IsBusinessConstraint );
			index.setIsDisplayName( eachIndex.IsDisplayName );
			index.setColumnList( eachIndex.ColumnList );
			index.setIdxCreatedDttm( dttm );
			index.setDeleteFl( false );
			index.setVersionId( 1 );
			index.setPartitionId( 1 );
			objectsToSave.add( index );
		}
		HibernateSession.save( objectsToSave.toArray( new Object[objectsToSave.size()] ) );
	}
	
	@Override
	public void updateDBTableFromSchemaTable( Table newTable, Tables oldTable )
	{
		Session session = HibernateSession.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try{
			createTableEntry( newTable, "System", "System" );
			session.delete( oldTable );
			for( Columns eachCol : oldTable.getColumnss() )
				session.delete( eachCol );
			for( Indexes eachIdx : oldTable.getIndexess() )
				session.delete( eachIdx );
		}
		catch( Exception e)
		{
			tx.rollback();
			session.close();
			throw new AmazeException( " Could not do the table dfn updation for the table " + newTable.TableName );
		}
		tx.commit();
		session.close();
	}

}