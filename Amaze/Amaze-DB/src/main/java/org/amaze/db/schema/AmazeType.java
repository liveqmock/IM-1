package org.amaze.db.schema;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public enum AmazeType
{
	Long, Int, String, DateTime, Bool, Decimal, Text, Unknown;

	public int toJDBCType()
	{
		switch( this )
		{
		case Long:
			return Types.BIGINT;

		case Int:
			return Types.INTEGER;

		case String:
			return Types.VARCHAR;

		case DateTime:
			return Types.TIMESTAMP;

		case Bool:
			return Types.CHAR;

		case Decimal:
			return Types.BIGINT;

		case Text:
			return Types.LONGVARCHAR;

		default:
			throw new IllegalStateException( "Can't convert Amaze type to jdbc type for " + this );

		}
	}

	public static AmazeType typeof( Object obj )
	{
		if ( obj == null )
			return AmazeType.Unknown;

		if ( obj instanceof String )
			return AmazeType.String;

		if ( obj instanceof org.joda.time.DateTime )
			return AmazeType.DateTime;

		if ( obj instanceof Boolean )
			return AmazeType.Bool;

		if ( obj instanceof Long )
			return AmazeType.Long;

		if ( obj instanceof Integer )
			return AmazeType.Int;

		if ( obj instanceof BigDecimal )
			return AmazeType.Decimal;

		return AmazeType.Unknown;
	}

	public static AmazeType typeofString( String type )
	{
		if ( type.equals( "Long" ) )
			return AmazeType.Long;
		if ( type.equals( "Integer" ) )
			return AmazeType.Int;
		if ( type.equals( "String" ) )
			return AmazeType.String;
		if ( type.equals( "Date" ) )
			return AmazeType.DateTime;
		if ( type.equals( "DateTime" ) )
			return AmazeType.DateTime;
		if ( type.equals( "Boolean" ) )
			return AmazeType.Bool;
		if ( type.equals( "Decimal" ) )
			return AmazeType.Decimal;
		throw new IllegalArgumentException( "Invalid type string passed " + type );
	}

	public static AmazeType jdbcTypeToCompatibleSparkType( ResultSet resultSet, int columnIndex ) throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();
		switch( metaData.getColumnType( columnIndex ) )
		{
		case Types.NUMERIC:
		{
			int precision = metaData.getPrecision( columnIndex );

			if ( precision <= 10 )
				return AmazeType.Int;

			return AmazeType.Long;
		}

		case Types.BIGINT:
			return AmazeType.Long;

		case Types.INTEGER:
			return AmazeType.Int;

		case Types.TIMESTAMP:
		case Types.DATE:
			return AmazeType.DateTime;

		case Types.VARCHAR:
			return AmazeType.String;

		case Types.CHAR:
			return AmazeType.Bool;

		default:
			return AmazeType.Unknown;
		}
	}

	public Column toColumn( Table table, String name )
	{
		if ( this == AmazeType.String )
		{
			return new Column( name, this, 255, false, table );
		}
		else
		{
			return new Column( name, this, false, table );
		}
	}

	public int getLength()
	{
		switch( this )
		{
		case String:
			return 255;
		case Long:
		case Int:
		case DateTime:
		case Bool:
		case Decimal:
		case Text:
			return 0;
		default:
			throw new IllegalStateException( "Can't convert amaze type to jdbc type" + this );

		}
	}

	public String toTableColumnType()
	{
		return toString().toLowerCase();
	}
}
