package org.amaze.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KunderaTest
{
	
	public static void main( String[] args )
	{

		//Spring approach to get the EMFactory and EM
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext( "db.xml" );
		EntityManagerFactory emf = (EntityManagerFactory) ctx.getBean("cassandraEmf");
		EntityManager em = emf.createEntityManager();

		User user = new User();
		user.setUserId( 0002 );
		user.setFirstName( "SRP" );
		user.setLastName( "Lay" );
		user.setCity( "PIC" );

		// Non Spring approach to get EMFactory and EM 
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "cassandra" );
//		EntityManager em = emf.createEntityManager();

		em.persist( user );
		em.find( User.class, 0002 );
		em.close();
		emf.close();
	}

}
