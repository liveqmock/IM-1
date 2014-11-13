package org.amaze.db.utils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Schema;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.hibernate.SessionFactory;
import org.w3c.dom.Document;

public interface DataSource
{

	public String getDatabase();
	
	public String getDatabaseType();
	
	public String getDataSourceName();
	
	public SessionFactory getSessionFactory();
	
	public javax.sql.DataSource getDataSource();
	
	public Connection getConnection();

	public void close();
	
	public List<String> getTableNames( String database ) throws DataSourceException;

	public List<String> getTableNames( String database, String tablePattern ) throws DataSourceException;

	public void truncateTable( String database, String tableName ) throws DataSourceException;

	public void createTable( Table table, String dataLocation, String indexLocation, boolean createIndexes ) throws DataSourceException;

	public void dropTable( Table table ) throws DataSourceException;

	public void recreateTable( Table table, String dataLocation, String indexLocation, Boolean createIndexes ) throws DataSourceException;

	public void updateTable( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException;

	public void updateIndex( Index oldIndex, Index newIndex, String indexLocation ) throws DataSourceException;

	public void renameTable( String oldTableName, String newTableName ) throws DataSourceException;

	public void copyTable( Table oldTable, String newTableName ) throws DataSourceException;

	public boolean tableExists( String tableName ) throws DataSourceException;

	public void dropAllIndexes( Table table ) throws DataSourceException;

	public void applyIndexes( Table table, String indexLocation ) throws DataSourceException;

	public void applyIndex( Index index, String indexLocation ) throws DataSourceException;

	public void applyIndexes( String indexLocation ) throws DataSourceException;

	public void dropStoredProcedure( String procName ) throws DataSourceException;

	public void applyStoredProcedure( String procName, String procedure ) throws DataSourceException;

	public List<String[]> executeStoredProcedure( String procName, List args ) throws DataSourceException;

	public void attachSchema( Schema schema );
	
	public void attachSchema( String doc );
	
	public void attachSchema( Document doc );

	public Schema getSchema();

	public IdGenerator getIdGenerator();

	public String getSelectString( String tableName, String[] colNames );
	
	public String getSelectString( String tableName, String[] colNames, long min, long max );

	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max );
	
	public String getSqlToFetchNRows( String tableName, long maxRows );

//	public AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException;

	public String getOrderByClause( List<Object[]> colNames, AmazeType[] resultTypes ) throws Exception;

}
