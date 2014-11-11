package org.amaze.db.schema;

import java.math.BigDecimal;

import org.amaze.commons.constants.AmazeConstants;
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
}
