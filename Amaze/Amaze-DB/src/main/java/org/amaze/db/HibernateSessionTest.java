package org.amaze.db;

import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.amaze.db.usage.repository.LoginEventRepository;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class HibernateSessionTest {

//	@Autowired
//	public static CassandraOperations cassandraOperations;
//	
//	@Autowired
//	public static LoginEventRepository loginEventRepository;
//
//	private static final Logger logger = LogManager.getLogger(HibernateSession.class);
//
//	private static Cluster cluster;
//	private static com.datastax.driver.core.Session session;
//
//	public static void main(String[] args) throws UnknownHostException {
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
//		
//		// NOSQL Test With springs
////		CassandraOperations cassandraOperations = ctx.getBean("cqlTemplate", CassandraOperations.class);
////		Insert insert1 = QueryBuilder.insertInto("test").value("test_name", "Name1").value("test_code", "Code1");
////		cassandraOperations.execute(insert1);
////		loginEventRepository = (LoginEventRepository) ctx.getBean("loginEventRepository");
////		loginEventRepository.findAll();
//
//		// NOSQL Test Without Springs
////		cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9042).build();
////		session = cluster.connect("amaze");
////		CassandraOperations cassandraOps = new CassandraTemplate(session);
////		Select s = QueryBuilder.select().from("test");
////		cassandraOps.query(s);
////		cassandraOps.truncate("test");
//		
//		
//		
////		HibernateSession session = (HibernateSession) ctx.getBean("hibernateSession");
////		session.loginEventRepository.count();
//
//		// Hibernate Test On HSQL
//		Session session1 = ((SessionFactory) ctx.getBean("sessionFactory")).openSession();
//		SQLQuery q = session1.createSQLQuery("SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%'");
//		q.list();
//		session1.get( Other.class, new Integer( 1 )  );
//		ctx.registerShutdownHook();
//		ctx.close();
//		
//	}
	
	public static void main( String[] args )
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		org.amaze.db.hibernate.utils.HibernateSession session = (org.amaze.db.hibernate.utils.HibernateSession) ctx.getBean( "hibernateSession" );
//		org.amaze.db.hibernate.utils.HibernateSession.get( User.class, new Integer( 1 ) );
		try{
			Users user  = HibernateSession.get( Users.class, new Integer( 1 ) );
		}catch(Exception e){
			System.out.println(e);
		}
		ctx.registerShutdownHook();
		ctx.close();
	}

	/*public static void main( String[] args )
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		CassandraOperations cassandraOperations = ctx.getBean("cqlTemplate", CassandraOperations.class);
		Insert insert1 = QueryBuilder.insertInto("test").value("test_name", "Name1").value("test_code", "Code1");
		cassandraOperations.execute(insert1);
		LoginEventRepository loginEventRepository = (LoginEventRepository) ctx.getBean("loginEventRepository");
		loginEventRepository.findAll();
	}*/
}