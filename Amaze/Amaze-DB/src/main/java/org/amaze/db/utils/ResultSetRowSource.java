package org.amaze.db.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Arrays;

import org.amaze.commons.exceptions.AmazeException;
import org.amaze.db.hibernate.types.DateTimeType;
import org.amaze.db.hibernate.types.DecimalType;
import org.amaze.db.schema.AmazeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResultSetRowSource implements RowSource<Object[]>
{
	private final static Logger logger = LogManager.getLogger( ResultSetRowSource.class );
	
	private ResultSet rs;
	private AmazeType[] resultTypes;

	public ResultSetRowSource( ResultSet rs, AmazeType[] resultTypes )
	{
		this.rs = rs;
		this.resultTypes = resultTypes;
		normaliseResultTypes();
		try
		{
			int columnCount = rs.getMetaData().getColumnCount();
	        if ( resultTypes.length != columnCount )
	            throw new SQLException( "The query returned " + columnCount + " columns when " + resultTypes.length + " was expected" );
	        for ( int i = 0; i < resultTypes.length; i++ )
	        {
	            AmazeType amazeType = resultTypes[i];
	            int columnType = rs.getMetaData().getColumnType( i + 1 );
	            switch ( amazeType )
	            {
	                case Long:
	                    if ( columnType != Types.BIGINT && columnType != Types.INTEGER && columnType != Types.NUMERIC )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                case Int:
	                    if ( columnType != Types.INTEGER && columnType != Types.NUMERIC )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                case String:
	                    if ( columnType != Types.VARCHAR && columnType != Types.CHAR )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                case DateTime:
	                    if ( columnType != Types.TIMESTAMP && columnType != Types.DATE )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                case Bool:
	                    if ( columnType != Types.CHAR && columnType != Types.VARCHAR )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                case Decimal:
	                    if ( columnType != Types.BIGINT && columnType != Types.NUMERIC )
	                        throw new SQLException( "For table column " + rs.getMetaData().getColumnName( i + 1 ) + " expecting data type " + rs.getMetaData().getColumnTypeName( i + 1 ) + " but found " + amazeType
	                                + " as output data type ." );
	                    break;
	                default:
	                    logger.error( "Invalid type in configured output that is :" + amazeType );
	                    throw new SQLException( "Invalid spark type passed " + amazeType );
	            }
	        }

		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
	}

	private void normaliseResultTypes()
	{
		try
		{
			if ( resultTypes == null )
			{
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				resultTypes = new AmazeType[resultSetMetaData.getColumnCount()];
				Arrays.fill( this.resultTypes, AmazeType.Unknown );
			}
			for ( int i = 0; i < resultTypes.length; i++ )
			{
				if ( resultTypes[i] == AmazeType.Unknown )
				{
					resultTypes[i] = AmazeType.jdbcTypeToCompatibleAmazeType( rs, i + 1 );
				}
			}
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
	}

	public boolean next()
	{
		try
		{
			return rs.next();
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
	}

	public Object[] get()
	{
		Object[] row = new Object[resultTypes.length];
		try
		{
			for ( int i = 0; i < row.length; i++ )
			{
				AmazeType amazeType = resultTypes[i];
				switch( amazeType )
				{
				case Long:
					row[i] = rs.getLong( i + 1 );
					if ( rs.wasNull() )
						row[i] = null;
					break;
				case Int:
					row[i] = rs.getInt( i + 1 );
					if ( rs.wasNull() )
						row[i] = null;
					break;
				case String:
					row[i] = rs.getString( i + 1 );
					break;
				case DateTime:
					row[i] = DateTimeType.nullSafeGet( rs, i + 1 );
					break;
				case Bool:
					row[i] = ( rs.getString( i + 1 ) == null ? null : ( Object ) rs.getString( i + 1 ).equals( "Y" ) );
					break;
				case Decimal:
					row[i] = DecimalType.nullSafeGet( rs, i + 1 );
					break;
				default:
					throw new AmazeException( "Invalid amaze type '%1'", amazeType );
				}
			}
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}
		return row;
	}

	public void beforeFirst()
	{
		try
		{
			rs.beforeFirst();
		}
		catch ( SQLException e )
		{
			throw new AmazeException( e );
		}

	}
}
