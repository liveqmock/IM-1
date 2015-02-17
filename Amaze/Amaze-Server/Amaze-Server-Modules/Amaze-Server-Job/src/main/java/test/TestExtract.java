package test;

import java.util.List;

import org.amaze.db.hibernate.objects.Stream;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.server.jobs.templates.etl.Extract;

public class TestExtract implements Extract<Stream>
{

	@Override
	public List<Stream> extract()
	{
		return HibernateSession.getAllObjects( Stream.class );
	}

}
