package org.amaze.server.jobs.templates.etl;

import java.io.Serializable;

public interface Transform<T, V> extends Serializable
{

	public V transform( T value );
	
}
