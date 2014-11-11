package org.amaze.db.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Schema;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.hibernate.cfg.Environment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public interface DataSource
{
	public static final String BCP_FIELD_DELIMITER = "\t";

	public static final String NEW_LINE = System.getProperty( "line.separator" );

	public final static String DATASOURCE = "datasource";
	public final static String DATABASE = "database";
	public final static String UNICODE = "unicode";

	public final static String USERNAME = Environment.USER;
	public final static String PASSWORD = Environment.PASS;
	public final static String JDBC_URL = Environment.URL;
	public final static String DRIVER_CLASS = Environment.DRIVER;

	public DatabaseType getDatabaseType();

	public Connection getConnection();

	public void close();

	public BulkLoader createBulkLoader( Table table );

	public BulkLoader createBulkLoader( Table table, TaskStateListener dataLoadTaskListener );

	public BulkLoadRowWriter createBulkLoadRowWriter( File file, AmazeType[] types );

	public BulkLoadRowWriter createBulkLoadRowWriter( File file, AmazeType[] types, boolean append );

	public DateTimeFormatter getBcpDateFormatter();

	public DateTimeFormatter getBcpDateTimeFormatter();

	public String getSqlDateConstant( DateTime dateTime );

	public Charset getBcpCharset();

	public String bcpIn( String tableName, String filename, String delimiter, Table table ) throws DataSourceException;

	public String bcpOut( String tableName, String filename, String delimiter, Table table ) throws DataSourceException;

	public String getUniqueConnectionId( Connection connection ) throws DataSourceException;

	public List<String> getTableNames( String database ) throws DataSourceException;

	public List<String> getTableNames( String database, String tablePattern ) throws DataSourceException;

	public void truncateTable( String database, String tableName ) throws DataSourceException;

	public void createTable( Table table, String dataLocation, String indexLocation ) throws DataSourceException;

	public void createTable( Table table, String dataLocation, String indexLocation, boolean createIndexes ) throws DataSourceException;

	public void dropTable( Table table ) throws DataSourceException;

	public void dropTableSilent( Table table );

	public void dropTableSilentInSameSession( Table table, Connection jdbcSession );

	public void recreateTable( Table table, String dataLocation, String indexLocation ) throws DataSourceException;

	public void dropTableWarn( Table table );

	public void updateTable( Table oldTable, Table newTable, String dataLocation ) throws DataSourceException;

	public void updateIndex( Index oldIndex, Index newIndex, String indexLocation ) throws DataSourceException;

	public void renameTable( String oldTableName, String newTableName ) throws DataSourceException;

	public void copyTable( Table oldTable, String newTableName ) throws DataSourceException;

	public boolean tableExists( String tableName ) throws DataSourceException;

	public void dropAllIndexes( Table table ) throws DataSourceException;

	public void applyIndexes( Table table, String indexLocation ) throws DataSourceException;

	public void applyIndex( Index index, String indexLocation ) throws DataSourceException;

	public void dropAllIndexes() throws DataSourceException;

	public void applyIndexes( String indexLocation ) throws DataSourceException;

	public void updateNextNumber( Table table, String objectName ) throws DataSourceException;

	public void updateNextNumber( String tableName, String primaryKeyColumn, String objectName ) throws DataSourceException;

	public void updateAllNextNumber() throws DataSourceException;

	public void dropStoredProcedure( String procName ) throws DataSourceException;

	public void applyStoredProcedure( Map<String, String> map, String procName, String macro ) throws DataSourceException;

	public List<String[]> executeStoredProcedure( String procName, List args ) throws DataSourceException;

	public String getDataSourceName();

	public String getDatabase();

	public void attachSchema( Schema schema );

	public Schema getSchema();

	public Schema getSchema( String database, String table ) throws DataSourceException;

	public Schema getSchema( String database ) throws DataSourceException;

	public IdGenerator getIdGenerator();

	public String getSelectString( String tableName, String[] colNames );

	public String getSqlToFetchNRows( String tableName, long maxRows );

	public String getSelectString( String tableName, String[] colNames, String orderByClause, long min, long max );

	// escapes a string parameter to avoid unwanted wild card matching sequences - '%' and '_' should be passed through untouched
	public String escapeString( String s );

	public AmazeType externalDBTypeToAmazeType( String dbType, int precision, int scale, boolean ignoreError ) throws DataSourceException;

	public String getOrderByClause( List<Object[]> colNames, AmazeType[] resultTypes ) throws Exception;

	public String getSelectString( String tableName, String[] colNames, long min, long max );

	public String getUnicodeCharset();
}
