package org.amaze.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HibernateSession {

	private static final Logger logger = LogManager.getLogger(HibernateSession.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		Session session = ((SessionFactory) ctx.getBean("sessionFactory"))
				.openSession();
		SQLQuery q = session
				.createSQLQuery("SELECT TABLE_NAME, COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, IS_NULLABLE FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS WHERE TABLE_NAME NOT LIKE 'SYSTEM_%'");
		q.list();
		ctx.registerShutdownHook();
		ctx.close();
	}

}
