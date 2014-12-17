package org.amaze.db;

import javax.sql.DataSource;

import org.amaze.db.usage.objects.LoginEvent;
import org.amaze.db.usage.utils.UsageSession;
import org.apache.cassandra.cql.jdbc.CassandraDataSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class CassandraTest
{

	public static void main( String[] args )
	{
		DataSource ds = new CassandraDataSource( "127.0.0.1", 9042, "amaze", "amaze", "amaze", "3.0.0" );
		
		Long st = System.currentTimeMillis();
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		System.out.println( System.currentTimeMillis() - st );
		CassandraOperations cassandraOperations = ctx.getBean( "cqlTemplate", CassandraOperations.class );ctx.getBean( "systemUsageDataSource" );
		Insert insert1 = QueryBuilder.insertInto( "test" ).value( "test_name", "Name1" ).value( "test_code", "Code1" ).value( "KEY", "1" );
		cassandraOperations.execute( insert1 );
//		LoginEventRepository loginEventRepository = ( LoginEventRepository ) ctx.getBean( "loginEventRepository" );
//		loginEventRepository.findAll();
		ctx.registerShutdownHook();
		ctx.close();

//		NOSQL Test Without Springs
		Cluster cluster = Cluster.builder().addContactPoints( "127.0.0.1" ).withPort( 9042 ).build();
		Session session = cluster.connect( "amaze" );
		CassandraOperations cassandraOps = new CassandraTemplate( session );
		Select s = QueryBuilder.select().from( "test" );
		cassandraOps.query( s );
		cassandraOps.truncate( "test" );
		UsageSession sess = (UsageSession) ctx.getBean( "" );
		UsageSession.get( LoginEvent.class, new Integer ( 150 ) );
	}

}
