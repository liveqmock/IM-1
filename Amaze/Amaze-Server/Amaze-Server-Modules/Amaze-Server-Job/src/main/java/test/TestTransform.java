package test;

import org.amaze.db.hibernate.objects.Stream;
import org.amaze.server.jobs.templates.etl.Transform;

public class TestTransform implements Transform<Stream, Stream>
{
	
	@Override
	public Stream transform( Stream value )
	{
		System.out.println( value.getId() + value.getStmName() );
		value.setStmName( value.getStmName() + "Test" );
		return value;
	}

}
