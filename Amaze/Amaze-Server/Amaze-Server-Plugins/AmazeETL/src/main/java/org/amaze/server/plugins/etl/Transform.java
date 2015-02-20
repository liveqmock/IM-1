package org.amaze.server.plugins.etl;

import java.io.Serializable;
import java.util.Map;

public interface Transform<T, V> extends Serializable
{

	public V transform( T value );
	
	public void setJobParams( Map<String, Object> params );

	public Map<String, Object> getJobParams();
	
}
