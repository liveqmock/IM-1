package org.amaze.db;

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
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		CassandraOperations cassandraOperations = ctx.getBean( "cqlTemplate", CassandraOperations.class );
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
	}

}
