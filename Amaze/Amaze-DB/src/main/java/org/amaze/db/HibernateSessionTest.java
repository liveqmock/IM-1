package org.amaze.db;

import java.util.List;

import org.amaze.db.hibernate.objects.Application;
import org.amaze.db.hibernate.objects.UserRoleMap;
import org.amaze.db.hibernate.objects.Users;
import org.amaze.db.hibernate.utils.HibernateSession;
import org.joda.time.DateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HibernateSessionTest
{

	@SuppressWarnings( "unused" )
	public static void main( String[] args )
	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		HibernateSession session = ( org.amaze.db.hibernate.utils.HibernateSession ) ctx.getBean( "hibernateSession" );
		try
		{
			Users user = HibernateSession.get( Users.class, new Integer( 1 ) );
			user.getAppIdApplication();
			List<UserRoleMap> maps = user.getUserRoleMaps();
			user.setUsrName( "ChangedName" );
			HibernateSession.update( user );
			Users user1 = HibernateSession.createObject( Users.class );
			user1.setId( 3 );
			user1.setAppIdApplication( HibernateSession.get( Application.class, 1 ) );
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
			System.out.println( "Done" );
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage() );
		}

		//		Hibernate Test On HSQLDB
		//		Session session1 = ((SessionFactory) ctx.getBean("sessionFactory")).openSession();
		//		SQLQuery q = session1.createSQLQuery("SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%'");
		//		q.list();

		ctx.registerShutdownHook();
		ctx.close();
	}

}