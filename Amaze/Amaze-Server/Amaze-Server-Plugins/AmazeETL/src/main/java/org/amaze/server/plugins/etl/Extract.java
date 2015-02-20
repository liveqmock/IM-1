package org.amaze.server.plugins.etl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Extract<T> extends Serializable
{

	public List<T> extract();

	public void setJobParams( Map<String, Object> params );

	public Map<String, Object> getJobParams();

}
