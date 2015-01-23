package org.amaze.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.amaze.db.usage.objects.User;
import org.amaze.db.usage.objects.LoginEvent;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KunderaTest
{

	public static void main( String[] args )
	{

		//Spring approach to get the EMFactory and EM
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		EntityManagerFactory emf = ( EntityManagerFactory ) ctx.getBean( "systemusage" );
		EntityManager em = emf.createEntityManager();
		

		org.amaze.db.usage.objects.User user = new org.amaze.db.usage.objects.User();
		user.setUserId( 0002 );
		user.setFirstName( "123456" );
		user.setLastName( "123456" );
		user.setCity( "123456" );

		LoginEvent event = new LoginEvent();
		event.setLetAccessClient( "" );
		event.setLetCreatedDttm( new DateTime() );
		//		event.setLetId( 12345 );
		event.setLetLoggedDttm( new DateTime() );
		event.setLetLoggoffDttm( new DateTime() );
		event.setLetSessionTerminated( "true" );
		event.setPtnId( 1 );
		event.setUsrId( 1 );

		// Non Spring approach to get EMFactory and EM 
		//		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "systemusage" );
		//		EntityManager em = emf.createEntityManager();

		em.persist( event );
		em.find( LoginEvent.class, 750 );
		Query q = em.createQuery( "select let from LoginEvent let where let.letId = 150" );
		List<LoginEvent> rules = q.getResultList();
		em.persist( user );

		em.find( User.class, 0002 );
		em.close();
		emf.close();
	}

}
