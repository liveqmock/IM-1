package org.amaze.commons.objects;

import java.util.Arrays;

public class MultiKey
{
	private Object[] key;

	public MultiKey( Object[] key )
	{
		this.key = key;
	}

	public Object get( int index )
	{
		return this.key[index];
	}

	public Object[] getKey()
	{
		return this.key;
	}

	public int hashCode()
	{
		return Arrays.hashCode( this.key );
	}

	public boolean equals( Object obj )
	{
		if ( obj == null )
			return false;
		if ( !( obj instanceof MultiKey ) )
		{
			return false;
		}
		MultiKey from = ( MultiKey ) obj;

		return Arrays.equals( this.key, from.key );
	}
}