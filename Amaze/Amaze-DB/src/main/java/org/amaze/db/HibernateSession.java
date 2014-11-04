package org.amaze.db;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HibernateSession {
	
	
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("db.xml");
		ctx.getBean("sessionFactory");
	}
	 

}
