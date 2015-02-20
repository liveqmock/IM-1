package org.amaze.server.plugins.etl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Load<T> extends Serializable
{

	public Boolean load( List<T> values );
	
	public void setJobParams( Map<String, Object> params );

	public Map<String, Object> getJobParams();
	
}
