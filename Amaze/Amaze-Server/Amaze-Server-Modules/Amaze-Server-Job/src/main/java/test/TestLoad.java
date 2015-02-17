package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.amaze.db.hibernate.objects.Stream;
import org.amaze.server.jobs.templates.etl.Load;

public class TestLoad implements Load<Stream>
{

	@Override
	public Boolean load( List<Stream> values )
	{
		try
		{
			FileWriter w = new FileWriter( new File( "D:\\Result.txt" ), true );
			for ( Stream stream : values )
			{
				w.write( "Outputing the Data" + stream.getStmId() + stream.getStmName() + " StmName " );
				w.write( "\n" );
				w.flush();
			}
			w.close();
		}
		catch ( IOException e )
		{
			System.out.println( "Error " + e );
		}
		return null;
	}

}
