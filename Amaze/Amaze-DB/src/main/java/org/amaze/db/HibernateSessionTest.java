package org.amaze.db;

import java.util.List;

import org.amaze.db.hibernate.objects.Application;
import org.amaze.db.hibernate.objects.UserRoleMap;
import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

	@SuppressWarnings( "unused" )
	public static void main( String[] args )
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		org.amaze.db.hibernate.utils.HibernateSession session = (org.amaze.db.hibernate.utils.HibernateSession) ctx.getBean( "hibernateSession" );
		try{
			Users user  = HibernateSession.get( Users.class, new Integer( 2 ) );
			user.getApplication();
			List<UserRoleMap> maps = user.getUserRoleMaps();
			user.setUsrName( "ChangedName" );
			HibernateSession.update( user );
			Users user1 = HibernateSession.createObject( Users.class );
			user1.setId( 3 );
			user1.setApplication( HibernateSession.get( Application.class, 1 ) );
			user1.setDeleteFl( true );
			user1.setUsrCreatedDttm( new DateTime() );
			user1.setUsrDisabled( false );
			user1.setUsrName( "ChangedName1" );
			user1.setUsrVersion( 23 );
			HibernateSession.save( user1 );
			HibernateSession.find( "from Users" );
			HibernateSession.query( "from Users where usrId = :usrId", "usrId", 2 );
			HibernateSession.queryExpectExactlyOneRow( "from Users where usrId = :usrId", "usrId", 2 );
			HibernateSession.queryExpectOneRow( "from Users where usrId = :usrId", "usrId", 2 );
			HibernateSession.queryExpectExactlyOneRow( "from Users where usrName = :usrName", "usrName", "ChangedName" );
			HibernateSession.queryExpectOneRow( "from Users where usrName = :usrName", "usrName", "ChangedName" );
			System.out.println("Done");
		}catch(Exception e){
			System.out.println(e.getMessage());
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