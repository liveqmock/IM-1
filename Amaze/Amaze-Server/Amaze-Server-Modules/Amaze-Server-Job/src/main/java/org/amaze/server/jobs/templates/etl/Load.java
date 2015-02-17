package org.amaze.server.jobs.templates.etl;

import java.io.Serializable;
import java.util.List;

public interface Load<T> extends Serializable
{

	public Boolean load( List<T> values );
	
}
