package org.amaze.db.hibernate.utils;

import org.amaze.commons.exceptions.AmazeException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface SessionWorkListener
{
    Object doSessionWork( Session session ) throws HibernateException, AmazeException;
}
