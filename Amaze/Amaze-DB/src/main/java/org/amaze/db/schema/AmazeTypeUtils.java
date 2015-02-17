package org.amaze.db.schema;

import java.math.BigDecimal;

import org.amaze.commons.constants.AmazeConstants;
import org.amaze.db.installer.exceptions.AmazeInstallerException;
import org.joda.time.DateTime;

public class AmazeTypeUtils
{
    public static Class amazeTypeToResultSetClass( AmazeType type )
    {
        switch (type)
        {
        case Long:
            return Long.class;
        case Int:
            return Integer.class;
        case String:
            return String.class;
        case DateTime:
            return DateTime.class;
        case Bool:
            return Boolean.class;
        case Decimal:
            return BigDecimal.class;
        case Text:
            return String.class;
        default:
            throw new IllegalArgumentException( "Invalid type passed " + type );
        }
    }

    public static Class amazeTypeToFunctionClass( AmazeType type )
    {
        switch (type)
        {
        case Long:
            return Long.class;
        case Int:
            return Integer.class;
        case String:
            return String.class;
        case DateTime:
            return DateTime.class;
        case Bool:
            return Boolean.class;
        case Decimal:
            return BigDecimal.class;
        case Text:
            return String.class;
        default:
            throw new IllegalArgumentException( "Invalid type passed " + type );
        }
    }

    public static Class typeStringToResultSetClass( String type )
    {
        if ( type.equals( AmazeConstants.DT_LONG ) )
            return Long.class;
        if ( type.equals( AmazeConstants.DT_INT ) )
            return Integer.class;
        if ( type.equals( AmazeConstants.DT_STRING ) )
            return String.class;
        if ( type.equals( AmazeConstants.DT_DATE ) )
            return DateTime.class;
        if ( type.equals( AmazeConstants.DT_DATETIME ) )
            return DateTime.class;
        if ( type.equals( AmazeConstants.DT_BOOL ) )
            return String.class;
        if ( type.equals( AmazeConstants.DT_DECIMAL ) )
            return Long.class;

        throw new IllegalArgumentException( "Invalid type string passed " + type );
    }

    public static AmazeType fieldClassToAmazeType( Class clazz )
    {
        if ( clazz.equals( Integer.class ) || clazz.equals( Integer.TYPE ) )
            return AmazeType.Int;

        if ( clazz.equals( String.class ) )
            return AmazeType.String;

        if ( clazz.equals( DateTime.class ) )
            return AmazeType.DateTime;

        if ( clazz.equals( Boolean.TYPE ) )
            return AmazeType.Bool;

        if ( clazz.equals( Long.class ) || clazz.equals( Long.TYPE ) )
            return AmazeType.Long;

        if ( clazz.equals( BigDecimal.class ) )
            return AmazeType.Decimal;

        throw new IllegalArgumentException( "Invalid class passed " + clazz.getName() );
    }

    public static AmazeType typeStringToAmazeType( String type )
    {
        if ( type.equals( AmazeConstants.DT_LONG ) )
            return AmazeType.Long;
        if ( type.equals( AmazeConstants.DT_INT ) )
            return AmazeType.Int;
        if ( type.equals( AmazeConstants.DT_STRING ) )
            return AmazeType.String;
        if ( type.equals( AmazeConstants.DT_DATE ) )
            return AmazeType.DateTime;
        if ( type.equals( AmazeConstants.DT_DATETIME ) )
            return AmazeType.DateTime;
        if ( type.equals( AmazeConstants.DT_BOOL ) )
            return AmazeType.Bool;
        if ( type.equals( AmazeConstants.DT_DECIMAL ) )
            return AmazeType.Decimal;

        throw new IllegalArgumentException( "Invalid type string passed " + type );
    }
    
    public static Object getCorrectTypedValue( String value, String dataType )
	{
    	if( value == null )
    		return null;
		if( dataType.equals( "java.lang.Boolean" ) )
			return Boolean.valueOf( value );
		else if( dataType.equals( "java.lang.String" ) )
			return String.valueOf( value );
		else if( dataType.equals( "java.lang.Integer" ) )
			return Integer.valueOf( value );
		else if( dataType.equals( "java.lang.Long" ) )
			return Long.valueOf( value );
		else if( dataType.equals( "org.joda.time.DateTime" )){
			if( value.equals( "now" ) )
				return new DateTime();
			else
				return new DateTime( value );
		}
		throw new AmazeInstallerException( "Installer exception for getting the datatype class from amaze for Type " + dataType.toString()  );
	}

	public static String getCompleteClassNameForAmazeType( AmazeType dataType )
	{
		if( dataType.equals( AmazeType.String ))
			return "java.lang.String";
		else if( dataType.equals( AmazeType.Bool ) )
			return "java.lang.Boolean";
		else if( dataType.equals( AmazeType.Int ) )
			return "java.lang.Integer";
		else if( dataType.equals( AmazeType.Long ) )
			return "java.lang.Long";
		else if( dataType.equals( AmazeType.Decimal ) )
			return "java.lang.Long";
		else if( dataType.equals( AmazeType.Text ) )
			return "java.lang.String";
		else if( dataType.equals( AmazeType.DateTime ))
			return "org.joda.time.DateTime";
		else if( dataType.equals( AmazeType.Unknown ) )
			throw new AmazeInstallerException( "Unknown Type configured in the Seed for the Type " + dataType.toString() );
		throw new AmazeInstallerException( "Installer exception for getting the datatype class from amaze for Type " + dataType.toString()  );
	}
}
