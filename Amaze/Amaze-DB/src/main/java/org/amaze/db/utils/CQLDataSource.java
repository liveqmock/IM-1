package org.amaze.db.utils;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.amaze.db.schema.AmazeType;
import org.amaze.db.schema.Column;
import org.amaze.db.schema.Index;
import org.amaze.db.schema.Table;
import org.amaze.db.utils.exceptions.DataSourceException;
import org.hibernate.SessionFactory;

public class CQLDataSource extends AbstractDataSource
{

	public CQLDataSource( String dataBase, String databaseType, String dataSourceName, SessionFactory sessionFactory, DataSource dataSource, Connection connection )
	{
		super( dataBase, databaseType, dataSourceName, sessionFactory, dataSource, connection );
	}

	@Override
	public void truncateTable( String database, String tableName ) throws DataSourceException
	{
		
	}

	@Override
	protected String getTableQuery( String database, String tablePattern, boolean exact )
	{
		return null;
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
		return null;
	}

	@Override
	protected String getCreateIndexStatement( Index index, String indexLocation )
	{
		return null;
	}

	@Override
	protected List<String> getDropTableDDL( Table table, boolean checkExists ) throws DataSourceException
	{
		return null;
	}

	@Override
	protected String getSelectDatabaseDDL( String databaseName ) throws DataSourceException
	{
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

}
