package org.amaze.server.jobs.templates.etl;

import java.io.Serializable;
import java.util.List;

public interface Extract<T> extends Serializable
{

	public List<T> extract();
	
}
