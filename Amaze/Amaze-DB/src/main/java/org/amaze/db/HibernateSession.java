package org.amaze.db;

import java.net.UnknownHostException;

import org.amaze.db.hibernate.objects.Other;
import org.amaze.db.hibernate.objects.Test;
import org.amaze.db.usage.cassandra.repository.LoginEventRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

public class HibernateSession {

	@Autowired
	public static CassandraOperations cassandraOperations;
	
	@Autowired
	public static LoginEventRepository loginEventRepository;

	private static final Logger logger = LogManager.getLogger(HibernateSession.class);

	private static Cluster cluster;
	private static com.datastax.driver.core.Session session;

	public static void main(String[] args) throws UnknownHostException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		
		// NOSQL Test With springs
//		CassandraOperations cassandraOperations = ctx.getBean("cqlTemplate", CassandraOperations.class);
//		Insert insert1 = QueryBuilder.insertInto("test").value("test_name", "Name1").value("test_code", "Code1");
//		cassandraOperations.execute(insert1);
//		loginEventRepository = (LoginEventRepository) ctx.getBean("loginEventRepository");
//		loginEventRepository.findAll();

		// NOSQL Test Without Springs
//		cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9042).build();
//		session = cluster.connect("amaze");
//		CassandraOperations cassandraOps = new CassandraTemplate(session);
//		Select s = QueryBuilder.select().from("test");
//		cassandraOps.query(s);
//		cassandraOps.truncate("test");
		
		
		
//		HibernateSession session = (HibernateSession) ctx.getBean("hibernateSession");
//		session.loginEventRepository.count();

		// Hibernate Test On HSQL
		Session session1 = ((SessionFactory) ctx.getBean("sessionFactory")).openSession();
		SQLQuery q = session1.createSQLQuery("SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%'");
		q.list();
		session1.get( Other.class, new Integer( 1 )  );
		ctx.registerShutdownHook();
		ctx.close();
		
	}

}